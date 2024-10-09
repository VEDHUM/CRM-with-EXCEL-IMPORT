//package com.example.application.security;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import com.vaadin.flow.component.page.Page;
//
//
//import java.io.IOException;
//import java.util.Collection;
//
//
//public class CustomLoginSuccessHandler extends  SavedRequestAwareAuthenticationSuccessHandler {
//
//	  @Override
//	    protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//	        // Get the user's roles (authorities)
//	        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
//	        
//	        String targetUrl = determineTargetUrl(authorities);
//	        
//	        if (response.isCommitted()) {
//	            System.out.println("Response has already been committed. Unable to redirect to " + targetUrl);
//	            return;
//	        }
//	        
//	        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//	    }
//
//	    // Determine target URL based on roles
//	    protected String determineTargetUrl(Collection<? extends GrantedAuthority> authorities) {
//	        for (GrantedAuthority authority : authorities) {
//	            String role = authority.getAuthority();
//	            if (role.equals("ROLE_ADMIN")) {
//	            	System.out.println(role);
//	            	return "/contact/Adminn";
//	            } else if (role.equals("ROLE_USER"))
//	            	System.out.println(role);{
//	                return "/user";   // Route for user view
//	            }
//	        }
//	        return "/";  // Default route if no roles are matched
//	    }
//	}
//
