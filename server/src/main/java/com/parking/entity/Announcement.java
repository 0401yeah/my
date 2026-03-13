package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("app_announcement")
public class Announcement {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField(value = "org_id")
    private Long orgId;

    private String title;

    private String content;

    private Integer type;

    private Integer status;

    @TableField(value = "creator_id")
    private Long creatorId;

    @TableField(value = "gmt_create")
    private LocalDateTime gmtCreate;
}
