package com.firsthotel.restaurant.model;

import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Table(name = "restauranttimeslot")
@Entity
public class RestaurantTimeslot {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer slotId;
	
	private LocalTime startTime;
	
	private LocalTime endTime;
	
	private Integer	maxPeople;
	
	private Boolean isEnabled;
	
}
