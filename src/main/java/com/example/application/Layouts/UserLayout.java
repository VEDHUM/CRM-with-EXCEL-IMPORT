package com.example.application.Layouts;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.application.backend.entity.Users;
import com.example.application.backend.service.UserService;
import com.example.application.security.SecurityService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;

public class UserLayout extends AppLayout {
	
	private final Text timeLabel;
	private  PasswordEncoder passwordEncoder;
	private UserService userservice;
	private SecurityService securityservice;
	
	public UserLayout(SecurityService securityservice, PasswordEncoder passwordEncoder,UserService userservice ) {

		this.userservice = userservice;
		this.securityservice = securityservice;
		this.passwordEncoder = passwordEncoder;
		timeLabel = new Text(getCurrentTime());
		createHeader();
		chechUserRole();
	}
	

	private void createHeader( ) {
		
		
		H1 logo = new H1("CRM");
		logo.addClassName("logo");
		Button logout = new Button("LOG OUT", e -> securityservice.logout());
		logout.addClassName("log-out");
		String FirstLetter = new String();
		FirstLetter = chechUserRole();
		
		H2 logo1 = new H2(""+chechUserRole());
		
		LocalDate currentDate = LocalDate.now();
	    String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    Div dateLabel = new Div( formattedDate);
	    
        Avatar avt1 = new Avatar(FirstLetter);
        avt1.addClassName("avatar-class");
		
		MenuBar menuBar = new MenuBar();
		menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
		
		MenuItem menuItem = menuBar.addItem(avt1);
		
		SubMenu subMenu = menuItem.getSubMenu();

		subMenu.addItem("Sign out", e -> securityservice.logout() );
		subMenu.addItem("Change Password", e-> changePassword());
	    
	    HorizontalLayout header = new HorizontalLayout(logo,menuBar);
	    
	    header.addAndExpand(new HorizontalLayout());
	    header.add(timeLabel,dateLabel,menuBar);
	    header.setWidthFull();
	    
	    UI.getCurrent().setPollInterval(1000);
	    UI.getCurrent().addPollListener(pollEvent -> updateTime());
	    
	    header.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
	    header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
	    
		header.addClassName("header");
		
		addToNavbar(header);
		
	}
	
	 
	 private void changePassword() {
		 
		 SecurityService securityService = new SecurityService() ;
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		

		
		 UserDetails userDetails = (UserDetails) auth.getPrincipal();
		 String username = securityService.getAuthenticatedUsername();
		 
		 Dialog dialog = new Dialog();
		 TextField currentPassword = new TextField("Currrent Password");
		 TextField newPassword = new TextField("New Password");
		 TextField confirmPassword = new TextField("Confirm Password");
		 
		 Button save = new Button("Save" , e->{
			    String currentPasswordValue = currentPassword.getValue();
	            String newPasswordValue = newPassword.getValue();
	            String confirmNewPasswordValue = confirmPassword.getValue();
	            
	            if(!newPasswordValue.equals(confirmNewPasswordValue)) {
	            	 Notification.show("New passwords do not match!", 3000, Notification.Position.MIDDLE);
	                 return;
	            }
	            
	           
			 
	            Users user = userservice.findByUsername(username);
	            
	            if(!passwordEncoder.matches(currentPasswordValue, user.getPassword())) {
	            	 Notification.show("Current password is incorrect!", 3000, Notification.Position.MIDDLE);
	                 return;
	            }
	            
	            user.setPassword(passwordEncoder.encode(newPasswordValue));
	            userservice.save(user);
	            
	            Notification.show("Password updated successfully!", 3000, Notification.Position.MIDDLE);

			 dialog.close();
		 });
		 Button cancel = new Button("Cancel", e-> dialog.close());
		 
		 FormLayout tool = new FormLayout(currentPassword,newPassword,confirmPassword);
		 HorizontalLayout bar = new HorizontalLayout(save,cancel);
		 dialog.add(tool,bar );
		 dialog.setHeaderTitle("Change Password of: "+ username);
		 dialog.open();
		
	}


	public String chechUserRole() {
	
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 
		 if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            
	            // Retrieve the username
	            String username = userDetails.getUsername();
	            return username.substring(0, 1).toUpperCase();
		 }
		 return "No authenticated user found.";
	 }
 


    private void updateTime() {
        // Update the text with the current time
        timeLabel.setText("Time: " + getCurrentTime());
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
	

}
