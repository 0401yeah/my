package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    @TableField("org_id")
    private Long orgId;

    private String username;

    private String password;

    private String nickname;

    private String mobile;

    private String gender;

    private Integer status;

    @TableField("user_type")
    private Integer userType;

    private String avatar;

    private String remark;

    @TableField("gmt_create")
    private LocalDateTime gmtCreate;

    @TableField("gmt_modified")
    private LocalDateTime gmtModified;

    @TableField("is_deleted")
    private Integer isDeleted;
}
