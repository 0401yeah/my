package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.Notice;
import com.parking.mapper.NoticeMapper;
import com.parking.service.NoticeService;
import org.springframework.stereotype.Service;

/**
 * 通知与公告服务实现类
 */
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {
}
