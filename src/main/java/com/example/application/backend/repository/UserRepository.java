package com.example.application.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.application.backend.entity.Contact;
import com.example.application.backend.entity.Users;

public interface UserRepository extends JpaRepository<Users, Long> {
	
	 @Query("select c from Users c " +
	            "where lower(c.username) like lower(concat('%', :searchTerm, '%')) " +
	            "or lower(c.password) like lower(concat('%', :searchTerm, '%'))" +
	            "or lower(c.role) like lower(concat('%', :searchTerm, '%'))" )//
	    List<Users> search(@Param("searchTerm") String searchTerm);


	Users findByUsername(String username);


	boolean existsByUsername(String username);


}
