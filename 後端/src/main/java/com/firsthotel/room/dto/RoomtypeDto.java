package com.firsthotel.room.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomtypeDto {
	
	private Integer roomtypeId;
	private String roomtypeName;
	private Integer pricePerNight;
	private String roomImage;
	private String description;
	private Integer roomSize;
	private Integer totalRooms;
	private Integer isDeleted;
	
	//剩餘房間數
	private Integer availableRooms;
	//被預訂房間數
	private Integer orderedRooms;
}
