package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.EquipmentMaintain;
import com.parking.entity.vo.EquipmentMaintainVO;
import com.parking.mapper.EquipmentMaintainMapper;
import com.parking.service.EquipmentMaintainService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EquipmentMaintainServiceImpl extends ServiceImpl<EquipmentMaintainMapper, EquipmentMaintain> implements EquipmentMaintainService {
    @Override
    public List<EquipmentMaintainVO> getWithEquipment() {
        return baseMapper.selectWithEquipment();
    }
}
