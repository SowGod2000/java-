package com.firsthotel.room.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "roomtype")
public class Roomtype {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer roomtypeId;
	
	@NonNull
	@Column(nullable = false)
	private String roomtypeName;
	
	@NonNull
	@Column(nullable = false)
	private Integer pricePerNight;
	
	@Lob
	private byte[] roomImage;
	
	private String description;
	
	@NonNull
	@Column(nullable = false)
	private Integer roomSize;
	
	@NonNull
	@Column(nullable = false)
	private Integer totalRooms;
	
	// 0為可使用，1為假刪除狀態
	@NonNull
	@Column(nullable = false)
	private Integer isDeleted;

	
}
