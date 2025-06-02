package com.firsthotel.restaurant.model;

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
@Table(name = "restaurantable")
@Getter
@Setter
@NoArgsConstructor
public class RestaurantTable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer tableId;
	@Column(unique = true, nullable = false)
	private String tableCode;

	private Integer capacity = 4;

}
