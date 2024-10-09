package com.example.application.login;


import com.example.application.backend.entity.AuditTrail;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

import jakarta.annotation.security.PermitAll;

@Route(value = "login")
@PageTitle("Login | CRM")
@PermitAll
public class LoginView extends VerticalLayout implements BeforeEnterListener  {   //
	
	AuditTrail audit;
	
	LoginForm login = new LoginForm();
	
	public LoginView( )
	{
		
		
		login.addForgotPasswordListener(e -> {
			UI.getCurrent().navigate("forget-password");
		});
		
		login.getElement().getThemeList().add("dark");
		
		addClassName("login-view");
		
		RouterLink signuplink = new RouterLink("SignUp",SignUpView.class);
			
		H1 logo = new H1("CRM");
		logo.addClassName("logo");
				
		setJustifyContentMode(JustifyContentMode.CENTER);
		setAlignItems(Alignment.CENTER);
		
		Div text = new Div("Dont Have A Account?");
		
		text.addClassName("text-signup");
		
		
		HorizontalLayout links = new HorizontalLayout(text,signuplink);
		
		
		login.setAction("login");
		
		add( logo,
				login,
				links
				);
		

	}
	
	 
	@Override
	public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
			
		if(!beforeEnterEvent.getLocation()
				.getQueryParameters()
				.getParameters()
				.containsKey("error"))
		{
			login.setError(true);
		}
		

		
	}
}
