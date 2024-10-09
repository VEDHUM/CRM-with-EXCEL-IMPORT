package com.example.application.backend.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.application.backend.entity.Contact;
import com.example.application.backend.repository.ContactRepository;
import com.example.application.helper.Helper;
import com.example.application.helper.Helper1;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;



@Service
public class ContactService {
    private static final Logger LOGGER = Logger.getLogger(ContactService.class.getName());
    private ContactRepository contactRepository;
    
    @Autowired
    private AuditTrailService auditservice;
  

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
     
    }
    
    public Contact updateContact(Contact updatedcontact) {
    	
    	Contact oldContact = contactRepository.findById(updatedcontact.getId()).orElseThrow();
    	
    	String oldValue = convertToJson(oldContact);
    	String newValue = convertToJson(updatedcontact);
    	
    	Contact savedContact = contactRepository.save(updatedcontact);
    	
    	return savedContact;
    }
    
    private String convertToJson(Object entity) {
        // Use a library like Jackson to convert object to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(entity);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
    
    public void save(List<Contact> contact) {
    	this.contactRepository.saveAll(contact);
    }
    
    public List<Contact>  getAllContacts(){
    	return this.contactRepository.findAll();
    }

    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    public List<Contact> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return contactRepository.findAll();
        } else {
            return contactRepository.search(stringFilter);
        }
    }

    public long count() {
        return contactRepository.count();
    }

    public void delete(Contact contact) {
        contactRepository.delete(contact);
    }
    
 
    public Contact save(Contact contact) {
        return contactRepository.save(contact);
    }


	public long countByName(String name) {
		 return contactRepository.countByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
	}

	public List<Contact> findByName(String name, int offset, int limit) {
		 Pageable pageable = PageRequest.of(offset / limit, limit);
	        return contactRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable);
	}


	
	public ByteArrayInputStream getActualData() throws IOException {
		List<Contact> all = contactRepository.findAll();
		ByteArrayInputStream byteArrayInputStream = Helper.dataToExcel(all);
		return byteArrayInputStream;	
	}
	
	public ByteArrayInputStream getTemplete() throws IOException {
		List<Contact> all = contactRepository.findAll();
		ByteArrayInputStream byteArrayInputStream = Helper1.dataToExcel(all);
		return byteArrayInputStream;	
	}	
	
	
	   public boolean checkIfEmailExists(String email) {
	    	
	    	 return contactRepository.existsByEmail(email);
	    }
	    
	    public boolean checkIfEmailExistsExcludingCurrent(String email, Long currentContactId) {
	        return contactRepository.existsByEmailAndIdNot(email, currentContactId);
	    }
	    
	    public boolean checkIfPhoneExists(String phone) {
	    	
	    	 return contactRepository.existsByPhone(phone);
	    }
	    
	    public boolean checkIfPhoneExistsExcludingCurrent(String phone, Long id) {
	        return contactRepository.existsByPhoneAndIdNot(phone, id);
	    }
	    
	    public boolean checkIfFirstNameValid(String firstName) {
	    	return firstName.matches(".*\\d.*");
	    }
	    public boolean checkIfLastNameValid(String lastName) {
	    	return lastName.matches(".*\\d.*");
	    }


	
	
	
	
	
	
	
}