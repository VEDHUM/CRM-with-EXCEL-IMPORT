package com.example.application.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.application.backend.entity.Contact;
import com.vaadin.flow.component.textfield.TextField;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("select c from Contact c " +
            "where lower(c.firstName) like lower(concat('%', :searchTerm, '%')) " +
            "or lower(c.lastName) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.email) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.phone) like lower(concat('%', :searchTerm, '%'))" +
            "or lower(c.status) like lower(concat('%', :searchTerm, '%'))")//
    List<Contact> search(@Param("searchTerm") String searchTerm);

	List<Contact> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, String name2,
			org.springframework.data.domain.Pageable pageable);

	int countByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, String name2);
	
	
	boolean existsByPhone(TextField phone);
	boolean existsByPhone(String phone);
	
	boolean existsByEmail(TextField email);
	boolean existsByEmail(String email);
	
    boolean existsByEmailAndIdNot(String email, Long id);
    boolean existsByPhoneAndIdNot(String phone, Long id);

}