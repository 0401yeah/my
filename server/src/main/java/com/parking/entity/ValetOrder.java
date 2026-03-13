package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("app_valet_order")
public class ValetOrder {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("plate_number")
    private String plateNumber;
    
    @TableField("parking_lot_id")
    private Long parkingLotId;
    
    @TableField("appointment_time")
    private LocalDateTime appointmentTime;
    
    @TableField("staff_id")
    private Long staffId;
    
    @TableField("check_images")
    private String checkImages;
    
    @TableField("fee")
    private BigDecimal fee;
    
    private Integer status;
    
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;
    
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;
}
