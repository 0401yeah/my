package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
@TableName("app_expense")
public class Expense {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("expense_no")
    private String expenseNo;

    private String category;

    private BigDecimal amount;

    @TableField("parking_lot_id")
    private Long parkingLotId;

    private String description;

    private Integer status;

    @TableField("creator_id")
    private Long creatorId;

    @TableField("approver_id")
    private Long approverId;

    @TableField("approve_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveTime;

    @TableField("gmt_create")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
}
