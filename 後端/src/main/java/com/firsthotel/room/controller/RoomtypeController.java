package com.firsthotel.room.controller;

import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RestController;

import com.firsthotel.room.dto.RoomtypeDto;
import com.firsthotel.room.model.Roomtype;
import com.firsthotel.room.service.RoomtypeService;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;




@RestController
@RequestMapping("/roomtype")
@CrossOrigin(origins = "http://localhost:5173")
public class RoomtypeController {
	
	@Autowired
	private RoomtypeService roomtypeService;
	
	@GetMapping("/findAll")
	public List<Roomtype> findAll() {
		return roomtypeService.findAllRoomtype();
	}
	
	//查詢單筆(修改用)
	@GetMapping("/{roomtypeId}")
	public RoomtypeDto getRoomtypeById(@PathVariable Integer roomtypeId) {
		Roomtype room = roomtypeService.findById(roomtypeId);

        RoomtypeDto dto = new RoomtypeDto();
        dto.setRoomtypeName(room.getRoomtypeName());
        dto.setPricePerNight(room.getPricePerNight());
        dto.setDescription(room.getDescription());
        dto.setRoomSize(room.getRoomSize());
        dto.setTotalRooms(room.getTotalRooms());
        dto.setIsDeleted(room.getIsDeleted());
        if (room.getRoomImage() != null) {
            dto.setRoomImage(Base64.getEncoder().encodeToString(room.getRoomImage()));
        }
        return dto;
	}
	
	//修改表單送出時使用
	@PutMapping("/update/{roomtypeId}")
	@Transactional
	public Roomtype updateRoomtype(
			@PathVariable Integer roomtypeId, 
			@RequestBody RoomtypeDto dto) {
		Roomtype room = roomtypeService.findById(roomtypeId);
		room.setRoomtypeName(dto.getRoomtypeName());
		room.setDescription(dto.getDescription());
		room.setPricePerNight(dto.getPricePerNight());
		room.setRoomSize(dto.getRoomSize());
		room.setTotalRooms(dto.getTotalRooms());
		System.out.println("-----------------------------");
		System.out.println(dto.getRoomImage());
		if (dto.getRoomImage() != null) {
            room.setRoomImage(Base64.getDecoder().decode(dto.getRoomImage()));
        }
		return room;
	}
	
	//刪除(軟刪除)
	@PutMapping("/delete/{roomtypeId}")
	@Transactional
	public Roomtype deleteRoomtype(@PathVariable Integer roomtypeId) {
		Roomtype room = roomtypeService.findById(roomtypeId);
		room.setIsDeleted(1);
		return room;
	}
	
	
	//新增
	@PostMapping("/insert")
	public ResponseEntity<String> insertRoomtype(@RequestBody RoomtypeDto dto) {
		try {
			System.out.println("收到的資料如下：");
	        System.out.println("名稱: " + dto.getRoomtypeName());
	        System.out.println("價格: " + dto.getPricePerNight());
	        System.out.println("圖片: " + (dto.getRoomImage() != null ? dto.getRoomImage().substring(0, 30) + "..." : "null"));
	        System.out.println("描述: " + dto.getDescription());
	        System.out.println("房型大小: " + dto.getRoomSize());
	        System.out.println("總房間數: " + dto.getTotalRooms());
			Roomtype roomtype = new Roomtype();
            roomtype.setRoomtypeName(dto.getRoomtypeName());
            roomtype.setPricePerNight(dto.getPricePerNight());
            roomtype.setDescription(dto.getDescription());
            roomtype.setRoomSize(dto.getRoomSize());
            roomtype.setTotalRooms(dto.getTotalRooms());
            roomtype.setIsDeleted(0);

            // 圖片 base64 轉 byte[]
            if (dto.getRoomImage() != null && !dto.getRoomImage().isEmpty()) {
                byte[] imageBytes = Base64.getDecoder().decode(dto.getRoomImage().split(",")[1]); // 去掉 data:image/jpeg;base64, 頭
                roomtype.setRoomImage(imageBytes);
            }

            roomtypeService.insertRoomtype(roomtype);
            return ResponseEntity.ok("房型新增成功");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("房型新增失敗：" + e.getMessage());
		}
	}
	
	
	
	
	
	
	
	
	
}
