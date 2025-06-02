package com.firsthotel.restaurant.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RestaurantOrderDto {
	private String customerName;
    private String phone;
    private String mail;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderdate;
    private Integer adult;
    private Integer child;
    private Integer expectedPrice;
    private Integer deposit;
    private String note;

    private Integer slotId; 
    private List<Integer> tableIds; // 多個座位Id
    
  
//    	String customerName,String phone,String mail,LocalDate orderdate,Integer adult,String note,Integer slotId,List<Integer> tableIds
    
    
}
