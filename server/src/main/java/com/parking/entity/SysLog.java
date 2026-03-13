package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_log")
public class SysLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long creatorId;
    private String username;
    private String operation;
    private String method;
    private String params;
    private Integer time;
    private String ip;
    private Integer deviceType;
    private Integer logType;
    private String exceptionDetail;
    private LocalDateTime gmtCreate;
}
