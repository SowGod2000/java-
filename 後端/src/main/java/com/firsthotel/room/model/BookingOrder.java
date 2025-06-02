package com.firsthotel.room.model;

import java.time.LocalDateTime;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NonNull
@Entity
@Table(name = "bookingOrder")
public class BookingOrder {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer bookingOrderId;
	
	@NonNull
	@Column(nullable = false)
	private Integer memberId;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd EEEE")
	@Temporal(TemporalType.DATE)
	@NonNull
	@Column(nullable = false)
	private Date checkInDate;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd EEEE")
	@Temporal(TemporalType.DATE)
	@NonNull
	@Column(nullable = false)
	private Date checkOutDate;
	
	@NonNull
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "fk_roomtype_id", nullable = false)
	private Roomtype roomtype;
	
	@NonNull
	@Column(nullable = false)
	private Integer pricePerRoom;
	
	@NonNull
	@Column(nullable = false)
	private Integer quantity;
	
	@NonNull
	@Column(nullable = false)
	private Integer totalPrice;
	
	@NonNull
	@Column(nullable = false)
	private String status;
	
	@NonNull
	@Column(nullable = false)
	private String paymentStatus;
	
	private String paymentMethod;
	
	//櫃台入住時間
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime checkInTime;
	
	//櫃台退房時間
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime checkOutTime;
	
	//訂單備註
	private String note;
	
	
}
