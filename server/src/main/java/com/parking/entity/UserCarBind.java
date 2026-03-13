package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
@TableName("app_user_car_bind")
public class UserCarBind {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("plate_number")
    private String plateNumber;

    @TableField("bind_type")
    private Integer bindType;

    @TableField("parent_id")
    private Long parentId;

    @TableField("is_auto_pay")
    private Integer isAutoPay;

    @TableField("gmt_create")
    private Date gmtCreate;

    @TableField("expire_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date expireTime;

    @TableField("family_name")
    private String familyName;

    @TableField("status")
    private Integer status;
}
