package com.madgod.library.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.madgod.library.common.Result;
import com.madgod.library.entity.Book;
import com.madgod.library.entity.Borrow;
import com.madgod.library.service.IBookService;
import com.madgod.library.service.IBorrowService;
import jakarta.annotation.Resource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/userLend")
public class UserLendController {
    @Resource
    IBookService bookService;

    @Resource
    IBorrowService borrowService;

    @Resource
    BorrowController borrowController;

    @Resource
    OperationLogController log;

    /* 用户借书 */
    @GetMapping("/lendBook")
    @Transactional
    public Result<?> lendBook(@RequestParam(required = false) Integer userId,
                          @RequestParam(defaultValue = "") String userName,
                          @RequestParam(required = false) Integer bookId,
                          @RequestParam(defaultValue = "") String bookTitle
    ) {
        if(!checkBookIfCanLend(bookId)){
            return Result.error(400, "书籍不可借");
        }

        Borrow borrow = new Borrow();
        borrow.setUserId(userId);
        borrow.setUserName(userName);
        borrow.setBookId(bookId);
        borrow.setBookTitle(bookTitle);
        borrow.setStatus(1); // 1表示借出
        borrow.setBorrowTime(LocalDateTime.now());

        Result<?> res1 = borrowController.save(borrow);
        Result<?> res2 = updateBookStatus(bookId, 0);
        if (res1.getCode() != 200) {
            return rollbackAndReturn(res1);
        }
        if (res2.getCode() != 200) {
            return rollbackAndReturn(res2);
        }

        return Result.success();
    }
    
    /* 用户还书 */
    @GetMapping("/returnBook")
    @Transactional
    public Result<?> returnBook(@RequestParam(required = false) Integer userId,
                              @RequestParam(required = false) Integer bookId
    ) {
        if (userId == null || bookId == null) {
            return Result.error(400, "userId和bookId不能为空");
        }

        // 查找可归还记录
        LambdaQueryWrapper<Borrow> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Borrow::getUserId, userId)
                .eq(Borrow::getBookId, bookId)
                .eq(Borrow::getStatus, 1)
                .orderByDesc(Borrow::getBorrowTime)
                .last("limit 1");

        Borrow activeBorrow = borrowService.getOne(queryWrapper, false);
        if (activeBorrow == null) {
            return Result.error(400, "未找到可归还的借阅记录");
        }
        activeBorrow.setStatus(0); // 0表示已还
        activeBorrow.setReturnTime(LocalDateTime.now());

        Result<?> res1 = borrowController.update(activeBorrow);
        Result<?> res2 = updateBookStatus(bookId, 1);
        if (res1.getCode() != 200) {
            return rollbackAndReturn(res1);
        }
        if (res2.getCode() != 200) {
            return rollbackAndReturn(res2);
        }
        return Result.success();
    }

    /* 检查书籍是否可借 */
    public Boolean checkBookIfCanLend(Integer bookId){
        Book  book = bookService.getById(bookId);
        if(book == null){
            return false;
        }
        return book.getStatus() > 0;
    }

    /* 修改书籍状态 */
    public Result<?> updateBookStatus(Integer bookId, Integer status) {
        if (bookId == null || status == null) {
            return Result.error(400, "bookId和status不能为空");
        }
        Book book = new Book();
        book.setId(bookId);
        book.setStatus(status);
        boolean updated = bookService.updateById(book);
        return updated ? Result.success() : Result.error("更新书籍状态失败或书籍不存在");
    }

    private Result<?> rollbackAndReturn(Result<?> result) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return result;
    }
}
