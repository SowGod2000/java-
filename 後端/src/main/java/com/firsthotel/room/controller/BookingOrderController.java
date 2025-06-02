package com.firsthotel.room.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.room.dto.BookingOrderDto;
import com.firsthotel.room.model.BookingOrder;
import com.firsthotel.room.service.BookingOrderService;
import com.firsthotel.room.service.BookingMailService;
import com.firsthotel.room.service.RoomtypeService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;




@RestController
@RequestMapping("/bookingOrder")
@CrossOrigin(origins = "http://localhost:5173")
public class BookingOrderController {
	
	@Autowired
	private BookingOrderService bookingOrderService;
	
	@Autowired
	private RoomtypeService roomtypeService;
	
	@Autowired
	private BookingMailService mailService;
	
	//查詢全部
	@GetMapping("/findAll")
	public List<BookingOrder> findAll() {
		return bookingOrderService.findAll();
	}
	
	 //查詢單筆(修改用)
	@GetMapping("/findById/{bookingOrderId}")
	public BookingOrder findById(@PathVariable Integer bookingOrderId) {
		BookingOrder order = bookingOrderService.findById(bookingOrderId);
		System.out.println(order);
		return order;
		
	}
	
	//查詢會員訂單
	@GetMapping("/findByMemberId/{memberId}")
	public List<BookingOrder> findByMemberId(@PathVariable Integer memberId) {
		List<BookingOrder> orders = bookingOrderService.findByMemberId(memberId);
		System.out.println(orders);
		return orders;
	}
	
	//會員取消訂單
	@PutMapping("/cancel/{bookingOrderId}")
	@Transactional
	public BookingOrder cancelOrder(@PathVariable Integer bookingOrderId) {
		BookingOrder order = bookingOrderService.findById(bookingOrderId);
		order.setStatus("已取消");
		order.setPaymentStatus("已退款");
		return order;
	}
	
	
	//新增
	@PostMapping("/insert")
	public BookingOrder insertBookingOrder(@RequestBody BookingOrderDto dto) {
		BookingOrder bookingOrder = new BookingOrder();
		bookingOrder.setMemberId(dto.getMemberId());
		bookingOrder.setCheckInDate(dto.getCheckInDate());
		bookingOrder.setCheckOutDate(dto.getCheckOutDate());
		bookingOrder.setRoomtype(roomtypeService.findById(dto.getRoomtypeId()));
		bookingOrder.setPricePerRoom(dto.getPricePerRoom());
		bookingOrder.setQuantity(dto.getQuantity());
		bookingOrder.setTotalPrice(dto.getTotalPrice());
		bookingOrder.setStatus("已成立");
		bookingOrder.setPaymentStatus("未付款");
		bookingOrder.setPaymentMethod("無");
		BookingOrder insertBookingOrder = bookingOrderService.insertBookingOrder(bookingOrder);
		
		// 將日期格式化為 yyyy/MM/dd
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
	    String checkInStr = sdf.format(insertBookingOrder.getCheckInDate());
	    String checkOutStr = sdf.format(insertBookingOrder.getCheckOutDate());
		
		//接下來要寄送mail
		String userMail = "johson831229@gmail.com";
		String subject = "【First Hotel】您的訂房已成功成立！";
		String content = "親愛的 " + "誠誠" + " 您好，\n感謝您使用 First Hotel 的線上訂房服務，您的訂房已成功成立，以下是您的訂房資訊：\n\n"
					+"訂單編號: " + insertBookingOrder.getBookingOrderId() +"\n"
					+"入住日期: " + checkInStr + "\n"
					+"退房日期: " + checkOutStr + "\n"
					+"房型: " + insertBookingOrder.getRoomtype().getRoomtypeName() + "\n"
					+"房間數量: " + insertBookingOrder.getQuantity() + " 間\n"
					+"總金額: " + insertBookingOrder.getTotalPrice() + " 元\n\n"
					+"我們期待為您提供舒適的住宿體驗，祝您旅途愉快！\n"
					+"敬祝\n"
					+"平安順心\n"
					+"First Hotel 客戶服務團隊 敬上";
		System.out.println("有送出嗎");
		mailService.sendPlainText(userMail, subject, content);
		System.out.println("寄出了吧");
		return insertBookingOrder;
	}
	
	
	//修改
	@PutMapping("/update/{bookingOrderId}")
	@Transactional
	public BookingOrder update(
			@PathVariable Integer bookingOrderId, 
			@RequestBody BookingOrder updateBookingOrder) {
		BookingOrder order = bookingOrderService.findById(bookingOrderId);
		order.setPricePerRoom(updateBookingOrder.getPricePerRoom());
		order.setTotalPrice(updateBookingOrder.getTotalPrice());
		order.setStatus(updateBookingOrder.getStatus());
		order.setPaymentStatus(updateBookingOrder.getPaymentStatus());
		order.setPaymentMethod(updateBookingOrder.getPaymentMethod());
		order.setNote(updateBookingOrder.getNote());
		return order;
	}
	
	
	//入住按鈕
	@PutMapping("/checkIn/{bookingOrderId}")
	@Transactional
	public BookingOrder checkin(@PathVariable Integer bookingOrderId) {
		BookingOrder order = bookingOrderService.findById(bookingOrderId);
		LocalDateTime now = LocalDateTime.now();
		order.setCheckInTime(now);
		return order;
	}
	
	
	//退房按鈕
	@PutMapping("/checkOut/{bookingOrderId}")
	@Transactional
	public BookingOrder checkOut(@PathVariable Integer bookingOrderId) {
		BookingOrder order = bookingOrderService.findById(bookingOrderId);
		LocalDateTime now = LocalDateTime.now();
		order.setCheckOutTime(now);
		return order;
	}
	
	
}
