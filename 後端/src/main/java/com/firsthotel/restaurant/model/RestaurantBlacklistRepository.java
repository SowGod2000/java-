package com.firsthotel.restaurant.model;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RestaurantBlacklistRepository extends JpaRepository<RestaurantBlacklist, Integer> {
	Optional<RestaurantBlacklist> findByPhoneAndBlackliststatus(String phone,Integer blackliststatus);

	boolean existsByPhone(String phone);
}
