package com.madgod.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.madgod.library.common.Privilege;
import com.madgod.library.common.Result;
import com.madgod.library.entity.OperationLog;
import com.madgod.library.entity.User;
import com.madgod.library.entity.operationTypeEnum;
import com.madgod.library.service.IOperationLogService;
import com.madgod.library.utils.DataExceptionUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operationLog")
public class OperationLogController {

    @Resource
    private IOperationLogService operationLogService;

    public Result<?> log(operationTypeEnum operationType, String msg, User user) {
        if(user == null){return Result.error(401, "未登录或会话已失效");}
        OperationLog log = new OperationLog();
        log.setUserId(user.getId());
        log.setUserName(user.getUsername());
        log.setOperationType(operationType.ordinal());
        log.setOperation(msg);
        return save(log);
    }

    @GetMapping("/searchByPage")
    @Privilege
    public Result<Page<OperationLog>> searchByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "") String userName,
            @RequestParam(defaultValue = "") Integer operationType,
            @RequestParam(defaultValue = "") String operation
    ) {
        LambdaQueryWrapper<OperationLog> qw = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            qw.like(OperationLog::getUserName, userName);
        }
        if (operationType != null) {
            qw.eq(OperationLog::getOperationType, operationType);
        }
        if (StringUtils.isNotBlank(operation)) {
            qw.like(OperationLog::getOperation, operation);
        }
        qw.orderByDesc(OperationLog::getTimestamp);
        Page<OperationLog> page = operationLogService.page(new Page<>(pageNum, pageSize), qw);
        return Result.success(page);
    }

    @GetMapping("/{id}")
    @Privilege
    public Result<OperationLog> getById(@PathVariable Integer id) {
        OperationLog log = operationLogService.getById(id);
        if (log == null) {
            return Result.error(404, "操作日志不存在");
        }
        return Result.success(log);
    }

    @PostMapping("/save")
    @Privilege
    public Result<?> save(@RequestBody OperationLog log) {
        return DataExceptionUtils.handleSave(() -> {
            boolean saved = operationLogService.save(log);
            return saved ? Result.success() : Result.error("新增日志失败");
        });
    }

    @PutMapping("/{id}")
    @Privilege
    public Result<?> update(@RequestBody OperationLog log) {
        boolean updated = operationLogService.updateById(log);
        if (!updated) {
            return Result.error("更新日志失败或日志不存在");
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Privilege
    public Result<?> delete(@PathVariable Integer id) {
        boolean removed = operationLogService.removeById(id);
        if (!removed) {
            return Result.error("删除日志失败或日志不存在");
        }
        return Result.success();
    }

    @PostMapping("/deleteBatch")
    @Privilege
    public Result<?> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的日志ID");
        }
        boolean removed = operationLogService.removeByIds(ids);
        if (!removed) {
            return Result.error("批量删除日志失败");
        }
        return Result.success();
    }
}
