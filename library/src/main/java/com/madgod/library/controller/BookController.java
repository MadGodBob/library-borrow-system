package com.madgod.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.madgod.library.common.Privilege;
import com.madgod.library.common.Result;
import com.madgod.library.entity.Book;
import com.madgod.library.entity.User;
import com.madgod.library.entity.operationTypeEnum;
import com.madgod.library.service.IBookService;
import com.madgod.library.utils.CurrentUserUtils;
import com.madgod.library.utils.DataExceptionUtils;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 图书表 前端控制器
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@RestController
@RequestMapping("/book")
public class BookController {

    @Resource
    private IBookService bookService;

    @Resource
    private OperationLogController log;

    @GetMapping("/searchByPage")
    public Result<Page<Book>> searchByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "") String isbn,
            @RequestParam(defaultValue = "") String title,
            @RequestParam(defaultValue = "") String author,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") Integer status) {
        LambdaQueryWrapper<Book> queryWrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(isbn)) {
            queryWrapper.like(Book::getIsbn, isbn);
        }
        if (StringUtils.isNotBlank(title)) {
            queryWrapper.like(Book::getTitle, title);
        }
        if (StringUtils.isNotBlank(author)) {
            queryWrapper.like(Book::getAuthor, author);
        }
        if (StringUtils.isNotBlank(category)) {
            queryWrapper.like(Book::getCategory, category);
        }
        if (status != null) {
            queryWrapper.eq(Book::getStatus, status);
        }
        queryWrapper.orderByAsc(Book::getId);
        Page<Book> bookPage = bookService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return Result.success(bookPage);
    }

    @GetMapping("/{id}")
    public Result<Book> getById(@PathVariable Integer id) {
        Book book = bookService.getById(id);
        if (book == null) {
            return Result.error(404, "图书不存在");
        }
        return Result.success(book);
    }

    @PostMapping("/save")
    @Privilege
    public Result<?> save(@RequestBody Book book) {
        return DataExceptionUtils.handleSave(() -> {
            boolean saved = bookService.save(book);
            if (!saved) {
                return Result.error("新增图书失败");
            }
            User operator = CurrentUserUtils.getCurrentUser();
            log.log(operationTypeEnum.SAVE_BOOK, "新增图书，ID：" + book.getId(), operator);
            return Result.success();
        });
    }

    @PutMapping("/update")
    @Privilege
    public Result<?> update(@RequestBody Book book) {
        boolean updated = bookService.updateById(book);
        if (!updated) {
            return Result.error("更新图书失败或图书不存在");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.UPDATE_BOOK, "更新图书，ID：" + book.getId(), operator);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @Privilege
    public Result<?> delete(@PathVariable Integer id) {
        boolean removed = bookService.removeById(id);
        if (!removed) {
            return Result.error("删除图书失败或图书不存在");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.DELETE_BOOK, "删除图书，ID：" + id, operator);
        return Result.success();
    }

    @PostMapping("/deleteBatch")
    @Privilege
    public Result<?> deleteBatch(@RequestBody List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Result.error(400, "请选择要删除的图书ID");
        }

        boolean removed = bookService.removeByIds(ids);
        if (!removed) {
            return Result.error("批量删除图书失败");
        }
        User operator = CurrentUserUtils.getCurrentUser();
        log.log(operationTypeEnum.DELETE_BOOK, "批量删除图书，ID：" + ids, operator);
        return Result.success();
    }
}
