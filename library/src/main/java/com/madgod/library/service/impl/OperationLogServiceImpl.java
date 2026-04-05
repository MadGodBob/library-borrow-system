package com.madgod.library.service.impl;

import com.madgod.library.entity.OperationLog;
import com.madgod.library.mapper.OperationLogMapper;
import com.madgod.library.service.IOperationLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author madgod
 * @since 2026-04-05
 */
@Service
public class OperationLogServiceImpl extends ServiceImpl<OperationLogMapper, OperationLog> implements IOperationLogService {

}
