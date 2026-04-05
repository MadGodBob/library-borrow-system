package com.madgod.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.madgod.library.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author madgod
 * @since 2026-04-03
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
