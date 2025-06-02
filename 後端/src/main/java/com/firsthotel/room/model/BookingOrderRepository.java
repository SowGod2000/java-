package com.firsthotel.room.model;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingOrderRepository extends JpaRepository<BookingOrder, Integer> {
	
	@Query("from BookingOrder order by bookingOrderId Desc")
	public List<BookingOrder> selectAll();
	
	
	@Query("SELECT SUM(b.quantity) FROM BookingOrder b " +
		       "WHERE :targetDate >= b.checkInDate AND :targetDate < b.checkOutDate " +
		       "AND b.roomtype.roomtypeId = :roomtypeId AND b.status != '已取消'")
	public Integer countBookedRoomsByDate(@Param("roomtypeId") Integer roomtypeId, @Param("targetDate") Date targetDate);

	
	@Query("from BookingOrder where memberId = ?1 order by bookingOrderId Desc")
	public List<BookingOrder> findByMemberId(Integer memberId);
	
}
