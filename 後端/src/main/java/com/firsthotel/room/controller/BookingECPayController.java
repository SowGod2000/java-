package com.firsthotel.room.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.room.model.BookingOrder;
import com.firsthotel.room.service.BookingOrderService;
import com.firsthotel.room.util.CheckMacValueCalculator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;


@RestController
@RequestMapping("/ecpay")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingECPayController {
	
	@Autowired
	private BookingOrderService bookingOrderService;
	
    // 取得checkMacValue
	@PostMapping("/getCheckMacValue")
	public String getCheckMacValue(@RequestBody Map<String, String> params) {
	    String checkMacValue = CheckMacValueCalculator.genCheckMacValue(params);
	    return checkMacValue;
	}

    
    @PostMapping("/notify")
    @Transactional
    public String handleNotification(HttpServletRequest request) {
    	// 綠界會用 POST 傳資料過來，你可以依據訂單號碼更新狀態
    	String merchantTradeNo = request.getParameter("MerchantTradeNo");
    	String rtnCode = request.getParameter("RtnCode");
    	String tradeAmt = request.getParameter("TradeAmt");
    	String orderId = request.getParameter("CustomField1");
    	System.out.println("收到付款通知：訂單編號 " + merchantTradeNo + ", 狀態碼 " + rtnCode + ", 金額 " + tradeAmt);
    	System.out.println("-------------------------------------------------------------");
    	System.out.println("訂單編號: " + orderId);
    	if ("1".equals(rtnCode)) {
    		// 表示付款成功，可以更新訂單狀態為已付款
    		System.out.println("付款成功");
    		BookingOrder order = bookingOrderService.findById(Integer.parseInt(orderId));
    		order.setPaymentStatus("已付款");
    		order.setPaymentMethod("信用卡");
    	}
    	
    	return "1|OK"; // 綠界要求這樣回應表示已收到通知
    }
    
    
}
