package com.madgod.library.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.madgod.library.common.Privilege;
import com.madgod.library.common.Result;
import com.madgod.library.entity.User;
import com.madgod.library.entity.operationTypeEnum;
import com.madgod.library.service.IUserService;
import com.madgod.library.utils.CurrentUserUtils;
import com.madgod.library.utils.DataExceptionUtils;
import com.madgod.library.utils.TokenUtils;
import jakarta.annotation.Resource;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author madgod
 * @since 2026-04-03
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    IUserService userService;

    @Resource
    OperationLogController  log;

    @PostMapping("/register")
    public Result<?> register(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        User res = userService.getOne(queryWrapper);
        if(res != null){return Result.error(400,"用户名已存在");}
        return save(user);
    }

    @PostMapping("/login")
    public Result<User> login(@RequestBody User user){
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername,user.getUsername());
        queryWrapper.eq(User::getPassword,user.getPassword());
        User res = userService.getOne(queryWrapper);
        if(res == null){return Result.error(400,"用户名或密码错误");}
        String token = TokenUtils.genToken(res);
        res.setToken(token);
        res.setPassword("");
        log.log(operationTypeEnum.LOGIN,"用户登录",res);
        return Result.success(res);
    }

    @GetMapping("/searchByPage")
    public Result<Page<User>> searchByPage(
             @RequestParam(defaultValue = "1") Integer pageNum,
             @RequestParam(defaultValue = "10") Integer pageSize,
             @RequestParam(defaultValue = "") String username,
             @RequestParam(defaultValue = "") String phone) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
            queryWrapper.like(User::getUsername,username);
        }
        if(StringUtils.isNotBlank(phone)){
            queryWrapper.like(User::getPhone,phone);
        }
        queryWrapper.orderByAsc(User::getId);   // 按编号排序
        Page<User> userPage = userService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(userPage);
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Integer id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error(404, "用户不存在");
        }
        return Result.success(user);
    }

    @PostMapping("save")
    @Privilege
    public Result<?> save(@RequestBody User user) {
        // handleSave 负责捕获异常
        return DataExceptionUtils.handleSave(() -> {
            boolean saved = userService.save(user);
            if(saved){
                User operator = CurrentUserUtils.getCurrentUser();
                log.log(operationTypeEnum.REGISTER,"用户注册,ID: "+user.getId(),operator);
                return Result.success();
            }
            else{return Result.error("新增用户失败");}
        });
    }

    @PutMapping("/update")
    @Privilege
    public Result<?> update(@RequestBody User user) {
        // 客户端必须传入用户的所有字段，全量更新用户信息
        boolean updated = userService.updateById(user);
        if (!updated) {
            return Result.error("更新用户失败或用户不存在");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.UPDATE_USER,"更新用户信息，ID："+user.getId(),operator);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Privilege
    public Result<?> delete(@PathVariable Integer id) {
        boolean removed = userService.removeById(id);
        if (!removed) {
            return Result.error("删除用户失败或用户不存在");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.DELETE_USER,"删除用户，ID："+id,operator);
        return Result.success();
    }

    @PostMapping("/deleteBatch")
    @Privilege
    public Result<?> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的用户ID");
        }

        boolean removed = userService.removeByIds(ids);
        if (!removed) {
            return Result.error("批量删除用户失败");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.DELETE_USER,"删除用户，ID："+ids,operator);
        return Result.success();
    }
}
