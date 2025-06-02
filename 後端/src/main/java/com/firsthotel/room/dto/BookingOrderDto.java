package com.firsthotel.room.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingOrderDto {
	
	private Integer bookingOrderId;
	private Integer memberId;
	private Date checkInDate;
	private Date checkOutDate;
	private Integer roomtypeId;
	private Integer pricePerRoom;
	private Integer quantity;
	private Integer totalPrice;
	private String status;
	private String paymentStatus;
	private String paymentMethod;
	private String note;
	
}
