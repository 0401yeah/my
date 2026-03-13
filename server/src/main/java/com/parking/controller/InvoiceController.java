package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Invoice;
import com.parking.service.InvoiceService;
import com.parking.util.InvoicePdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/invoice")
@CrossOrigin
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String invoiceTitle,
            @RequestParam(required = false) Integer status) {
        
        Page<Invoice> page = new Page<>(current, size);
        QueryWrapper<Invoice> wrapper = new QueryWrapper<>();
        
        if (invoiceTitle != null && !invoiceTitle.isEmpty()) {
            wrapper.like("invoice_title", invoiceTitle);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<Invoice> result = invoiceService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<Invoice> getById(@PathVariable Long id) {
        Invoice invoice = invoiceService.getById(id);
        return Result.success(invoice);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Invoice invoice) {
        boolean success = invoiceService.save(invoice);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody Invoice invoice) {
        boolean success = invoiceService.updateById(invoice);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = invoiceService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @GetMapping("/status")
    public Result<Map<String, Object>> getInvoiceStatus(@RequestParam String orderNo) {
        QueryWrapper<Invoice> wrapper = new QueryWrapper<>();
        wrapper.eq("order_no", orderNo);
        wrapper.orderByDesc("gmt_create");
        wrapper.last("LIMIT 1");
        Invoice invoice = invoiceService.getOne(wrapper);
        
        Map<String, Object> data = new HashMap<>();
        if (invoice != null) {
            data.put("id", invoice.getId());
            data.put("status", invoice.getStatus());
            data.put("invoiceTitle", invoice.getInvoiceTitle());
            data.put("amount", invoice.getAmount());
            data.put("gmtCreate", invoice.getGmtCreate());
        }
        return Result.success(data);
    }
    
    @PostMapping("/apply")
    public Result<Map<String, Object>> applyInvoice(@RequestBody Invoice invoice) {
        if (invoice.getGmtCreate() == null) {
            invoice.setGmtCreate(LocalDateTime.now());
        }
        if (invoice.getStatus() == null) {
            invoice.setStatus(0);
        }
        boolean success = invoiceService.save(invoice);
        
        if (success) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", invoice.getId());
            data.put("status", invoice.getStatus());
            return Result.success("申请成功", data);
        }
        return Result.error("申请失败");
    }
    
    @GetMapping("/download/{id}")
    public void downloadInvoice(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Invoice invoice = invoiceService.getById(id);
        
        if (invoice == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"发票不存在\"}");
            return;
        }
        
        if (invoice.getStatus() == null || invoice.getStatus() != 1) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":400,\"msg\":\"发票尚未开具，请等待审核\"}");
            return;
        }
        
        byte[] pdfBytes = InvoicePdfGenerator.generateInvoicePdf(invoice);
        
        String fileName = "发票_" + invoice.getInvoiceTitle() + "_" + 
                DateTimeFormatter.ofPattern("yyyyMMdd").format(invoice.getGmtCreate()) + ".pdf";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20");
        
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, 
                "attachment; filename=\"" + encodedFileName + "\"; filename*=UTF-8''" + encodedFileName);
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
        response.setContentLength(pdfBytes.length);
        
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }
    
    @PostMapping("/approve/{id}")
    public Result<String> approveInvoice(@PathVariable Long id) {
        Invoice invoice = invoiceService.getById(id);
        if (invoice == null) {
            return Result.error("发票不存在");
        }
        
        invoice.setStatus(1);
        boolean success = invoiceService.updateById(invoice);
        return success ? Result.success("发票已开具") : Result.error("操作失败");
    }
}
