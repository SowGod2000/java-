package com.firsthotel.restaurant.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firsthotel.restaurant.model.RestaurantTable;
import com.firsthotel.restaurant.model.RestaurantTableRepository;

@Service
public class RestaurantTableService {
	
	

		@Autowired
		RestaurantTableRepository rTableRepository;
		
		public List<RestaurantTable> findAllTableById(List<Integer> tableId) {
			
			return rTableRepository.findAllById(tableId);
			
		}
}
