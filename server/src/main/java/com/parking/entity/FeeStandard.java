package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收费标准实体类
 */
@Data
@TableName("app_fee_standard")
public class FeeStandard {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long parkingLotId;

    private String name;

    private Integer vehicleType;

    private Integer freeMinutes;

    private BigDecimal firstHourFee;

    private BigDecimal afterFirstHourFee;

    private BigDecimal dayMaxFee;

    private String nightStartTime;

    private String nightEndTime;

    private BigDecimal nightFee;

    private Integer status;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

}
