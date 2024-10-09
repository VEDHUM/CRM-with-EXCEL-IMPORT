package com.example.application.backend.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.application.backend.entity.AuditTrail;
import com.example.application.backend.entity.Contact;
import com.example.application.backend.repository.AuditTrailRepository;


@Service
public class AuditTrailService {

	@Autowired
	private AuditTrailRepository auditrepo;
	
	public void logChange(String entityName, Long entityId, String action,String Stats, String oldValue, String newValue) {
		
		AuditTrail audittrail = new AuditTrail();
		audittrail.setEntityName(entityName);
		audittrail.setEntityId(entityId);
		audittrail.setAction(action);
		audittrail.setStatuss(Stats);
		
		audittrail.setOldValue(oldValue);
		audittrail.setNewValue(newValue);
		audittrail.setChangedBy(chechUserRole());
		audittrail.setChangeDate(LocalDateTime.now());
		
		auditrepo.save(audittrail);
	}
	
    public AuditTrail save(AuditTrail contact) {
        return auditrepo.save(contact);
    }
    
    
	 public String chechUserRole() {
			
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 
		 if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            
	            // Retrieve the username
	            String username = userDetails.getUsername();
	           
	            return username;
		 }
		 return "No authenticated user found.";
	 }

	    public void delete(AuditTrail contact) {
	    	auditrepo.delete(contact);
	    }
}
