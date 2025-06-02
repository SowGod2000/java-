package com.firsthotel.restaurant.controller;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.restaurant.dto.RestaurantOrderDto;
import com.firsthotel.restaurant.dto.RestaurantOrderUpdateDto;
import com.firsthotel.restaurant.model.RestaurantBlacklist;
import com.firsthotel.restaurant.model.RestaurantOrder;
import com.firsthotel.restaurant.model.RestaurantTable;
import com.firsthotel.restaurant.model.RestaurantTimeslot;
import com.firsthotel.restaurant.service.MailService;
import com.firsthotel.restaurant.service.RestaurantBlacklistService;
import com.firsthotel.restaurant.service.RestaurantOrderService;
import com.firsthotel.restaurant.service.RestaurantTableService;
import com.firsthotel.restaurant.service.RestaurantTimslotService;
import com.firsthotel.restaurant.util.tools;

import jakarta.servlet.http.HttpServletResponse;
import java.io.OutputStream;




@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/firstHotel/restaurantManage")
@RestController
public class RestaurantManage {
	
	@Autowired
	RestaurantOrderService rOrderService;
	@Autowired
	private RestaurantTimslotService rTimslotService;

	@Autowired
	private RestaurantTableService rTableService;

	@Autowired
	private MailService mailService;
	
	@Autowired
	private RestaurantBlacklistService rBlacklistService;
	
	@GetMapping("/RestaurantOrders")
	public List<RestaurantOrder> getAllRestaurantOrders(){
		
		return rOrderService.findAllorders();
	}
	
	//軟刪除
		@PostMapping("cancelReserve")
		public RestaurantOrder cancelRestaurantOrder(@RequestBody RestaurantOrder rOrder) {
			
			return rOrderService.cancelOrder(rOrder);
		}
//更新報到
		@PostMapping("/checkin")
		public RestaurantOrder restaurantCheckin(@RequestBody RestaurantOrder rOrder) {
			
			return rOrderService.restaurantCheckin(rOrder.getOrderId());
			
		}
		//更新未到
		@PostMapping("/absent")
		public RestaurantOrder  restataurantabsent(@RequestBody RestaurantOrder rOrder) {
			
			
			return rOrderService.restaurantAbsent(rOrder);
		}
		//修改訂單
		@PutMapping("/updateReserve")
		public RestaurantOrder updateReserve(@RequestBody RestaurantOrderUpdateDto dto) {
			return rOrderService.updateOrder(dto);
		}	
		
		//預約訂位
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
		//黑名單查詢
		@GetMapping("/findblacklist")
		public List<RestaurantBlacklist> getAllblacklist(){
			
			return rOrderService.findAllBlacklists();
		}
		//黑名單解ban
		@PostMapping("/unbanBlacklist")
		public ResponseEntity<?> unban(@RequestBody RestaurantBlacklist blacklist){
			boolean success = rBlacklistService.unban(blacklist.getBlacklistId());
	        if (success) {
	            return ResponseEntity.ok("已解除黑名單");
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND)
	                    .body("找不到 黑名單 ID：" + blacklist.getBlacklistId());
	        }
		}
		//查詢訂位狀態是取消訂位
		@GetMapping("/findorderstatus")
		public List<RestaurantOrder> findorderstatuscacancel() {
			return rOrderService.findorderstatuscacancel();
		}
		//匯出CSV
		@GetMapping("/exportOrderCsv")
		public void exportOrderCsv(HttpServletResponse response) {
			
			response.setContentType("text/csv;charset=UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=RestaurantOrder.csv");
			
			List<RestaurantOrder> allRestaurantOrders = rOrderService.findAllRestaurantOrders();
			
			try {
				// 加入BOM，讓Excel知道是UTF-8
				OutputStream outputStream = response.getOutputStream();
		        outputStream.write(0xEF);
		        outputStream.write(0xBB);
		        outputStream.write(0xBF);

		        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				//表頭
				writer.println("訂位編號,顧客名稱,電話,Email,訂位日期,預約時段,大人人數,小孩人數,預估金額,訂金,備註,訂位狀態");
				//每筆資料
				for(RestaurantOrder order:allRestaurantOrders) {
					writer.printf("%d,%s,%s,%s,%s,%s,%d,%d,%d,%d,%s,%s%n",
							order.getOrderId(),order.getCustomerName(),tools.escapeCsv(order.getPhone()),order.getMail(),
							order.getOrderdate(),order.getRestaurantTimeslot().getStartTime(),order.getAdult(),order.getChild(),
							order.getExpectedPrice(),order.getDeposit(),tools.escapeCsv(order.getNote()),order.getOrderStatus());
					
					
				}
				writer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		
		}
		
		
		
		
		
		
}
