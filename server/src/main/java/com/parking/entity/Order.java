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
 * 业务综合订单实体类
 */
@Data
@TableName("app_order")
public class Order {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("transaction_id")
    private String transactionId;

    @TableField("order_type")
    private Integer orderType;

    private String body;

    @TableField("pay_amount")
    private BigDecimal payAmount;

    @TableField("original_amount")
    private BigDecimal originalAmount;

    @TableField("discount_amount")
    private BigDecimal discountAmount;

    @TableField("plate_number")
    private String plateNumber;

    @TableField("parking_lot_id")
    private Long parkingLotId;

    @TableField("fee_standard_id")
    private Long feeStandardId;

    @TableField("vehicle_type")
    private Integer vehicleType;

    @TableField("charge_minutes")
    private Integer chargeMinutes;

    @TableField("gmt_into")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtInto;

    @TableField("gmt_out")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtOut;

    private Integer status;

    @TableField("pay_way")
    private Integer payWay;

    @TableField("user_id")
    private Long userId;

    @TableField("pay_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;

    private String remark;

    @TableField("gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
}
