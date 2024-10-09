package com.example.application.security;

import java.io.IOException;
import java.util.Collection;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.example.application.backend.service.AuditTrailService;
import com.example.application.login.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity // <1>
@Configuration
public class SecurityConfig extends VaadinWebSecurity {  //
	
	private  AuditTrailService auditService;// <2>  c
    
	@Autowired
	private DataSource dataSource;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	

	@Override
	protected void configure(HttpSecurity http) throws Exception{
		   
		    
		    setLoginView(http,LoginView.class);
        http
                .formLogin(login -> login
                        .successHandler(new CustomLoginSuccessHandler()));
		        
		    super.configure(http);
	}
	

	
    public class CustomLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    	
        @Override
		public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            // Get the roles (authorities) of the authenticated user
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            // Determine the target URL based on roles
            String targetUrl = determineTargetUrl(authorities);
   
            // Redirect to the target URL (e.g., "/admin" or "/user")
           response.sendRedirect(targetUrl);
        }

        // Determine target URL based on the user's role
        protected String determineTargetUrl(Collection<? extends GrantedAuthority> authorities) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals("ROLE_ADMIN")) {
                    return "/HomeView";  // Admin route
                } else if (authority.getAuthority().equals("ROLE_USER")) {
                    return "/user";  // User route
                }
            }
            return "/";  // Default route
        }
    }
	
	    
	@Override
	public void configure(WebSecurity web) throws Exception{
		web.ignoring().requestMatchers("/images/**");
		super.configure(web);
	}

    
    @Bean
    public UserDetailsService userDetailsService() {
    	
    	JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager(dataSource);
    	jdbcUserDetailsManager.setUsersByUsernameQuery("SELECT username, password, enabled FROM users WHERE username = ?");
    	jdbcUserDetailsManager.setAuthoritiesByUsernameQuery("SELECT username, role FROM users WHERE username = ?");
    	return jdbcUserDetailsManager;
    }
    
}


