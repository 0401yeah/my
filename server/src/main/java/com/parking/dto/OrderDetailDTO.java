package com.parking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderDetailDTO {
    private Long id;
    private String orderNo;
    private String transactionId;
    private Integer orderType;
    private String body;
    private BigDecimal payAmount;
    private BigDecimal originalAmount;
    private BigDecimal discountAmount;
    private String plateNumber;
    private Long parkingLotId;
    private String parkingLotName;
    private Integer status;
    private Integer payWay;
    private Long customerId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date payTime;
    
    private String remark;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtInto;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtOut;
    
    private Integer stayMinutes;
    private Long feeStandardId;
    private Integer vehicleType;
    private Integer paymentStatus;
    private String source;
}
