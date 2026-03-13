package com.parking.car.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baidu.aip.ocr.AipOcr;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;


@Component
public class BaiDuUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(BaiDuUtils.class);

    @Value("${baidu.app.id}")
    private String appId;

    @Value("${baidu.api.key}")
    private String apiKey;

    @Value("${baidu.access.key.secret}")
    private String accessKeySecret;

    private AipOcr client;

    @PostConstruct
    public void init() {
        try {
            System.out.println("开始初始化百度AI...");
            System.out.println("appId: " + appId);
            System.out.println("apiKey: " + apiKey);
            System.out.println("accessKeySecret: " + accessKeySecret);
            client = new AipOcr(appId, apiKey, accessKeySecret);
            client.setConnectionTimeoutInMillis(2000);
            client.setSocketTimeoutInMillis(60000);
            System.out.println("百度AI初始化成功！");
        } catch (Exception e) {
            System.out.println("百度智能AI初始化失败:");
            e.printStackTrace();
            LOGGER.error("百度智能AI初始化失败,{}", e.getMessage());
        }
    }

    /**
     * 参数为本地图片路径
     */
    public String plateLicense(String image) {
        LOGGER.info("正在调用百度AI进行车牌识别，图片路径: {}", image);
        try {
            // 检查客户端是否初始化成功
            if (client == null) {
                LOGGER.error("百度AI客户端未初始化成功，无法进行识别");
                throw new IllegalStateException("百度AI客户端未初始化成功");
            }
            
            HashMap<String, String> options = new HashMap<>();
            /**
             * 是否检测多张车牌，默认为false
             * 当置为true的时候可以对一张图片内的多张车牌进行识别
             */
            options.put("multi_detect", "true");
            
            // 客户端初始化好后将图片和操作的参数向百度请求
            LOGGER.info("发送识别请求到百度AI...");
            JSONObject res = client.plateLicense(image, options);
            LOGGER.info("百度AI响应原始结果: {}", res.toString());
            
            // 获取识别结果
            if (!res.has("words_result")) {
                LOGGER.warn("百度AI返回结果中不包含 words_result 字段");
                return "";
            }
            
            Object result = res.get("words_result");
            JSONArray array = JSON.parseArray(result.toString());
            
            if (array == null || array.isEmpty()) {
                LOGGER.warn("百度AI未能识别到任何车牌信息");
                return "";
            }
            
            com.alibaba.fastjson.JSONObject object = JSON.parseObject(array.get(0).toString());
            Object number = object.get("number");
            
            if (number == null) {
                LOGGER.warn("百度AI识别结果中不包含 number 字段");
                return "";
            }
            
            String plate = number.toString();
            LOGGER.info("百度AI成功识别车牌号: {}", plate);
            return plate;
        }catch (Exception e){
            LOGGER.error("百度AI识别过程发生异常", e);
            throw e; // 抛出异常，让上层处理
        }
    }
}