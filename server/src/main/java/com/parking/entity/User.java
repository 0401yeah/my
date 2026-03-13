package com.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_user")
public class User {
    @TableId(type = IdType.AUTO, value = "user_id")
    private Long userId;
    
    private Long orgId;
    
    private String username;
    
    private String password;
    
    private String nickname;
    
    private String mobile;
    
    private String gender;
    
    private Integer status;
    
    private Integer userType;
    
    private String avatar;
    
    private String remark;
    
    @TableField(fill = FieldFill.INSERT, value = "gmt_create")
    private LocalDateTime gmtCreate;
    
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "gmt_modified")
    private LocalDateTime gmtModified;
    
    @TableField(exist = false)
    private List<String> roles;
    
    @TableField(exist = false)
    private List<String> buttons;
}
