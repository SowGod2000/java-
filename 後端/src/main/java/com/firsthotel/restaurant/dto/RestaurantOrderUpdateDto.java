package com.firsthotel.restaurant.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class RestaurantOrderUpdateDto {
	  private Integer orderId;
	    private String customerName;
	    private String phone;
	    private String mail;
	    private LocalDate orderdate;
	    private Integer slotId;
	    private Integer adult;
	    private Integer child;
	    private String note;
	    private List<Integer> tableIds;
}
