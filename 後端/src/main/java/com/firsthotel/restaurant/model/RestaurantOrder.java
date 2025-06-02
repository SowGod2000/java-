package com.firsthotel.restaurant.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "restaurantorders")
public class RestaurantOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer orderId;
	@Column(nullable = false)
	private String customerName;
	@Column(nullable = false)
	private String phone;
	@Column(nullable = false)
	private String mail;
	@Column(nullable = false)
	private LocalDate orderdate;
	private Integer adult;
	private Integer child;
	private Integer expectedPrice;
	private Integer deposit;
	private String orderStatus= "待確認";
	private String note;
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm EEEE")
	private LocalDateTime createdTime;
	
	//綠界的付款編號
	@Column(unique = true,nullable = true)
	private String resMerchantTradeNo;
	
	@ManyToOne
	@JoinColumn(name = "slotId")
	private RestaurantTimeslot restaurantTimeslot;
	
	@ManyToMany
	@JoinTable(
	    name = "order_tables",
	    joinColumns = @JoinColumn(name = "orderId"),
	    inverseJoinColumns = @JoinColumn(name = "tableId")
	)
	private List<RestaurantTable> tables = new ArrayList<>();
	
}
