package com.firsthotel.restaurant.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firsthotel.restaurant.model.RestaurantTimeslot;
import com.firsthotel.restaurant.model.RestaurantTimeslotRespository;

@Service
public class RestaurantTimslotService {
	
	@Autowired
	private RestaurantTimeslotRespository rTimeslotRespository;
	
	public Optional<RestaurantTimeslot> findAllTimeslotById(Integer slotid) {
		return rTimeslotRespository.findById(slotid);
	}
	
}
