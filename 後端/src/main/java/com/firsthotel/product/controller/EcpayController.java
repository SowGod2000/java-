package com.firsthotel.product.controller;

import com.firsthotel.product.model.Order;
import com.firsthotel.product.service.EcpayService;
import com.firsthotel.product.service.OrderService;
import com.firsthotel.product.util.EcpayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/ecpay")
public class EcpayController {

    private final EcpayService ecpayService;
    private final OrderService orderService;

    public EcpayController(EcpayService ecpayService, OrderService orderService) {
        this.ecpayService = ecpayService;
        this.orderService = orderService;
    }

    @GetMapping("/payment-info")
    public ResponseEntity<?> getPaymentInfo(@RequestParam Long orderId) {
        try {
            Order order = orderService.findById(orderId);
            if (order == null) {
                return ResponseEntity.status(404).body("找不到訂單");
            }

            String htmlForm = ecpayService.generateEcpayForm(order);
            if (htmlForm == null || htmlForm.trim().isEmpty()) {
                return ResponseEntity.status(500).body("綠界表單產生失敗");
            }

            Map<String, String> formData = EcpayUtil.extractFormInputs(htmlForm);
            if (formData.isEmpty()) {
                return ResponseEntity.status(500).body("綠界表單解析失敗");
            }

            return ResponseEntity.ok(formData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("金流錯誤：" + e.getMessage());
        }
    }

    @PostMapping("/notify")
    public String handleEcpayNotify(HttpServletRequest request) {
        try {
            Map<String, String[]> paramMap = request.getParameterMap();
            String rtnCode = getFirstValue(paramMap, "RtnCode");
            String merchantTradeNo = getFirstValue(paramMap, "MerchantTradeNo");
            String tradeNo = getFirstValue(paramMap, "TradeNo");

            if (!merchantTradeNo.startsWith("EC")) {
                return "0|FAIL";
            }

            Long orderId = Long.parseLong(merchantTradeNo.replace("EC", ""));
            Order order = orderService.findById(orderId);

            if (order != null && "1".equals(rtnCode)) {
                order.setPaymentStatus("PAID");
                order.setMerchantTradeNo(tradeNo);
                order.setPaidAt(LocalDateTime.now());
                orderService.save(order);
            }

            return "1|OK";
        } catch (Exception e) {
            return "0|FAIL";
        }
    }

    private String getFirstValue(Map<String, String[]> paramMap, String key) {
        String[] values = paramMap.get(key);
        return (values != null && values.length > 0) ? values[0] : "";
    }
}
