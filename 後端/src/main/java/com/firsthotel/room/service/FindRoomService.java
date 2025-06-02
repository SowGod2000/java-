package com.firsthotel.room.service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.datatype.jsr310.ser.MonthDaySerializer;
import com.firsthotel.room.dto.RoomtypeDto;
import com.firsthotel.room.model.BookingOrderRepository;
import com.firsthotel.room.model.Roomtype;
import com.firsthotel.room.model.RoomtypeRepository;

@Service
public class FindRoomService {
	
	@Autowired
	private RoomtypeRepository roomtypeRepository;
	
	@Autowired
	private BookingOrderRepository bookingOrderRepository;
	
	//取得所有roomtype，並且轉成dto形式
	public List<RoomtypeDto> findAllRooms(){
		List<Roomtype> roomtypeList = roomtypeRepository.selectAll();
		List<RoomtypeDto> dtoList = new ArrayList<>();
		for(Roomtype room :roomtypeList) {
			RoomtypeDto roomDto = new RoomtypeDto();
			roomDto.setRoomtypeId(room.getRoomtypeId());
			roomDto.setRoomtypeName(room.getRoomtypeName());
			roomDto.setPricePerNight(room.getPricePerNight());
			if (room.getRoomImage() != null) {
				roomDto.setRoomImage(Base64.getEncoder().encodeToString(room.getRoomImage()));
			}
			roomDto.setDescription(room.getDescription());
			roomDto.setRoomSize(room.getRoomSize());
			roomDto.setTotalRooms(room.getTotalRooms());
			roomDto.setAvailableRooms(room.getTotalRooms());
			dtoList.add(roomDto);
		}
		return dtoList;
	}
	
	
	//找出指定日期指定房型已被下訂數量
	public Integer countBookedRoomsByDate(Integer roomtypeId, Date startDate, Date endDate) {
	    // 計算總共天數
	    long diffInMillies = endDate.getTime() - startDate.getTime();
	    int days = (int) (diffInMillies / (1000 * 60 * 60 * 24));

	    Integer maxBooked = 0;

	    // 使用 Calendar 遍歷每一天
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(startDate);

	    for (int i = 0; i < days; i++) {
	        Date targetDate = calendar.getTime();
	        Integer booked = bookingOrderRepository.countBookedRoomsByDate(roomtypeId, targetDate);
	        if (booked != null && booked > maxBooked) {
	            maxBooked = booked;
	        }
	        calendar.add(Calendar.DATE, 1); // 日期加一天
	    }
	    return maxBooked;
	}
	
	
	
	
	
}
