package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 停车预约实体类
 */
@Data
@TableName("app_car_reservation")
public class ParkingReservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("parking_lot_id")
    private Long parkingLotId;
    
    @TableField("parking_space_id")
    private Long parkingSpaceId;
    
    @TableField("plate_number")
    private String plateNumber;
    
    @TableField("reservation_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date reservationTime;
    
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;
    
    private Integer status; // 0:预约中 1:已履约 2:已取消 3:已违约过期
    
    @TableField("gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
}