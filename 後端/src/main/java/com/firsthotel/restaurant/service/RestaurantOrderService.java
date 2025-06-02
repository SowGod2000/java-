package com.firsthotel.restaurant.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import com.firsthotel.restaurant.dto.RestaurantOrderDto;
import com.firsthotel.restaurant.dto.RestaurantOrderUpdateDto;
import com.firsthotel.restaurant.model.RestaurantBlacklist;
import com.firsthotel.restaurant.model.RestaurantBlacklistRepository;
import com.firsthotel.restaurant.model.RestaurantOrder;
import com.firsthotel.restaurant.model.RestaurantOrderRepository;
import com.firsthotel.restaurant.model.RestaurantTable;
import com.firsthotel.restaurant.model.RestaurantTableRepository;
import com.firsthotel.restaurant.model.RestaurantTimeslot;
import com.firsthotel.restaurant.model.RestaurantTimeslotRespository;

@Service
public class RestaurantOrderService {

    private final RestaurantBlacklistService restaurantBlacklistService;


	
	@Autowired
	RestaurantOrderRepository restaurantOrderRepository;
	
@Autowired
RestaurantTableRepository rTableRepository;
@Autowired
RestaurantTimeslotRespository rTimeslotRespository;
@Autowired
RestaurantBlacklistRepository rBlacklistRepository;


    RestaurantOrderService(RestaurantBlacklistService restaurantBlacklistService) {
        this.restaurantBlacklistService = restaurantBlacklistService;
    }

   
	
  
	//新增
	public RestaurantOrder reserveOrders(RestaurantOrder rOrder){
		rOrder.setExpectedPrice(rOrder.getAdult()*1000+rOrder.getChild()*500);
		rOrder.setDeposit((int)(rOrder.getExpectedPrice()*0.1));
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		rOrder.setResMerchantTradeNo("Ord"+formatter.format(new Date()));
		return restaurantOrderRepository.save(rOrder);
	}
	//查詢時段空位
	public List<Integer> finddEmptySeat(Integer slotId, LocalDate orderdate){
		return restaurantOrderRepository.findUsedSeat(slotId,orderdate);
	}
	
	
	//計算該筆電話orderStatus
	public int countPhoneOrderStatus(String phone ) {
		String orderStatus="未到";
		return	restaurantOrderRepository.countByPhoneAndOrderStatus(phone, orderStatus);
	}
	
	//查詢全部
	public List<RestaurantOrder> findAllorders(){
		return restaurantOrderRepository.findByOrderStatusNot("取消訂位");
	}
	
	//軟刪除
	public RestaurantOrder cancelOrder(RestaurantOrder rOrder) {
		rOrder.setOrderStatus("取消訂位");
		rOrder.getTables().clear();
		return restaurantOrderRepository.save(rOrder);
	}
	
	//報到
	public RestaurantOrder restaurantCheckin(Integer orderId) {
		 Optional<RestaurantOrder> data = restaurantOrderRepository.findById(orderId);
		
		 if(data.isPresent()) {
			  data.get().setOrderStatus("已報到");
			 return restaurantOrderRepository.save(data.get());
		 }
		 return null;
		
	}
	//未到
	public RestaurantOrder restaurantAbsent(RestaurantOrder rOrder) {
		Optional<RestaurantOrder> data = restaurantOrderRepository.findById(rOrder.getOrderId());
		
		if(data.isPresent()) {
			data.get().setOrderStatus("未到");
			 RestaurantOrder save = restaurantOrderRepository.save(data.get());
			 // 查詢這支電話未到次數
		        int absentNum = countPhoneOrderStatus(rOrder.getPhone());

		        if (absentNum >= 3) {
		            // 確認這支電話不在黑名單裡
		            boolean alreadyBlacklisted = rBlacklistRepository.existsByPhone(rOrder.getPhone());

		            if (!alreadyBlacklisted) {
		                // 沒有在黑名單，才新增
		                RestaurantBlacklist blacklist = new RestaurantBlacklist();
		                blacklist.setPhone(rOrder.getPhone());
		                blacklist.setReason("未到3次");
		                blacklist.setBannedTime(LocalDateTime.now());
		                blacklist.setBlackliststatus(1); // 1=封鎖中

		                rBlacklistRepository.save(blacklist);
		            }
		        }
				
				return save;
			
		}
		return null;
		
	}
	//修改訂位
	public RestaurantOrder updateOrder(RestaurantOrderUpdateDto dto) {
	    Optional<RestaurantOrder> optional = restaurantOrderRepository.findById(dto.getOrderId());

	    if (optional.isPresent()) {
	        RestaurantOrder existingOrder = optional.get();
	        existingOrder.setCustomerName(dto.getCustomerName());
	        existingOrder.setPhone(dto.getPhone());
	        existingOrder.setMail(dto.getMail());
	        existingOrder.setOrderdate(dto.getOrderdate());
	        existingOrder.setAdult(dto.getAdult());
	        existingOrder.setChild(dto.getChild());
	        existingOrder.setNote(dto.getNote());

	        // 查詢餐廳時段
	        RestaurantTimeslot slot = rTimeslotRespository.findById(dto.getSlotId()).orElse(null);
	        existingOrder.setRestaurantTimeslot(slot);

	        // 查詢座位列表
	        List<RestaurantTable> tables = rTableRepository.findAllById(dto.getTableIds());
	        existingOrder.setTables(tables);

	        return restaurantOrderRepository.save(existingOrder);
	    } else {
	        throw new RuntimeException("找不到這筆訂單 id: " + dto.getOrderId());
	    }
	}
	//查詢全部黑名單
	public List<RestaurantBlacklist> findAllBlacklists(){
		return rBlacklistRepository.findAll();
	}
	//查詢訂位狀態是取消訂位
	public List<RestaurantOrder> findorderstatuscacancel(){
		return restaurantOrderRepository.findByOrderStatus("取消訂位");
	}
	//查詢全部訂位
	public List<RestaurantOrder> findAllRestaurantOrders(){
		return restaurantOrderRepository.findAll();
	}
	//依電話還有mail查詢
	public List<RestaurantOrder> findByPhoneAndMail(String phone, String mail){
		return restaurantOrderRepository.findByPhoneAndMail(phone, mail);
	}
	//依綠界編號進行更新訂單狀態
		public void updatePayedOrderStatus(String ResMerchantTradeNo) {
			Optional<RestaurantOrder> op = restaurantOrderRepository.findByResMerchantTradeNo(ResMerchantTradeNo);	
			
			if(op.isPresent()) {
				op.get().setOrderStatus("已付訂金");
				restaurantOrderRepository.save(op.get());
			}
			
		}
		//依綠界編號進行查詢
		public Optional<RestaurantOrder> findByResMerchantTradeNo(String ResMerchantTradeNo){
			
			return restaurantOrderRepository.findByResMerchantTradeNo(ResMerchantTradeNo);
			
			
		}
	
}
