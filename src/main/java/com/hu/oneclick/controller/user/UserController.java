package com.hu.oneclick.controller.user;

import com.hu.oneclick.common.constant.OneConstant;
import com.hu.oneclick.model.base.Resp;
import com.hu.oneclick.model.domain.SysUser;
import com.hu.oneclick.model.domain.dto.RegisterUser;
import com.hu.oneclick.model.domain.dto.SubUserDto;
import com.hu.oneclick.model.domain.dto.SysProjectPermissionDto;
import com.hu.oneclick.server.user.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author qingyang
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public Resp<String> register(@RequestBody RegisterUser registerUser) {
        return userService.register(registerUser);
    }

    @GetMapping("sendEmailRegisterCode")
    public Resp<String> sendEmailRegisterCode(@RequestParam String email) {
        return userService.sendEmailCode(email, OneConstant.REDIS_KEY_PREFIX.REGISTRY);
    }

    @PostMapping("modifyPassword")
    public Resp<String> modifyPassword(@RequestBody Map<String,String> args){
        return userService.modifyPassword(args);
    }

    @PostMapping("resetPassword")
    public Resp<String> resetPassword(@RequestBody Map<String,String> args){
        return userService.resetPassword(args);
    }

    @GetMapping("sendResetPasswordEmailUrl")
    public Resp<String> sendResetPasswordEmailCode(@RequestParam String email) {
        return userService.sendEmailCode(email,OneConstant.REDIS_KEY_PREFIX.RESET_PASSWORD);
    }

    @GetMapping("queryEmailDoesItExist")
    public Resp<String> queryEmailDoesItExist(@RequestParam String email) {
        return userService.queryEmailDoesItExist(email);
    }

    @PostMapping("updateUserInfo")
    public Resp<String> updateUserInfo(@RequestBody SysUser sysUser) {
        return userService.updateUserInfo(sysUser);
    }

    @GetMapping("queryUserInfo")
    public Resp<SysUser> queryUserInfo() {
        return userService.queryUserInfo();
    }

    @GetMapping("queryUserPermissions")
    public Resp<List<SysProjectPermissionDto>> queryUserPermissions() {
        return userService.queryUserPermissions();
    }

    /**
     * 获取子用户列表，下拉框使用
     * @param subUserName
     * @return
     */
    @PostMapping("queryByNameSubUsers")
    public Resp<List<SubUserDto>> queryByNameSubUsers(@RequestParam(required = false) String subUserName){
        return userService.queryByNameSubUsers(subUserName);
    }
}
