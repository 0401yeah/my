package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 车辆分组与黑白名单表
 */
@Data
@TableName("app_car_tag")
public class CarTag {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属停车场ID
     */
    @TableField("parking_lot_id")
    private Long parkingLotId;

    /**
     * 车牌号
     */
    @TableField("plate_number")
    private String plateNumber;

    /**
     * 标签名称
     */
    @TableField("tag_name")
    private String tagName;

    /**
     * 标签类型: 0普通 1白名单 2黑名单
     */
    @TableField("tag_type")
    private Integer tagType;

    /**
     * 颜色
     */
    private String color;

    /**
     * 原因
     */
    private String reason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private Date gmtCreate;

    /**
     * 名单失效/到期时间
     */
    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;
}
