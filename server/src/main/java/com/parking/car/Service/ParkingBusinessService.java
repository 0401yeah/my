package com.parking.car.Service;

import com.parking.common.Result;
import org.springframework.web.multipart.MultipartFile;

public interface ParkingBusinessService {
    /**
     * 处理车辆进出场逻辑（识别+计费+落库）
     * @param file 摄像头上传的图片文件
     * @param parkId 停车场ID
     * @return 处理结果
     */
    Result<Object> processVehicleEntryExit(MultipartFile file, Long parkId);
}