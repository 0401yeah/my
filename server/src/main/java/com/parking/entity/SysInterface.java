package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_interface")
public class SysInterface {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String type;
    private String token;
    private String query;
    private String params;
    private String description;
    private Integer status;
    private Long creatorId;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
}
