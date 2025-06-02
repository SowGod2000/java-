package com.firsthotel.restaurant.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "restaurantblacklist")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantBlacklist {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Integer blacklistId;
	
	@Column(unique =true,nullable = false)
	private String phone;
	@Column(nullable  = false)
	private LocalDateTime bannedTime =LocalDateTime.now();
	@Column(nullable = false)
	private String reason;
	@Column(nullable = false)
	private Integer blackliststatus=1;
}
