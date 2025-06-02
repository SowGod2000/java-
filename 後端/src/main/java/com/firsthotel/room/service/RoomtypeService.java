package com.firsthotel.room.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.firsthotel.room.model.Roomtype;
import com.firsthotel.room.model.RoomtypeRepository;

@Service
public class RoomtypeService {
	
	@Autowired
	private RoomtypeRepository roomtypeRepository;
	
	//新增
	public Roomtype insertRoomtype(Roomtype roomtype) {
		return roomtypeRepository.save(roomtype);
	}
	
	//查詢單筆(修改用)
	public Roomtype findById(Integer roomtypeId) {
		return roomtypeRepository.selectById(roomtypeId);
	}
	
	//查詢全部
	public List<Roomtype> findAllRoomtype(){
		return roomtypeRepository.selectAll();
	}
	
	//依價格區間查詢
	public List<Roomtype> selectBySetPrice(Integer low, Integer high){
		return roomtypeRepository.selectBySetPrice(low, high);
	}
	
	//關鍵字查詢
	public List<Roomtype> selectByPartialRoomtype(String keyword){
		String word = "%" + keyword + "%";
		return roomtypeRepository.selectByPartialRoomtype(word);
	}
	
	//關鍵字+價格區間查詢
	public List<Roomtype> selectByKeywordAndPriceRange(Integer low, Integer high, String keyword){
		String word = "%" + keyword + "%";
		return roomtypeRepository.selectByKeywordAndPriceRange(low, high, word);
	}
	
	/*
	 * ------------------- 底下為分頁查詢 -------------------
	 */
	
	//查詢全部
	public Page<Roomtype> findAllRoomtypeByPage(Pageable pageable){
		return roomtypeRepository.selectAllByPage(pageable);
	}
	
	
	
	
	
}
