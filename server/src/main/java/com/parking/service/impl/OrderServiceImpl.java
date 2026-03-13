package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.Order;
import com.parking.mapper.OrderMapper;
import com.parking.service.OrderService;
import org.springframework.stereotype.Service;

/**
 * 业务综合订单表 Service 实现
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {
}
