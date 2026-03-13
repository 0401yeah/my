package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 车辆基础资产档案表
 */
@Data
@TableName("app_vehicle")
public class Vehicle {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 车牌号
     */
    @TableField("plate_number")
    private String plateNumber;

    /**
     * 车主姓名
     */
    @TableField("owner_name")
    private String ownerName;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 关联用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 所属主停车场ID
     */
    @TableField("parking_lot_id")
    private Long parkingLotId;

    /**
     * 状态 0:禁用(拒绝入场) 1:正常
     */
    private Integer status;

    /**
     * 车辆计费属性: 0临时车 1月租车 2VIP免收费
     */
    private Integer type;

    /**
     * 月租卡到期时间
     */
    @TableField("validity_time")
    private LocalDateTime validityTime;

    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    /**
     * 0未删除 1已删除
     */
    @TableField("is_deleted")
    private Integer isDeleted;
}
