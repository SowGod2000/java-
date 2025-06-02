package com.firsthotel.product.service;

import com.firsthotel.product.repository.OrderRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final OrderRepository orderRepository;

    public DashboardService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public byte[] exportDashboardDataAsExcel() throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            createSummarySheet(workbook);
            createMonthlySalesSheet(workbook);
            createCategorySalesSheet(workbook);
            createTagSalesSheet(workbook); // ✅ 加入標籤銷售報表
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    private void createSummarySheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("今日概況");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        LocalDateTime now = LocalDateTime.now();
        BigDecimal todayTotal = orderRepository.sumTotalAmountByDate(now.toLocalDate().atStartOfDay(), now);
        Long thisMonthOrders = orderRepository.countPaidOrdersInMonth(now.getYear(), now.getMonthValue());
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();

        Row timeRow = sheet.createRow(0);
        timeRow.createCell(0).setCellValue("下載時間：" + now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

        Row header = sheet.createRow(2);
        header.createCell(0).setCellValue("指標");
        header.createCell(1).setCellValue("數值");
        header.getCell(0).setCellStyle(headerStyle);
        header.getCell(1).setCellStyle(headerStyle);

        String[][] data = {
            {"今日總收入", String.valueOf(todayTotal != null ? todayTotal : BigDecimal.ZERO)},
            {"本月訂單數量", String.valueOf(thisMonthOrders != null ? thisMonthOrders : 0L)},
            {"本月累積收入", String.valueOf(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)}
        };

        for (int i = 0; i < data.length; i++) {
            Row row = sheet.createRow(i + 3);
            for (int j = 0; j < data[i].length; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue(data[i][j]);
                cell.setCellStyle(cellStyle);
            }
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createMonthlySalesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("每月銷售趨勢");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        List<Map<String, Object>> monthlySales = orderRepository.getMonthlySales();

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("月份");
        header.createCell(1).setCellValue("銷售總額");
        header.getCell(0).setCellStyle(headerStyle);
        header.getCell(1).setCellStyle(headerStyle);

        for (int i = 0; i < monthlySales.size(); i++) {
            Map<String, Object> record = monthlySales.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.get("month").toString());
            row.createCell(1).setCellValue(record.get("total").toString());
            row.getCell(0).setCellStyle(cellStyle);
            row.getCell(1).setCellStyle(cellStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createCategorySalesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("分類銷售分佈");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        List<Map<String, Object>> categorySales = orderRepository.getCategorySales();

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("分類名稱");
        header.createCell(1).setCellValue("銷售總額");
        header.getCell(0).setCellStyle(headerStyle);
        header.getCell(1).setCellStyle(headerStyle);

        for (int i = 0; i < categorySales.size(); i++) {
            Map<String, Object> record = categorySales.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.get("category").toString());
            row.createCell(1).setCellValue(record.get("total").toString());
            row.getCell(0).setCellStyle(cellStyle);
            row.getCell(1).setCellStyle(cellStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void createTagSalesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("標籤銷售分佈");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle cellStyle = createCellStyle(workbook);

        List<Map<String, Object>> tagSales = orderRepository.getTagSales();

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("標籤名稱");
        header.createCell(1).setCellValue("銷售總額");
        header.getCell(0).setCellStyle(headerStyle);
        header.getCell(1).setCellStyle(headerStyle);

        for (int i = 0; i < tagSales.size(); i++) {
            Map<String, Object> record = tagSales.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(record.get("tag").toString());
            row.createCell(1).setCellValue(record.get("total").toString());
            row.getCell(0).setCellStyle(cellStyle);
            row.getCell(1).setCellStyle(cellStyle);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    private CellStyle createCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // ✅ 提供給 /tag-sales 圖表使用
    public List<Map<String, Object>> getTagSales() {
        return orderRepository.getTagSales();
    }
}
