package com.firsthotel.restaurant.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.firsthotel.restaurant.model.RestaurantBlacklistRepository;
import com.firsthotel.restaurant.model.RestaurantBlacklist;

@Service
public class RestaurantBlacklistService {
	
	@Autowired 
	RestaurantBlacklistRepository rBlacklistRepository;
	
	//依電話查詢是否是黑名單
		public Optional<RestaurantBlacklist> findBlacklists(String phone){
			Integer blackliststatus=1;
			return rBlacklistRepository.findByPhoneAndBlackliststatus(phone,blackliststatus);
		}
		//解Ban
		public boolean unban(Integer blacklistId) {
			Optional<RestaurantBlacklist> op = rBlacklistRepository.findById(blacklistId);
			if(op.isPresent()) {
				op.get().setBlackliststatus(0);
				  rBlacklistRepository.save(op.get());
				return true;
			}
			return false;
		
		}
}
