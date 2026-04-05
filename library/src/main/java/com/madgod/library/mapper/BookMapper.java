package com.madgod.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.madgod.library.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 图书表 Mapper 接口
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {

}
