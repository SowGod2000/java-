package com.firsthotel.product.controller;

import com.firsthotel.product.repository.OrderRepository;
import com.firsthotel.product.service.DashboardService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final OrderRepository orderRepository;
    private final DashboardService dashboardService;

    public DashboardController(OrderRepository orderRepository, DashboardService dashboardService) {
        this.orderRepository = orderRepository;
        this.dashboardService = dashboardService;
    }

    // ✅ 回傳今日成交金額、本月訂單數、總收入
    @GetMapping(value = "/summary", produces = "application/json")
    public Map<String, Object> getSummary() {
        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        YearMonth month = YearMonth.from(today);

        BigDecimal todayTotal = orderRepository.sumTotalAmountByDate(today.atStartOfDay(), now);
        Long thisMonthOrders = orderRepository.countPaidOrdersInMonth(month.getYear(), month.getMonthValue());
        BigDecimal totalRevenue = orderRepository.sumTotalRevenue();

        Map<String, Object> result = new HashMap<>();
        result.put("todayTotal", todayTotal != null ? todayTotal : BigDecimal.ZERO);
        result.put("thisMonthOrders", thisMonthOrders != null ? thisMonthOrders : 0L);
        result.put("totalRevenue", totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
        return result;
    }

    // ✅ 每月銷售額（折線圖）
    @GetMapping(value = "/monthly-sales", produces = "application/json")
    public List<Map<String, Object>> getMonthlySales() {
        return orderRepository.getMonthlySales();
    }

    // ✅ 商品分類銷售分佈（圓餅圖）
    @GetMapping(value = "/category-sales", produces = "application/json")
    public List<Map<String, Object>> getCategorySales() {
        return orderRepository.getCategorySales();
    }

    // ✅ 標籤銷售分佈（圓餅圖）
    @GetMapping(value = "/tag-sales", produces = "application/json")
    public List<Map<String, Object>> getTagSales() {
        return dashboardService.getTagSales();
    }

    // ✅ 匯出 Excel 報表
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportDashboardData() {
        try {
            byte[] excelFile = dashboardService.exportDashboardDataAsExcel();
            String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String filename = "dashboard_report_" + today + ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);

            return new ResponseEntity<>(excelFile, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
