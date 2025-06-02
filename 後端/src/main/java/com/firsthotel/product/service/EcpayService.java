package com.firsthotel.product.service;

import com.firsthotel.ecpay.AllInOne;
import com.firsthotel.ecpay.AioCheckOutALL;
import org.springframework.stereotype.Service;
import com.firsthotel.product.model.Order;
import com.firsthotel.product.model.OrderItem;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class EcpayService {

    public String generateEcpayForm(Order order) {
        AllInOne all = new AllInOne(""); // SDK 內建類別

        AioCheckOutALL obj = new AioCheckOutALL();
        obj.setMerchantTradeNo("EC" + order.getOrderId()); // ✅ 用訂單 ID 保證唯一性
        obj.setMerchantTradeDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

        // ✅ 避免小數金額被綠界拒絕
        int amount = order.getAmount();
        obj.setTotalAmount(String.valueOf(amount));

        obj.setTradeDesc("綠界線上購物測試");

        // ✅ 顯示完整商品資訊並換行
        String itemName = buildItemName(order.getItems());
        obj.setItemName(itemName);

        obj.setReturnURL("https://8454-2401-e180-8820-b7b5-4807-e972-6c67-1ed1.ngrok-free.app/api/ecpay/notify");

        obj.setClientBackURL("http://localhost:5173/order/" + order.getOrderId());
        obj.setNeedExtraPaidInfo("N");

        String html = all.aioCheckOut(obj, null);

        System.out.println("✅ ECPay HTML 表單：\n" + html);
        return html;
    }

    private String buildItemName(List<OrderItem> items) {
        StringBuilder sb = new StringBuilder();
        for (OrderItem item : items) {
            sb.append(item.getProductName())
              .append(" x")
              .append(item.getQuantity())
              .append(" NT$")
              .append(item.getPrice())
              .append("｜"); // 使用全形直線分隔
        }

        // 移除最後一個分隔符號
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }

        return sb.toString();
    }

}
