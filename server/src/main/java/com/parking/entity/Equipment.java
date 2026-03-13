package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("app_equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String type;
    private Long parkingLotId;
    private Integer status;
    private LocalDateTime lastMaintainTime;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Integer isDeleted;
}
