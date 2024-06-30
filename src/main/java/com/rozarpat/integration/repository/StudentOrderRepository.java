package com.rozarpat.integration.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rozarpat.integration.entity.StudentOrder;

public interface StudentOrderRepository extends JpaRepository<StudentOrder, Integer> {
	
	 StudentOrder findByRozarpayOrderId(String rozarpayOrderId);

}
