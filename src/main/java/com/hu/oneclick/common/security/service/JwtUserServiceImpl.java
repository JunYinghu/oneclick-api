package com.hu.oneclick.common.security.service;

import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.common.constant.TwoConstant;
import com.hu.oneclick.common.enums.SysConstantEnum;
import com.hu.oneclick.common.exception.BizException;
import com.hu.oneclick.common.security.JwtAuthenticationToken;
import com.hu.oneclick.dao.ProjectDao;
import com.hu.oneclick.dao.SysProjectPermissionDao;
import com.hu.oneclick.dao.SysUserDao;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.UserUseOpenProject;
import com.hu.oneclick.model.domain.dto.AuthLoginUser;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author qingyang
 */
@Service("jwtUserService")
public class JwtUserServiceImpl implements UserDetailsService {

	private final PasswordEncoder passwordEncoder;

	private final SysUserDao sysUserDao;

	private final SysProjectPermissionDao sysProjectPermissionDao;

	private final ProjectDao projectDao;


	private final RedissonClient redisClient;

	public JwtUserServiceImpl(SysUserDao sysUserDao, RedissonClient redisClient, SysProjectPermissionDao sysProjectPermissionDao, ProjectDao projectDao) {
		this.projectDao = projectDao;
		this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
		this.sysUserDao = sysUserDao;
		this.redisClient = redisClient;
		this.sysProjectPermissionDao = sysProjectPermissionDao;
	}

	public AuthLoginUser getUserLoginInfo(String username) {
		String salt = "123456ef";
    	//将salt放到password字段返回
		RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN  + username);
		AuthLoginUser authLoginUser = JSONObject.parseObject(bucket.get(), AuthLoginUser.class);
		if (authLoginUser == null) {
			throw new InsufficientAuthenticationException("token invalidation");
		}
		authLoginUser.setUsername(username);
		authLoginUser.setPassword(salt);
		return authLoginUser;
	}

	public AuthLoginUser getUserLoginInfo() {
		DecodedJWT token = ((JwtAuthenticationToken) SecurityContextHolder.getContext().getAuthentication()).getToken();
		//Get the userId from token claim.
		String username = token.getSubject();
		//将salt放到password字段返回
		RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN  + username);
		AuthLoginUser authLoginUser = JSONObject.parseObject(bucket.get(), AuthLoginUser.class);
		if (authLoginUser == null) {
			throw new InsufficientAuthenticationException("token invalidation");
		}
		return authLoginUser;
	}

	/**
	 * 验证用户是否存在
	 * @param username
	 * @return
	 */
	public boolean verifyUserExists(String username){
		RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN  + username);
		return bucket.get() != null;
	}


	public String getMasterId(){
		SysUser sysUser = getUserLoginInfo().getSysUser();
		if(sysUser.getManager().equals(OneConstant.PLATEFORM_USER_TYPE.MANAGER) || sysUser.getManager().equals(OneConstant.PLATEFORM_USER_TYPE.ORDINARY)){
			return sysUser.getId();
		}else if (StringUtils.isEmpty(sysUser.getParentId())){
			throw new InsufficientAuthenticationException("用户异常！");
		}
		return sysUser.getParentId();
	}

	public String getId(){
		return getUserLoginInfo().getSysUser().getId();
	}


	public String saveUserLoginInfo(AuthLoginUser user) {
		//正式开发时可以调用该方法实时生成加密的salt,BCrypt.gensalt
		String salt = "123456ef";
		Algorithm algorithm = Algorithm.HMAC256(salt);
		//设置1小时后过期
		Date date = new Date(System.currentTimeMillis()+3600*1000);
		//创建token
		String sign = JWT.create()
				.withSubject(user.getUsername())
				.withExpiresAt(date)
				.withIssuedAt(new Date())
				.sign(algorithm);
		String s = JSONObject.toJSONString(user);
		RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN + user.getUsername());
		if (bucket.isExists()){
			bucket.delete();
		}
		bucket.set(s);
		bucket.expire(1, TimeUnit.HOURS);
		return sign;
	}

	public void saveUserLoginInfo2(SysUser sysUser) {
		AuthLoginUser user = getUserLoginInfo();
		user.setSysUser(sysUser);
		String s = JSONObject.toJSONString(user);
		RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN + sysUser.getEmail());
		if (bucket.isExists()){
			bucket.delete();
		}
		bucket.set(s);
		bucket.expire(1, TimeUnit.HOURS);
	}

	@Override
	public AuthLoginUser loadUserByUsername(String username) throws UsernameNotFoundException {
		SysUser user = sysUserDao.queryByEmail(username);
		AuthLoginUser authLoginUser = new AuthLoginUser();
		if (user == null){
			throw new RuntimeException();
		}
		//子用户需要查询权限列表,并且需要裁剪邮箱用户名
		if (user.getManager().equals(OneConstant.USER_TYPE.SUB_USER)){
			user.setEmail(TwoConstant.subUserNameCrop(user.getEmail()));
			authLoginUser.setPermissions(sysProjectPermissionDao.queryBySubUserId(user.getId()));
		}
		//查询用户上一次打开的项目
		UserUseOpenProject userUseOpenProject = projectDao.queryUseOpenProject(user.getId());
		if (userUseOpenProject != null){
			user.setUserUseOpenProject(userUseOpenProject);
			user.setIsUseProject(1);
		}

		authLoginUser.setSysUser(user);
		authLoginUser.setUsername(username);
		authLoginUser.setPassword(user.getPassword());
		return authLoginUser;
	}

	/**
	 * 加密密码
	 * @param password
	 * @return
	 */
	public String encryptPassword(String password) {
		return passwordEncoder.encode(password);
	}

	public boolean verifyPassword(String password,SysUser sysUser){
		return passwordEncoder.matches(password,sysUser.getPassword());
	}

	public void deleteUserLoginInfo(String username) {
		RBucket<String> bucket = redisClient.getBucket(OneConstant.REDIS_KEY_PREFIX.LOGIN + username);
		bucket.delete();
		SecurityContextHolder.clearContext();
	}
}
