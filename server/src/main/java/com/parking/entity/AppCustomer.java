package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("app_customer")
public class AppCustomer {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String openid;

    private String mobile;

    private String nickname;

    private String avatar;

    private String password;

    private Integer status;

    @TableField("gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime gmtCreate;

    @TableField("gmt_modified")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime gmtModified;

    @TableField("is_deleted")
    private Integer isDeleted;
}
