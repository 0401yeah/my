package com.parking.entity.vo;

import com.parking.entity.EquipmentMaintain;

@SuppressWarnings("serial")
public class EquipmentMaintainVO extends EquipmentMaintain {
    private String equipmentName;
    private String equipmentCode;

    public String getEquipmentName() {
        return equipmentName;
    }

    public void setEquipmentName(String equipmentName) {
        this.equipmentName = equipmentName;
    }

    public String getEquipmentCode() {
        return equipmentCode;
    }

    public void setEquipmentCode(String equipmentCode) {
        this.equipmentCode = equipmentCode;
    }
}
