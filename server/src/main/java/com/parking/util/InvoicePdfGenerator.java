package com.parking.util;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.borders.DashedBorder;
import com.itextpdf.kernel.colors.ColorConstants;
import com.parking.entity.Invoice;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

public class InvoicePdfGenerator {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy年MM月dd日");

    public static byte[] generateInvoicePdf(Invoice invoice) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // 使用系统默认字体或嵌入字体
        PdfFont font = null;
        try {
            // 尝试使用系统字体
            font = PdfFontFactory.createFont("STSong-Light", "UniGB-UCS2-H");
        } catch (Exception e) {
            // 如果系统字体不可用，使用内置字体
            font = PdfFontFactory.createFont();
        }

        // 设置页面边距
        document.setMargins(50, 50, 50, 50);

        // 发票头部 - 单行布局
        Table headerTable = new Table(UnitValue.createPercentArray(new float[]{20, 50, 30}));
        headerTable.setWidth(UnitValue.createPercentValue(100));

        // 左侧：税务局圆形红章
        Cell taxSealCell = new Cell().add(new Paragraph("国家税务\n总局监制")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.RED))
                .setBorder(new SolidBorder(ColorConstants.RED, 4))
                .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(50))
                .setBackgroundColor(ColorConstants.WHITE)
                .setPadding(10)
                .setTextAlignment(TextAlignment.CENTER);
        headerTable.addCell(taxSealCell);

        // 中间：发票标题
        Cell titleCell = new Cell().add(new Paragraph("电子发票（普通发票）")
                .setFont(font)
                .setFontSize(28)
                .setBold()
                .setTextAlignment(TextAlignment.LEFT))
                .add(new Paragraph("Electronic Invoice (General Invoice)")
                        .setFont(font)
                        .setFontSize(11)
                        .setTextAlignment(TextAlignment.LEFT)
                        .setFontColor(ColorConstants.GRAY))
                .setBorder(null)
                .setPadding(5);
        headerTable.addCell(titleCell);

        // 右侧：发票号码和日期
        Cell infoCell = new Cell().add(new Paragraph("发票号码 INVOICE NUMBER")
                .setFont(font)
                .setFontSize(11)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontColor(ColorConstants.GRAY))
                .add(new Paragraph(generateInvoiceNo())
                        .setFont(font)
                        .setFontSize(24)
                        .setBold()
                        .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("开票日期 DATE OF ISSUE")
                        .setFont(font)
                        .setFontSize(11)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontColor(ColorConstants.GRAY))
                .add(new Paragraph(invoice.getGmtCreate() != null ? invoice.getGmtCreate().format(DATE_FORMATTER) : "")
                        .setFont(font)
                        .setFontSize(16)
                        .setTextAlignment(TextAlignment.RIGHT))
                .setBorder(null)
                .setPadding(5);
        headerTable.addCell(infoCell);

        document.add(headerTable);
        document.add(new Paragraph(" ")); // 空行

        // 购买方与销售方信息
        Table partyTable = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        partyTable.setWidth(UnitValue.createPercentValue(100));
        partyTable.setMarginBottom(32f);

        // 购买方信息
        Cell purchaserCell = new Cell().add(new Paragraph("购买方信息 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Purchaser</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED))
                .add(new Paragraph(" "))
                .add(new Paragraph(invoice.getInvoiceTitle() != null ? invoice.getInvoiceTitle() : "个人")
                        .setFont(font)
                        .setFontSize(18)
                        .setBold())
                .add(new Paragraph(" "))
                .add(new Paragraph("统一社会信用代码: " + (invoice.getTaxNumber() != null ? invoice.getTaxNumber() : "-"))
                        .setFont(font)
                        .setFontSize(14))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceGray(0.95f))
                .setPadding(20);
        partyTable.addCell(purchaserCell);

        // 销售方信息
        Cell sellerCell = new Cell().add(new Paragraph("销售方信息 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Seller</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED))
                .add(new Paragraph(" "))
                .add(new Paragraph("智泊云端")
                        .setFont(font)
                        .setFontSize(18)
                        .setBold())
                .add(new Paragraph(" "))
                .add(new Paragraph("统一社会信用代码: 91320100MA1XXXXXXX")
                        .setFont(font)
                        .setFontSize(14))
                .add(new Paragraph("地址、电话: 南京市玄武区XX路XX号 025-12345678")
                        .setFont(font)
                        .setFontSize(14))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceGray(0.95f))
                .setPadding(20);
        partyTable.addCell(sellerCell);

        document.add(partyTable);
        document.add(new Paragraph(" ")); // 空行

        // 项目明细表格
        Table itemTable = new Table(UnitValue.createPercentArray(new float[]{33, 10, 15, 15, 15, 12}));
        itemTable.setWidth(UnitValue.createPercentValue(100));
        itemTable.setMarginBottom(32f);

        // 表头
        itemTable.addHeaderCell(new Cell().add(new Paragraph("项目名称 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Item Name</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(12)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 2)));
        
        itemTable.addHeaderCell(new Cell().add(new Paragraph("数量 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Qty</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(12)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 2)));
        
        itemTable.addHeaderCell(new Cell().add(new Paragraph("单价 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Price</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(12)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 2)));
        
        itemTable.addHeaderCell(new Cell().add(new Paragraph("金额 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Amount</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(12)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 2)));
        
        itemTable.addHeaderCell(new Cell().add(new Paragraph("税率 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Tax Rate</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(12)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 2)));
        
        itemTable.addHeaderCell(new Cell().add(new Paragraph("税额 <span style='font-size: 11px; font-weight: normal; margin-left: 4px;'>Tax</span>")
                .setFont(font)
                .setFontSize(14)
                .setBold()
                .setFontColor(ColorConstants.RED))
                .setBackgroundColor(ColorConstants.WHITE)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(12)
                .setBorderBottom(new SolidBorder(ColorConstants.RED, 2)));

        // 项目行
        String content = invoice.getInvoiceContent() != null ? invoice.getInvoiceContent() : "*停车服务* 车辆停放费";
        BigDecimal amount = invoice.getAmount() != null ? invoice.getAmount() : BigDecimal.ZERO;
        BigDecimal taxRate = new BigDecimal("0.06"); // 6%税率
        BigDecimal tax = amount.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal untaxedAmount = amount.subtract(tax).setScale(2, RoundingMode.HALF_UP);

        Cell itemNameCell = new Cell().add(new Paragraph(content)
                .setFont(font)
                .setFontSize(14))
                .add(new Paragraph("Parking Service Fee")
                        .setFont(font)
                        .setFontSize(11)
                        .setFontColor(ColorConstants.GRAY))
                .setTextAlignment(TextAlignment.LEFT)
                .setPadding(16)
                .setBorderBottom(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        itemTable.addCell(itemNameCell);
        
        Cell qtyCell = new Cell().add(new Paragraph("1")
                .setFont(font)
                .setFontSize(14))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(16)
                .setBorderBottom(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        itemTable.addCell(qtyCell);
        
        Cell priceCell = new Cell().add(new Paragraph(untaxedAmount.toString())
                .setFont(font)
                .setFontSize(14))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(16)
                .setBorderBottom(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        itemTable.addCell(priceCell);
        
        Cell amountCell = new Cell().add(new Paragraph(untaxedAmount.toString())
                .setFont(font)
                .setFontSize(14)
                .setBold())
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(16)
                .setBorderBottom(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        itemTable.addCell(amountCell);
        
        Cell taxRateCell = new Cell().add(new Paragraph("6%")
                .setFont(font)
                .setFontSize(14))
                .setTextAlignment(TextAlignment.CENTER)
                .setPadding(16)
                .setBorderBottom(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        itemTable.addCell(taxRateCell);
        
        Cell taxCell = new Cell().add(new Paragraph(tax.toString())
                .setFont(font)
                .setFontSize(14))
                .setTextAlignment(TextAlignment.RIGHT)
                .setPadding(16)
                .setBorderBottom(new DashedBorder(ColorConstants.LIGHT_GRAY, 1));
        itemTable.addCell(taxCell);

        document.add(itemTable);
        document.add(new Paragraph(" ")); // 空行

        // 底部信息
        Table footerTable = new Table(UnitValue.createPercentArray(new float[]{60, 40}));
        footerTable.setWidth(UnitValue.createPercentValue(100));
        footerTable.setMarginTop(48f);

        // 备注
        Cell remarksCell = new Cell().add(new Paragraph("备注 Remarks")
                .setFont(font)
                .setFontSize(11)
                .setFontColor(ColorConstants.GRAY))
                .add(new Paragraph(" "))
                .add(new Paragraph("订单号: " + (invoice.getOrderNo() != null ? invoice.getOrderNo() : ""))
                        .setFont(font)
                        .setFontSize(14))
                .add(new Paragraph("发票类型: " + getInvoiceTypeName(invoice.getInvoiceType()))
                        .setFont(font)
                        .setFontSize(14))
                .add(new Paragraph("开票状态: " + getStatusName(invoice.getStatus()))
                        .setFont(font)
                        .setFontSize(14))
                .setBorder(new SolidBorder(ColorConstants.LIGHT_GRAY, 1))
                .setBackgroundColor(new com.itextpdf.kernel.colors.DeviceGray(0.95f))
                .setPadding(20);
        footerTable.addCell(remarksCell);

        // 金额统计
        Cell footerAmountCell = new Cell().add(new Paragraph("合计金额 (不含税) <span style='font-size: 11px; margin-left: 4px;'>Subtotal</span>")
                .setFont(font)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("¥ " + untaxedAmount)
                        .setFont(font)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph(" "))
                .add(new Paragraph("合计税额 <span style='font-size: 11px; margin-left: 4px;'>Total Tax</span>")
                        .setFont(font)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph("¥ " + tax)
                        .setFont(font)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.RIGHT))
                .add(new Paragraph(" "))
                .add(new Paragraph(" "))
                // 红色背景的价税合计
                .add(new Table(UnitValue.createPercentArray(new float[]{1, 1}))
                        .addCell(new Cell().add(new Paragraph("价税合计")
                                .setFont(font)
                                .setFontSize(16)
                                .setBold()
                                .setTextAlignment(TextAlignment.LEFT)
                                .setFontColor(ColorConstants.WHITE))
                                .add(new Paragraph("Total Amount")
                                        .setFont(font)
                                        .setFontSize(11)
                                        .setTextAlignment(TextAlignment.LEFT)
                                        .setFontColor(new com.itextpdf.kernel.colors.DeviceGray(0.7f)))
                                .setBorder(null)
                                .setPadding(20))
                        .addCell(new Cell().add(new Paragraph("¥ " + amount)
                                .setFont(font)
                                .setFontSize(32)
                                .setBold()
                                .setTextAlignment(TextAlignment.RIGHT)
                                .setFontColor(ColorConstants.WHITE))
                                .setBorder(null)
                                .setPadding(20))
                        .setWidth(UnitValue.createPercentValue(100))
                        .setBackgroundColor(ColorConstants.RED)
                        .setBorderRadius(new com.itextpdf.layout.properties.BorderRadius(8)))
                .setBorder(null)
                .setPadding(0);
        footerTable.addCell(footerAmountCell);

        document.add(footerTable);

        document.close();
        return outputStream.toByteArray();
    }



    private static String generateInvoiceNo() {
        // 生成16位发票号码
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append((int) (Math.random() * 10));
        }
        return sb.toString();
    }

    private static String getInvoiceTypeName(Integer type) {
        if (type == null) return "个人普票";
        switch (type) {
            case 1: return "企业普票";
            case 2: return "企业专票";
            case 3: return "个人普票";
            default: return "个人普票";
        }
    }

    private static String getStatusName(Integer status) {
        if (status == null) return "待开具";
        switch (status) {
            case 0: return "待开具";
            case 1: return "已开具";
            case 2: return "开具失败";
            default: return "待开具";
        }
    }
}