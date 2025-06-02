package com.firsthotel.restaurant.controller;

import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.restaurant.dto.OrderCountDto;
import com.firsthotel.restaurant.dto.RestaurantBlacklistDto;
import com.firsthotel.restaurant.dto.RestaurantOrderDto;
import com.firsthotel.restaurant.model.RestaurantBlacklist;
import com.firsthotel.restaurant.model.RestaurantOrder;
import com.firsthotel.restaurant.model.RestaurantOrderRepository;
import com.firsthotel.restaurant.model.RestaurantTable;
import com.firsthotel.restaurant.model.RestaurantTimeslot;
import com.firsthotel.restaurant.service.MailService;
import com.firsthotel.restaurant.service.RestaurantBlacklistService;
import com.firsthotel.restaurant.service.RestaurantOrderService;
import com.firsthotel.restaurant.service.RestaurantTableService;
import com.firsthotel.restaurant.service.RestaurantTimslotService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/firstHotel/restaurant")
public class RestaurantReserveController {

	@Autowired
	private RestaurantOrderService rOrderService;

	@Autowired
	private RestaurantTimslotService rTimslotService;

	@Autowired
	private RestaurantTableService rTableService;

	@Autowired
	private MailService mailService;

	@Autowired
	private RestaurantBlacklistService rBlacklistService;
	
	@Autowired
	private RestaurantOrderRepository rOrderRepository;

	// 預約訂位
	@PostMapping("/reserve")
	public ResponseEntity<?> reserve(@RequestBody RestaurantOrderDto rOrderDto) {

		System.out.println("收到資料: " + rOrderDto);
		System.out.println("slotId: " + rOrderDto.getSlotId());
		System.out.println("tableIds: " + rOrderDto.getTableIds());
		// 建立新訂單物件
		RestaurantOrder restaurantOrder = new RestaurantOrder();

		// 填入基本資料
		restaurantOrder.setCustomerName(rOrderDto.getCustomerName());
		restaurantOrder.setPhone(rOrderDto.getPhone());
		restaurantOrder.setMail(rOrderDto.getMail());
		restaurantOrder.setAdult(rOrderDto.getAdult());
		restaurantOrder.setChild(rOrderDto.getChild());
		restaurantOrder.setOrderdate(rOrderDto.getOrderdate());
		restaurantOrder.setNote(rOrderDto.getNote());
		restaurantOrder.setCreatedTime(LocalDateTime.now());

		// 取得時段資料
		Optional<RestaurantTimeslot> timeSlotData = rTimslotService.findAllTimeslotById(rOrderDto.getSlotId());
		if (timeSlotData.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("找不到指定的用餐時段 ID: " + rOrderDto.getSlotId());
		}
		System.out.println("時段查詢結果: " + timeSlotData.isPresent());
		// 取得座位資料
		List<RestaurantTable> tableData = rTableService.findAllTableById(rOrderDto.getTableIds());
		if (tableData == null || tableData.isEmpty()) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("未選擇任何座位，請重新選擇");
		}

		System.out.println("座位查詢結果: " + (tableData != null && !tableData.isEmpty()));

		// 設定時段與座位
		restaurantOrder.setRestaurantTimeslot(timeSlotData.get());
		restaurantOrder.setTables(tableData);

		// 儲存訂單
		rOrderService.reserveOrders(restaurantOrder);

		// 發送 Email 通知
		mailService.sendOrderMail(rOrderDto.getMail(), restaurantOrder.getCustomerName(), rOrderDto.getOrderdate(),
				timeSlotData.get().getStartTime().toString());

		return ResponseEntity.ok(restaurantOrder);
	}

	// 查詢時段空座位
	@GetMapping("/findreservedseat")
	public List<Integer> getMethodName(@RequestParam("slotId") Integer slotId,
			@RequestParam("orderdate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate orderdate) {
		List<Integer> finddEmptySeat = rOrderService.finddEmptySeat(slotId, orderdate);
		Set<Integer> tableIds = new HashSet<>(finddEmptySeat); // 用 Set 自動去重複

		return new ArrayList<Integer>(tableIds); // 轉成 List 回傳

	}

	// 抓黑名單
	@GetMapping("/catchBlacklist")
	public ResponseEntity<?> getBlacklist(@RequestParam String phone) {

		Optional<RestaurantBlacklist> blacklist = rBlacklistService.findBlacklists(phone);
		if (blacklist.isPresent()) {
			RestaurantBlacklistDto blacklistData = new RestaurantBlacklistDto(blacklist.get().getPhone(),
					blacklist.get().getReason(), blacklist.get().getBlackliststatus());
			return ResponseEntity.ok(blacklistData);
		} else {
			RestaurantBlacklistDto blacklistData = new RestaurantBlacklistDto(phone, null, 0);
			return ResponseEntity.ok(blacklistData);
		}

	}
	
	//前台查詢全部
	@GetMapping("/searchOrder")
	public List<RestaurantOrder> findByPhoneAndMail(@RequestParam String phone, @RequestParam String email) {
		return rOrderService.findByPhoneAndMail(phone, email);
	}
	
	
	

	// 計算未到次數
	@GetMapping("/checkBlacklist")
	public ResponseEntity<Map<String, Boolean>> getMethodName(@RequestParam String phone) {
		int absentNum = rOrderService.countPhoneOrderStatus(phone);

		boolean isBlacklisted = absentNum >= 3;

		Map<String, Boolean> response = new HashMap<>();
		response.put("blacklisted", isBlacklisted);

		return ResponseEntity.ok(response);
	}

	
	//計算當天有幾筆訂位
	@GetMapping("/orders-per-day")
	public List<OrderCountDto> getOrdersPerDay() {
	    return rOrderRepository.countOrdersGroupByDate();
	}
	
}
