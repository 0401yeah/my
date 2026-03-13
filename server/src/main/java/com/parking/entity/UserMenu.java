package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_menu_user")
public class UserMenu {
    @TableId(value = "menu_id", type = IdType.AUTO)
    private Long menuId;

    private Long parentId;

    private String name;

    private String url;

    private String perms;

    private Integer type;

    private String icon;

    private Integer orderNum;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;
}
