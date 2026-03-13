package com.parking.entity.dto;

import lombok.Data;

/**
 * 车辆入场请求DTO
 */
@Data
public class VehicleEntryDTO {
    private String plateNumber;
    private Long parkingLotId;
}
