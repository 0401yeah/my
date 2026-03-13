package com.parking.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("app_operation_strategy")
public class OperationStrategy {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long parkingLotId;
    private String strategyName;
    private String description;
    private Integer status;
    private String ruleCondition;
    private Integer isStackable;
    private Long creatorId;
    private String creatorName;
    private LocalDateTime gmtCreate;
    private LocalDateTime gmtModified;
    private Integer isDeleted;
}
