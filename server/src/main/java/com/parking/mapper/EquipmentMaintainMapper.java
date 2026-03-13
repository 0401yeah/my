package com.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.entity.EquipmentMaintain;
import com.parking.entity.vo.EquipmentMaintainVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface EquipmentMaintainMapper extends BaseMapper<EquipmentMaintain> {
    @Select("SELECT m.*, e.name AS equipment_name, e.code AS equipment_code FROM app_equipment_maintain m LEFT JOIN app_equipment e ON m.equipment_id = e.id")
    List<EquipmentMaintainVO> selectWithEquipment();
}
