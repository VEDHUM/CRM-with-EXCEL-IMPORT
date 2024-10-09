package com.example.application.login;

import com.example.application.backend.entity.Users;
import com.example.application.backend.service.UserService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@Route("forget-password")
@AnonymousAllowed
public class ForgetPassword extends VerticalLayout {

	private UserService userService;

	
	public  ForgetPassword(UserService userService) {
		
		this.userService = userService;
		
		Button back = new Button("Back", new Icon(VaadinIcon.ARROW_LEFT) , e ->{
			UI.getCurrent().navigate("login");
		});
		
		TextField Username = new TextField("Enter UserName");
		
		Button submit = new Button("Submit");
		
		submit.addClickListener(event ->{
			
			String username = Username.getValue();
			
			Users user =  userService.findByUsername(username);
			
			if(userService.existsByUsername(username) == false){
				Notification.show("UserName Not Found", 3000, Notification.Position.MIDDLE);
			}
			else {
			Dialog dialog = new Dialog();
			dialog.setHeaderTitle("Password in EncrptedForm");
			Div pass = new Div(user.getPassword());
			Button done = new Button("Done", e -> {
				dialog.close();
			    UI.getCurrent().navigate("/login");
			});
			dialog.add(pass,done);
			dialog.open();
			}
			
			
		});
		H2 head = new H2("Forget Password");
		VerticalLayout tool = new VerticalLayout(head,Username, submit);
		tool.addClassName("sign-up");
		tool.setSizeFull();
		tool.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
		add(back,tool);
	}
}
