package com.madgod.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.madgod.library.common.Privilege;
import com.madgod.library.common.Result;
import com.madgod.library.entity.Borrow;
import com.madgod.library.entity.User;
import com.madgod.library.entity.operationTypeEnum;
import com.madgod.library.service.IBorrowService;
import com.madgod.library.utils.CurrentUserUtils;
import com.madgod.library.utils.DataExceptionUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 借阅表 前端控制器
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@RestController
@RequestMapping("/borrow")
public class BorrowController {

    @Resource
    private IBorrowService borrowService;

    @Resource
    private OperationLogController log;

    @GetMapping("/searchByPage")
    public Result<Page<Borrow>> searchByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "") String userName,
            @RequestParam(defaultValue = "") String bookTitle,
            @RequestParam(required = false) Integer status) {
        LambdaQueryWrapper<Borrow> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(userName)) {
            queryWrapper.like(Borrow::getUserName, userName);
        }
        if (StringUtils.isNotBlank(bookTitle)) {
            queryWrapper.like(Borrow::getBookTitle, bookTitle);
        }
        if (status != null) {
            queryWrapper.eq(Borrow::getStatus, status);
        }
        queryWrapper.orderByDesc(Borrow::getBorrowTime);
        Page<Borrow> borrowPage = borrowService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(borrowPage);
    }

    @GetMapping("/{id}")
    public Result<Borrow> getById(@PathVariable Integer id) {
        Borrow borrow = borrowService.getById(id);
        if (borrow == null) {
            return Result.error(404, "借阅记录不存在");
        }
        return Result.success(borrow);
    }

    @PostMapping("/save")
    @Privilege
    public Result<?> save(@RequestBody Borrow borrow) {
        return DataExceptionUtils.handleSave(() -> {
            boolean saved = borrowService.save(borrow);
            if (!saved) {
                return Result.error("新增借阅记录失败");
            }
            User operator = CurrentUserUtils.getCurrentUser();
            log.log(operationTypeEnum.BORROW_BOOK, "新增借阅记录，ID：" + borrow.getId() + " 状态: " + borrow.getStatus(), operator);
            return Result.success();
        });
    }

    @PutMapping("/update")
    @Privilege
    public Result<?> update(@RequestBody Borrow borrow) {
        boolean updated = borrowService.updateById(borrow);
        if (!updated) {
            return Result.error("更新借阅记录失败或借阅记录不存在");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.RETURN_BOOK, "更新借阅记录，ID：" + borrow.getId(), operator);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Privilege
    public Result<?> delete(@PathVariable Integer id) {
        boolean removed = borrowService.removeById(id);
        if (!removed) {
            return Result.error("删除借阅记录失败或借阅记录不存在");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.RETURN_BOOK, "删除借阅记录，ID：" + id, operator);
        return Result.success();
    }

    @PostMapping("/deleteBatch")
    @Privilege
    public Result<?> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的借阅记录ID");
        }

        boolean removed = borrowService.removeByIds(ids);
        if (!removed) {
            return Result.error("批量删除借阅记录失败");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.RETURN_BOOK, "批量删除借阅记录，ID：" + ids, operator);
        return Result.success();
    }
}
