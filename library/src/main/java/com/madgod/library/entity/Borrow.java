package com.madgod.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 借阅表
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Data
public class Borrow implements Serializable {
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 借书人ID
     */
    private Integer userId;

    /**
     * 借书人姓名
     */
    private String userName;

    /**
     * 图书ID
     */
    private Integer bookId;

    /**
     * 图书标题
     */
    private String bookTitle;

    /**
     * 借阅时间
     */
    private LocalDateTime borrowTime;

    /**
     * 归还时间
     */
    private LocalDateTime returnTime;

    /**
     * 状态,1借阅中，0已归还
     */
    private Integer status;
}
