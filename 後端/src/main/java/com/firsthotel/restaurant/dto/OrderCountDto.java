package com.firsthotel.restaurant.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
public class OrderCountDto {
	private LocalDate orderdate;
    private Long count;
    
    public OrderCountDto(LocalDate orderdate, Long count) {
        this.orderdate = orderdate;
        this.count = count;
    }
}
