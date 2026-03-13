package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("app_equipment_maintain")
public class EquipmentMaintain {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long equipmentId;
    private String issueDesc;
    private Long reporterId;
    private Long maintainerId;
    private Integer status;
    private BigDecimal payAmount;
    private LocalDateTime fixTime;
    private LocalDateTime gmtCreate;
}
