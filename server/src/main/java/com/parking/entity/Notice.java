package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 通知与公告实体类
 */
@Data
@TableName("app_announcement")
public class Notice {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("org_id")
    private Long orgId;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("type")
    private Integer type;

    @TableField("status")
    private Integer status;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
}
