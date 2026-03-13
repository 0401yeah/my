package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.Expense;
import com.parking.mapper.ExpenseMapper;
import com.parking.service.ExpenseService;
import org.springframework.stereotype.Service;

@Service
public class ExpenseServiceImpl extends ServiceImpl<ExpenseMapper, Expense> implements ExpenseService {
}
