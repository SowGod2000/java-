package com.firsthotel.room.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.room.dto.RoomtypeDto;
import com.firsthotel.room.service.BookingOrderService;
import com.firsthotel.room.service.FindRoomService;
import com.firsthotel.room.service.RoomtypeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/findRoom")
@CrossOrigin(origins = "http://localhost:5173")
public class FindRoomController {
	
	@Autowired
	private BookingOrderService bookingOrderService;
	
	@Autowired
	private RoomtypeService roomtypeService;
	
	@Autowired
	private FindRoomService findRoomService;
	
	
	@GetMapping("/{checkInDate}/{checkOutDate}")
	public List<RoomtypeDto> findRoom(
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkInDate,
			@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOutDate) {
		//這裡要寫甚麼邏輯
		List<RoomtypeDto> rooms = findRoomService.findAllRooms();
		for(RoomtypeDto room : rooms) {
			Integer maxbooked = findRoomService.countBookedRoomsByDate(room.getRoomtypeId(), checkInDate, checkOutDate);
			room.setOrderedRooms(maxbooked);
			Integer availableRooms = room.getTotalRooms() - maxbooked;
			room.setAvailableRooms(availableRooms);
			System.out.println("在 " + checkInDate + " 到 " + checkOutDate);
			System.out.println("房型編號: " + room.getRoomtypeId() + " 同一天最多被訂了 " + maxbooked + " 間");
			System.out.println("總共 " + room.getTotalRooms() + " 間，剩餘 " + availableRooms);
		}
		return rooms;
	}
	
//	@GetMapping("/test")
//	public List<RoomtypeDto> test() {
//		List<RoomtypeDto> rooms = findRoomService.findAllRooms();
//		for(RoomtypeDto room : rooms) {
//			
//		}
//		
//		return rooms;
//	}
	
	
	
	
	
	
}
