package com.madgod.library.utils;

import com.madgod.library.common.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@Component
public final class DataExceptionUtils {

    private DataExceptionUtils() {
    }

    public static <T> Result<T> handleSave(Supplier<Result<T>> action) {
        return handle(action);
    }

    private static <T> Result<T> handle(Supplier<Result<T>> action) {
        try {
            return action.get();
        } catch (DuplicateKeyException e) {
            return Result.error(409, "用户名已存在");
        } catch (DataIntegrityViolationException e) {
            return Result.error(400, "数据不合法或违反约束");
        } catch (OptimisticLockingFailureException e) {
            return Result.error(409, "数据已被其他请求修改，请刷新后重试");
        } catch (DataAccessException e) {
            return Result.error(500, "数据库操作失败：" + e.getMostSpecificCause().getMessage());
        } catch (Exception e) {
            return Result.error("异常：" + e.getMessage());
        }
    }
}

