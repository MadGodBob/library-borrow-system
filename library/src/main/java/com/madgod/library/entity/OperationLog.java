package com.madgod.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 操作日志表
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Data
public class OperationLog implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 操作人ID
     */
    private Integer userId;

    /**
     * 操作人姓名
     */
    private String userName;

    /**
     * 操作类型
     */
    private Integer operationType;

    /**
     * 操作内容
     */
    private String operation;

    /**
     * 操作时间
     */
    private LocalDateTime timestamp;
}
