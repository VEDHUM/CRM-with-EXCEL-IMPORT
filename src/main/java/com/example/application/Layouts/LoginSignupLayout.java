package com.example.application.Layouts;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class LoginSignupLayout extends AppLayout {
	
	private Text timelable;
	public  LoginSignupLayout(){
		
		createHeader();
			
	}
	

	public void createHeader() {
		
		H1 logo = new H1("CRM");
		logo.addClassName("logo");
		
		Button signup = new Button("SignUp", e ->{
			UI.getCurrent().navigate("signup");
			
		});
		signup.addClassName("Process-Button");
		
		HorizontalLayout header= new HorizontalLayout(logo,signup);
		header.setAlignItems(FlexComponent.Alignment.CENTER);
	
		
		addToNavbar(header);
	}

}
