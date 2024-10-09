package com.example.application.backend.service;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.application.backend.entity.Users;
import com.example.application.backend.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	private UserRepository userrepo;
	
//	public List<UserEntity> findAll(){
//		return userrepo.findAll();
//	}
	
    public UserService(UserRepository userrepo) {
        this.userrepo = userrepo;
     
    }

	public Collection<Users> findAll() {
		return userrepo.findAll();
	}

    public void save(Users user) {
    	
    	this.userrepo.save(user);
    }

	public void delete(Users user) {
		this.userrepo.delete(user);
		
	}
	
	public void registerUser(String username,  String rawPassword) {
		
		Users user = new Users();
		user.setUsername(username);
		user.setRole("ROLE_USER");
		user.setEnabled(true);
		String encodedpassword = passwordEncoder.encode(rawPassword);
		user.setPassword(encodedpassword);
		userrepo.save(user);
		
	}
	
    public List<Users> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return userrepo.findAll();
        } else {
            return userrepo.search(stringFilter);
        }
    }
    
    public Users findByUsername(String username) {
        return userrepo.findByUsername(username);
    }
    
    public boolean existsByUsername(String username) {
    	return userrepo.existsByUsername(username);
    }

}
