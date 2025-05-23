package com.hu.oneclick.server.user;

import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.RegisterUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
public interface UserService {

    /**
     * 用户注册
     * @param registerUser
     * @return
     */
    Resp<String> register(RegisterUser registerUser);

    /**
     * 修改密码
     * @param args
     * @return
     */
    Resp<String> modifyPassword(Map<String, String> args);
    /**
     * 重置密码
     * @param args
     * @return
     */
    Resp<String> resetPassword(Map<String, String> args);

    /**
     * 发送邮箱 验证码
     * @param email
     * @param prefix
     * @return
     */
    Resp<String> sendEmailCode(String email,String prefix);

    /**
     * 查询邮箱是否存在
     * @param email
     * @return
     */
    Resp<String> queryEmailDoesItExist(String email);

    /**
     * 更新用户信息
     * @param sysUser
     * @return
     */
    Resp<String> updateUserInfo(SysUser sysUser);

    /**
     * 查询用户信息
     * @return
     */
    Resp<SysUser> queryUserInfo();

    /**
     * 查询用户权限
     * @return
     */
    Resp<List<SysProjectPermissionDto>> queryUserPermissions();

    Resp<List<SubUserDto>> queryByNameSubUsers(String subUserName);


    /**
     * 删除用户
     * @param id
     * @return
     */
    Resp<String> deleteUserById(String id);


}
