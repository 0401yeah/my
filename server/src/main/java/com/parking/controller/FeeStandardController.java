package com.parking.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.FeeStandard;
import com.parking.service.FeeStandardService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;
import java.util.HashMap;


/**
 * 收费标准Controller
 */
@RestController
@RequestMapping("/api/fee-standard")
public class FeeStandardController {

    @Resource
    private FeeStandardService feeStandardService;

    /**
     * 获取收费标准列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(@RequestParam Map<String, Object> params) {
        int current = Integer.parseInt(params.getOrDefault("current", "1").toString());
        int size = Integer.parseInt(params.getOrDefault("size", "20").toString());

        Page<FeeStandard> page = new Page<>(current, size);
        IPage<FeeStandard> pageResult = feeStandardService.page(page);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("records", pageResult.getRecords());
        resultMap.put("total", pageResult.getTotal());
        resultMap.put("size", pageResult.getSize());
        resultMap.put("current", pageResult.getCurrent());
        resultMap.put("pages", pageResult.getPages());

        return Result.success(resultMap);
    }

    /**
     * 获取收费标准详情
     */
    @GetMapping("/detail/{id}")
    public Result<FeeStandard> detail(@PathVariable Long id) {
        FeeStandard feeStandard = feeStandardService.getById(id);
        return Result.success(feeStandard);
    }

    /**
     * 新增收费标准
     */
    @PostMapping("/add")
    public Result<String> add(@RequestBody FeeStandard feeStandard) {
        feeStandardService.save(feeStandard);
        return Result.success("新增成功");
    }

    /**
     * 更新收费标准
     */
    @PutMapping("/update")
    public Result<String> update(@RequestBody FeeStandard feeStandard) {
        feeStandardService.updateById(feeStandard);
        return Result.success("更新成功");
    }

    /**
     * 删除收费标准
     */
    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        feeStandardService.removeById(id);
        return Result.success("删除成功");
    }

}
