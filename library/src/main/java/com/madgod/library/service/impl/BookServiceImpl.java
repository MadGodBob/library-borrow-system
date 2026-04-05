package com.madgod.library.service.impl;

import com.madgod.library.entity.Book;
import com.madgod.library.mapper.BookMapper;
import com.madgod.library.service.IBookService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 图书表 服务实现类
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements IBookService {

}
