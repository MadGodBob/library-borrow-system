package com.madgod.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.madgod.library.entity.Borrow;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 借阅表 Mapper 接口
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Mapper
public interface BorrowMapper extends BaseMapper<Borrow> {

}
