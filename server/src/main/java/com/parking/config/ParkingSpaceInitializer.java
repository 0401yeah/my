package com.parking.config;

import com.parking.entity.ParkingLot;
import com.parking.service.ParkingLotService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统启动时自动检查并生成停车位
 */
@Component
public class ParkingSpaceInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ParkingSpaceInitializer.class);

    @Autowired
    private ParkingLotService parkingLotService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("开始检查停车场停车位数据...");
        
        try {
            // 获取所有停车场（包括已删除的，因为我们要检查所有停车场）
            List<ParkingLot> parkingLots = parkingLotService.list();
            
            for (ParkingLot parkingLot : parkingLots) {
                // 跳过已删除的停车场
                if (parkingLot.getIsDeleted() != null && parkingLot.getIsDeleted() == 1) {
                    continue;
                }
                
                // 检查停车位数量是否一致
                if (parkingLot.getParkingSpaceNumber() != null && parkingLot.getParkingSpaceNumber() > 0) {
                    logger.info("检查停车场: {} (ID: {}), 预期车位数量: {}", 
                        parkingLot.getName(), parkingLot.getId(), parkingLot.getParkingSpaceNumber());
                    
                    // 调用服务方法自动生成或删除停车位
                    parkingLotService.updateParkingSpaces(parkingLot.getId(), parkingLot.getParkingSpaceNumber());
                    
                    logger.info("停车场 {} 的停车位数据已更新", parkingLot.getName());
                }
            }
            
            logger.info("停车场停车位数据检查完成");
        } catch (Exception e) {
            logger.error("检查停车场停车位数据时发生错误", e);
        }
    }
}
