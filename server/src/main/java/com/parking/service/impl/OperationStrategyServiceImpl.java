package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.OperationStrategy;
import com.parking.mapper.OperationStrategyMapper;
import com.parking.service.OperationStrategyService;
import org.springframework.stereotype.Service;

@Service
public class OperationStrategyServiceImpl extends ServiceImpl<OperationStrategyMapper, OperationStrategy> implements OperationStrategyService {
}
