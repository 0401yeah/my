package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 停车场物理信息管理表
 */
@Data
@TableName("app_parking_lot")
public class ParkingLot {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 停车场名称
     */
    private String name;

    /**
     * 所属机构
     */
    private Long orgId;

    /**
     * 机构名称
     */


    /**
     * 总车位数
     */
    private Integer parkingSpaceNumber;

    /**
     * 剩余可用车位(实时更新)
     */
    private Integer availableSpaceNumber;

    /**
     * 状态 1开放 0关闭
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private Long creatorId;

    /**
     * 创建时间
     */
    private LocalDateTime gmtCreate;

    /**
     * 更新时间
     */
    private LocalDateTime gmtModified;

    /**
     * 0未删除 1已删除
     */
    private Integer isDeleted;

    /**
     * 停车场地址
     */
    private String address;
}