package com.example.application.ui;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.application.backend.entity.Users;
import com.example.application.backend.service.UserService;
import com.example.application.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;


@Route(value = "HomeView")
@RolesAllowed("ADMIN")

public class HomeView extends VerticalLayout {
	
	private SecurityService securityservice;
    private UserService userservice;
    private PasswordEncoder passwordEncoder;
    
	public HomeView( SecurityService securityservice, UserService userservice, PasswordEncoder passwordEncoder) {
		addClassName("home-view");
		this.passwordEncoder =   passwordEncoder;
		this.userservice = userservice;
		this.securityservice = securityservice;
		Div text = new Div("CRM");
		text.addClassName("text-span");
		
		Span action = new Span("HOME NAVIATION");
		action.addClassName("action-span");
		
		Button logout = new Button("LOGOUT", new Icon(VaadinIcon.ARROW_LEFT));
		logout.addClassNames("home-button");
		logout.addClickListener( e->{
			 securityservice.logout();
		});
		
		Button activity = new Button("ACTIVITY");
		activity.addClassName("home-button");
		activity.addClickListener( e->{
			UI.getCurrent().navigate("audit-trail");
		});
		
		
		Button Contact = new Button("CONTACTS", e->{
			UI.getCurrent().navigate(ListView.class);
		});
		Contact.addClassNames("home-button");
		
		
		Button User = new Button("USERS", e->{
			UI.getCurrent().navigate(UserListView.class);
		});
		User.addClassName("home-button");
		
		Button changePassword = new Button("CHANGE PASSWORD", E -> changePassword());
		changePassword.addClassName("home-button");
		
		
		HorizontalLayout layout = new HorizontalLayout(Contact,User);
		layout.setJustifyContentMode(JustifyContentMode.CENTER);
		
		HorizontalLayout layout1 = new HorizontalLayout(changePassword,activity);
		layout1.setJustifyContentMode(JustifyContentMode.CENTER);
		
		VerticalLayout layout3 = new VerticalLayout();
		layout3.setJustifyContentMode(JustifyContentMode.CENTER);
		
		setAlignItems(Alignment.CENTER);
		setJustifyContentMode(JustifyContentMode.CENTER);
		
		add(text,action,layout,layout1,logout);
		setSizeFull();
	
  }





	private void changePassword() {
		   SecurityService securityService = new SecurityService() ;
		 	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
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
}
