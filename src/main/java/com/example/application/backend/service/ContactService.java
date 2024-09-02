package com.example.application.backend.service;

import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.application.backend.entity.Contact;
import com.example.application.backend.repository.ContactRepository;


import jakarta.annotation.PostConstruct;

@Service
public class ContactService {
    private static final Logger LOGGER = Logger.getLogger(ContactService.class.getName());
    private ContactRepository contactRepository;
  

    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
     
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
}