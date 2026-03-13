package com.parking.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sys_role")
public class Role {
    @TableId(type = IdType.AUTO, value = "role_id")
    private Long roleId;
    
    private Long orgId;
    
    private String roleName;
    
    private String roleSign;
    
    private String remark;
    
    private Long creatorId;
    
    @TableField(fill = FieldFill.INSERT, value = "gmt_create")
    private LocalDateTime gmtCreate;
    
    @TableField(fill = FieldFill.INSERT_UPDATE, value = "gmt_modified")
    private LocalDateTime gmtModified;
}
