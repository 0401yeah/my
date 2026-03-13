package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.EquipmentMaintain;
import com.parking.entity.vo.EquipmentMaintainVO;

import java.util.List;

public interface EquipmentMaintainService extends IService<EquipmentMaintain> {
    List<EquipmentMaintainVO> getWithEquipment();
}
