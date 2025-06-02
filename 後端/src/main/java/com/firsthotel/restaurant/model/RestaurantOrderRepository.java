package com.firsthotel.restaurant.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.firsthotel.restaurant.dto.OrderCountDto;

public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Integer>{
	
//查詢該天時段有哪些座位已經被使用
	@Query("SELECT t.id FROM RestaurantOrder o JOIN o.tables t " +
		       "WHERE o.orderdate = :orderdate AND o.restaurantTimeslot.id = :slotId AND o.orderStatus != '已取消'")
	List<Integer> findUsedSeat(@Param("slotId") Integer slotId,@Param("orderdate") LocalDate orderdate);
	//計算這筆電話訂單狀態
	int countByPhoneAndOrderStatus(String phone, String orderStatus);
	//查詢訂單狀態不是取消訂單
	List<RestaurantOrder> findByOrderStatusNot(String orderStatus);
	//查詢訂單狀態是取消訂單
	List<RestaurantOrder> findByOrderStatus(String orderStatus);
	//依電話和mail查詢
	List<RestaurantOrder> findByPhoneAndMail(String phone, String mail);
	//計算當天有幾筆
	@Query("SELECT new com.firsthotel.restaurant.dto.OrderCountDto(r.orderdate, COUNT(r)) FROM RestaurantOrder r GROUP BY r.orderdate ORDER BY r.orderdate ASC")
	List<OrderCountDto> countOrdersGroupByDate();
	//依綠界編號查詢
		Optional<RestaurantOrder> findByResMerchantTradeNo(String ResMerchantTradeNo);
}
