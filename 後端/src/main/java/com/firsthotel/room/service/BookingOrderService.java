package com.firsthotel.room.service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.firsthotel.room.model.BookingOrder;
import com.firsthotel.room.model.BookingOrderRepository;

@Service
public class BookingOrderService {
	
	@Autowired
	private BookingOrderRepository bookingOrderRepository;
	
	//查詢全部(訂單編號從大到小)(訂單從新到舊顯示)
	public List<BookingOrder> findAll(){
		return bookingOrderRepository.selectAll();
	}
	
	//查詢單筆
	public BookingOrder findById(Integer id) {
		Optional<BookingOrder> op = bookingOrderRepository.findById(id);
		if(op.isPresent()) {
			return op.get();
		}
		return null;
	}
	
	//查詢會員訂單
	public List<BookingOrder> findByMemberId(Integer memberId){
		return bookingOrderRepository.findByMemberId(memberId);
	}
	
	//新增訂單
	public BookingOrder insertBookingOrder(BookingOrder bookingOrder) {
		return bookingOrderRepository.save(bookingOrder);
	}
	
	
}
