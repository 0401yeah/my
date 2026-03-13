package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 车辆进出场基础流水表
 */
@Data
@TableName("app_parking_record")
public class ParkingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("plate_number")
    private String plateNumber;

    @TableField("parking_lot_id")
    private Long parkingLotId;


    @TableField("gmt_into")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtInto;

    @TableField("gmt_out")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtOut;

    @TableField("stay_minutes")
    private Integer stayMinutes;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("original_amount")
    private BigDecimal originalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("fee_standard_id")
    private Long feeStandardId;

    @TableField("vehicle_type")
    private Integer vehicleType;

    @TableField("order_no")
    private String orderNo;

    @TableField("payment_status")
    private Integer paymentStatus;

    @TableField("pay_way")
    private Integer payWay;

    @TableField("user_id")
    private Long userId;

    private Integer type;

    private Integer status;

    @TableField("space_no")
    private String spaceNo;
}
