package com.sanjay.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sanjay.entity.User;

public interface UserRepository extends JpaRepository<User, Integer>{
	 User findByUsername(String username);
}
