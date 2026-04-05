package com.madgod.library.service.impl;

import com.madgod.library.entity.Borrow;
import com.madgod.library.mapper.BorrowMapper;
import com.madgod.library.service.IBorrowService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 借阅表 服务实现类
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements IBorrowService {

}
