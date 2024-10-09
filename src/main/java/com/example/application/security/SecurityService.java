package com.example.application.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.VaadinServletRequest;
import com.vaadin.flow.spring.security.AuthenticationContext;

@Component
public class SecurityService {
	
	public void logout() {
		UI.getCurrent().getPage().setLocation("/");
		SecurityContextLogoutHandler logouthandler = new SecurityContextLogoutHandler();
		logouthandler.logout(VaadinServletRequest.getCurrent().getHttpServletRequest(), null, null);
		}

    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getName();  // Get the username of the authenticated user
        }

        return null; // Return null if no user is authenticated
    }
	
	}


