package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("app_parking_space")
public class ParkingSpace {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("parking_lot_id")
    private Long parkingLotId;
    
    @TableField("space_no")
    private String spaceNo;
    
    private Integer status;
}
