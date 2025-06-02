package com.firsthotel.restaurant.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.restaurant.model.RestaurantOrder;
import com.firsthotel.restaurant.service.MailService;
import com.firsthotel.restaurant.service.RestaurantOrderService;
import com.firsthotel.restaurant.util.CheckMacValueCalculat;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/RestaurantEcpay")
@CrossOrigin(origins = "http://localhost:5173")
public class RestaurantECPayController {

	@Autowired
	private RestaurantOrderService rOrderService; 
	
	@Autowired
	private MailService mailService;
	
	// 取得checkMacValue
	 @PostMapping("/getCheckMacValue")
	 public String getCheckMacValue(@RequestBody Map<String, String> params) {
	     String checkMacValue = CheckMacValueCalculat.genCheckMacValue(params);
	     return checkMacValue;
	 }

	    
	    
	    
	    @PostMapping("/notify")
	    public String handleNotification(HttpServletRequest request) {
	     // 綠界會用 POST 傳資料過來，你可以依據訂單號碼更新狀態
	     String merchantTradeNo = request.getParameter("MerchantTradeNo");
	     String rtnCode = request.getParameter("RtnCode");
	     String tradeAmt = request.getParameter("TradeAmt");
	     String PaymentDate=request.getParameter("PaymentDate");
	     System.out.println("收到付款通知：訂單編號 " + merchantTradeNo + ", 狀態碼 " + rtnCode + ", 金額 " + tradeAmt);
	     
	     if ("1".equals(rtnCode)) {
	      // 表示付款成功，可以更新訂單狀態為已付款
	    	 rOrderService.updatePayedOrderStatus(merchantTradeNo);
	    	 mailService.sendPaidMail(merchantTradeNo,tradeAmt,PaymentDate);
	    	 System.out.println("付款成功");
	     }
	     
	     return "1|OK"; // 綠界要求這樣回應表示已收到通知
	    }
}
