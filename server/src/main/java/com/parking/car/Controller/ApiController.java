package com.parking.car.Controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.parking.common.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import javax.annotation.PostConstruct;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 接口管理
 */
@RestController
@RequestMapping("/api")
public class ApiController {

    @Value("${file.path}")
    private String filePath;
    
    @PostConstruct
    public void init() {
        System.out.println("初始化ApiController...");
        System.out.println("加载的文件路径配置: " + filePath);
        System.out.println("文件路径是否存在: " + new File(filePath).exists());
    }

    @Autowired
    private com.parking.car.util.BaiDuUtils baiDuUtils;

    /**
     * 识别车牌
     */
    @RequestMapping("distinguish")
    public Result<Map<String, Object>> distinguish(MultipartFile file, Long id) {
        try {
            System.out.println("接收到识别请求，id: " + id);
            if(file!=null){
                if(id==null){
                    System.out.println("停车场ID为空");
                    return Result.error("请选择停车场");
                }
                
                // 检查文件大小
                if (file.getSize() > 4 * 1024 * 1024) { // 4MB限制
                    System.out.println("文件过大: " + file.getSize() + " bytes");
                    return Result.error("文件大小不能超过4MB");
                }
                
                // 确保使用绝对路径
                File uploadDir = new File(filePath);
                System.out.println("文件上传目录: " + uploadDir.getAbsolutePath());
                
                // 确保上传目录存在
                if (!uploadDir.exists()) {
                    System.out.println("创建上传目录: " + uploadDir.mkdirs());
                    System.out.println("创建后上传目录是否存在: " + uploadDir.exists());
                }
                
                // 生成唯一文件名
                String fileName = file.getOriginalFilename();
                System.out.println("原始文件名: " + fileName);
                
                // 检查文件扩展名
                if (fileName == null || !fileName.contains(".")) {
                    System.out.println("文件名无效: " + fileName);
                    return Result.error("请上传有效的图片文件");
                }
                
                String suffix = fileName.substring(fileName.lastIndexOf("."));
                String uuid = IdUtil.simpleUUID();
                String newFileName = uuid + suffix;
                System.out.println("新文件名: " + newFileName);
                
                // 创建文件对象
                File imageFile = new File(uploadDir, newFileName);
                System.out.println("图片文件完整路径: " + imageFile.getAbsolutePath());
                
                // 确保文件父目录存在
                File parentDir = imageFile.getParentFile();
                if (!parentDir.exists()) {
                    System.out.println("创建文件父目录: " + parentDir.mkdirs());
                    System.out.println("创建后父目录是否存在: " + parentDir.exists());
                }
                
                // 写入文件
                System.out.println("开始写入文件...");
                System.out.println("文件大小: " + file.getSize() + " bytes");
                
                // 使用Java标准IO API写入文件，避免Hutool库的潜在问题
                try (InputStream inputStream = file.getInputStream();
                     FileOutputStream outputStream = new FileOutputStream(imageFile)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.flush();
                }
                
                System.out.println("文件写入完成，文件大小: " + imageFile.length());
                
                // 验证文件是否成功创建
                if (!imageFile.exists() || imageFile.length() == 0) {
                    System.out.println("文件创建失败或为空");
                    return Result.error("文件上传失败");
                }
                
                // 调用百度AI识别
                System.out.println("开始调用百度AI识别...");
                String plateNumber = baiDuUtils.plateLicense(imageFile.getAbsolutePath());
                System.out.println("百度AI识别结果: " + plateNumber);
                
                if(StringUtils.isBlank(plateNumber)){
                    System.out.println("识别失败：未识别到车牌号");
                    return Result.error("识别失败：未识别到车牌号");
                }
                
                // 构建返回数据
                String fileDay = DateUtil.thisYear()+"/"+(DateUtil.thisMonth()+1)+"/"+DateUtil.thisDayOfMonth();
                String imagePath = "/file/" + fileDay+"/"+newFileName;
                Map<String, Object> data = new HashMap<>();
                data.put("plateNumber", plateNumber);
                data.put("imagePath", imagePath);
                
                System.out.println("识别成功：" + plateNumber);
                return Result.success("识别成功", data);
            }else{
                System.out.println("未接收到文件");
                return Result.error("请上传图片");
            }
        } catch (Exception e) {
            System.out.println("识别过程发生异常:");
            e.printStackTrace();
            return Result.error("识别失败: " + e.getMessage());
        }
    }


    /**
     * 支付回调
     */
    @RequestMapping("/callBack")
    public String callBack() {
        /**
         * 生成订单、调用支付接口
         */
        return "";
    }
}