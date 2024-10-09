package com.example.application.login;

import com.example.application.backend.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route(value="signup")
@AnonymousAllowed
public class SignUpView extends VerticalLayout {
	
	private  UserService userservice;
	
	
	public SignUpView (UserService userservice) {
		this.userservice = userservice;
		Button back = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT) , e ->{
			UI.getCurrent().navigate("login");
		});
		TextField username = new TextField("Username");
		PasswordField password = new PasswordField("Password");
		Button signup = new Button("Sign Up");
		
		signup.addClickListener( e-> {
			if(!username.isEmpty() && !password.isEmpty() ) {
				
				String userrr = username.getValue();
				String passwordd = password.getValue();
				if(userservice.existsByUsername(userrr) == true) {
					Notification.show("UserName Already Exists",3000, Notification.Position.MIDDLE);
				}else {
				userservice.registerUser(userrr,passwordd);
				Notification.show("Registration successful! Please log in.");
				UI.getCurrent().navigate("login");
				}
			}else {
				Notification.show("Please fill in al fields.");
			}
		});
		
		H1 head = new H1("SIGNUP");
		
		VerticalLayout tool = new VerticalLayout(head,username,password,signup); 
		tool.addClassName("sign-up");
		tool.setSizeFull();
		tool.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
		tool.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
	   
		add(back,tool);
}
}
