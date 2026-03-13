package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("app_invoice")
public class Invoice {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("order_no")
    private String orderNo;
    
    @TableField("user_id")
    private Long userId;
    
    private BigDecimal amount;
    
    @TableField("invoice_type")
    private Integer invoiceType;
    
    @TableField("invoice_title")
    private String invoiceTitle;
    
    @TableField("invoice_content")
    private String invoiceContent;
    
    @TableField("tax_number")
    private String taxNumber;
    
    private String email;
    
    private Integer status;
    
    @TableField("invoice_url")
    private String invoiceUrl;
    
    @TableField("gmt_create")
    private LocalDateTime gmtCreate;
}
