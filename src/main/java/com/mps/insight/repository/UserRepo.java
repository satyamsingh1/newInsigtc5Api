package com.mps.insight.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mps.insight.entity.C5User;

public interface UserRepo  extends JpaRepository<C5User, Integer> {
	 C5User findByUserName(String username);

}
