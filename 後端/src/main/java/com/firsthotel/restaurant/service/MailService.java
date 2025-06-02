package com.firsthotel.restaurant.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.firsthotel.restaurant.model.RestaurantOrder;


@Service
public class MailService {
	 @Autowired
	    private JavaMailSender mailSender;
	 @Autowired
	 private RestaurantOrderService rOrderService;
	 
	 //訂位成功
	 public void sendOrderMail(String mail, String name
			 , LocalDate date, String time) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(mail);
	        message.setSubject("First Hotel訂位成功通知");
	        message.setText("親愛的 " + name + " 您好，\n" +
	                "您已成功訂位！\n" +
	                "日期：" + date + "\n" +
	                "時段：" + time + "\n" +
	                "感謝您的訂位！\n p.s.愛你");

	        mailSender.send(message);
	    }
	 //使用綠界付訂金成功
	 public void sendPaidMail(String merchantTradeNo,String tradeAmt,String PaymentDate) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        
	        Optional<RestaurantOrder> op = rOrderService.findByResMerchantTradeNo(merchantTradeNo);
	        if(op.isPresent()) {
	        	String mail = op.get().getMail();
	        String name = op.get().getCustomerName();

	        
	        message.setTo(mail);
	        message.setSubject("First Hotel訂位成功通知");
	        message.setText("親愛的 " + name + " 您好，\n" +
	                "在"+PaymentDate+"收到您的"+tradeAmt+"元的訂金付款！\n" +
	                
	                "感謝您的訂位！\n p.s.愛你");
}
	        mailSender.send(message);
	    }
	 
}
