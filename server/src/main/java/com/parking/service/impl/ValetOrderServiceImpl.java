package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ValetOrder;
import com.parking.mapper.ValetOrderMapper;
import com.parking.service.ValetOrderService;
import org.springframework.stereotype.Service;

@Service
public class ValetOrderServiceImpl extends ServiceImpl<ValetOrderMapper, ValetOrder> implements ValetOrderService {
}
