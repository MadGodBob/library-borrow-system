package com.madgod.library.service.impl;

import com.madgod.library.entity.User;
import com.madgod.library.mapper.UserMapper;
import com.madgod.library.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author madgod
 * @since 2026-04-03
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
