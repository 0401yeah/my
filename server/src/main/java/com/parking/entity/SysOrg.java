package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 机构管理表
 */
@Data
@TableName("sys_org")
public class SysOrg implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "org_id", type = IdType.AUTO)
    private Long orgId;

    private Long parentId;

    private String code;

    private String name;

    private String fullName;

    private String director;

    private String phone;

    private String email;

    private String address;

    private Integer orderNum;

    private Integer status;

    private Date gmtCreate;

    private Date gmtModified;

    private Integer isDeleted;
}
