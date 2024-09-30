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

   @PostConstruct
    public void populateTestData() {
//        if (companyRepository.count() == 0) {
//            companyRepository.saveAll(
//                    Stream.of("Path-Way Electronics", "E-Tech Management", "Path-E-Tech Management")
//                            .map(Company::new)
//                            .collect(Collectors.toList()));
//        }

        if (contactRepository.count() == 0) {
            Random r = new Random(0);
        
            contactRepository.saveAll(
                    Stream.of("Gabrielle Patel", "Brian Robinson", "Eduardo Haugen",
                            "Koen Johansen", "Alejandro Macdonald", "Angel Karlsson", "Yahir Gustavsson", "Haiden Svensson",
                            "Emily Stewart", "Corinne Davis", "Ryann Davis", "Yurem Jackson", "Kelly Gustavsson",
                            "Eileen Walker", "Katelyn Martin", "Israel Carlsson", "Quinn Hansson", "Makena Smith",
                            "Danielle Watson", "Leland Harris", "Gunner Karlsen", "Jamar Olsson", "Lara Martin",
                            "Ann Andersson", "Remington Andersson", "Rene Carlsson", "Elvis Olsen", "Solomon Olsen",
                            "Jaydan Jackson", "Bernard Nilsen", "Babu Rajendran", "Shu Yang")
                            .map(name -> {
                                String[] split = name.split(" ");
                                Contact contact = new Contact();
                                contact.setFirstName(split[0]);
                                contact.setLastName(split[1]);
                                contact.setStatus(Contact.Status.values()[r.nextInt(Contact.Status.values().length)]);
                         
                                String email = (contact.getFirstName() + "." + contact.getLastName() + "@" + contact.getFirstName().replaceAll("[\\s-]", "") + ".com").toLowerCase();
                                contact.setEmail(email);
                                return contact;
                            }).collect(Collectors.toList()));
        }
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