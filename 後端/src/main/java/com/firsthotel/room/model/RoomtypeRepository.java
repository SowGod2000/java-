package com.firsthotel.room.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RoomtypeRepository extends JpaRepository<Roomtype, Integer> {
	
	//依價格區間查詢
	@Query("from Roomtype where pricePerNight >= ?1 and pricePerNight <= ?2 and isDeleted = 0")
	public List<Roomtype> selectBySetPrice(Integer low, Integer high);
	
	//關鍵字查詢
	@Query("from Roomtype where roomtypeName like ?1 and isDeleted = 0")
	public List<Roomtype> selectByPartialRoomtype(String keyword);
	
	//關鍵字+價格區間查詢
	@Query("from Roomtype where pricePerNight >= ?1 and pricePerNight <= ?2 and roomtypeName like ?3 and isDeleted = 0")
	public List<Roomtype> selectByKeywordAndPriceRange(Integer low, Integer high, String keyword);
	
	//查詢單筆
	@Query("from Roomtype where roomtypeId = ?1 and isDeleted = 0")
	public Roomtype selectById(Integer id);
	
	//查詢全部
	@Query("from Roomtype where isDeleted = 0")
	public List<Roomtype> selectAll();
	
	/*
	 * ------------------- 底下為分頁查詢 -------------------
	 */
	
	//查詢全部
	@Query("from Roomtype where isDeleted = 0")
	public Page<Roomtype> selectAllByPage(Pageable pageable);
	
	
}
