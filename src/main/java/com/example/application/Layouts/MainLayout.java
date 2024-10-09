package com.example.application.Layouts;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.application.backend.entity.AuditTrail;
import com.example.application.backend.entity.Users;
import com.example.application.backend.service.AuditTrailService;
import com.example.application.security.SecurityService;
import com.example.application.ui.AuditTrailView;
import com.example.application.ui.HomeView;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class MainLayout extends AppLayout {
	
	private final Text timeLabel;
	String UserName;
	AuditTrail audit = new AuditTrail();
	 private AuditTrailService auditservice;
	 Users user = new Users();
	
	public MainLayout(SecurityService securityservice,AuditTrailService auditservice) {
		
		this.auditservice = auditservice;
		timeLabel = new Text(getCurrentTime());
		createHeader(securityservice);
		createDrawer();
		chechUserRole();
		
	}
	
	private void createDrawer() {
		
		Button list = new Button("Contacts", e-> UI.getCurrent().navigate("admin"));
		list.addClassName("list-button");
		
		Button user = new Button("Users", e-> UI.getCurrent().navigate("userview"));
		user.addClassName("user-button");
		
        
		addToDrawer(new VerticalLayout(	
				list,
				user
				));
	}	

	private void createHeader(SecurityService securityservice) {
		
		H1 logo = new H1("CRM");
		logo.addClassName("logo");
		
		Button logout = new Button("LOG OUT", e -> securityservice.logout());
		logout.addClassName("log-out");
		
		
		Avatar avt1 = new Avatar(chechUserRole());
		avt1.addClassName("avatar-class");
		
		MenuBar menuBar = new MenuBar();
		menuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);
		
		MenuItem menuItem = menuBar.addItem(avt1);
		
		SubMenu subMenu = menuItem.getSubMenu();
		

		MenuItem headerr = subMenu.addItem(UserName);
		headerr.setEnabled(false);  // Disable the item to make it non-clickable
		headerr.getElement().getStyle().set("font-weight", "bold"); // Make it bold

		
		subMenu.addItem("Activity", e -> UI.getCurrent().navigate(AuditTrailView.class));
		subMenu.addItem("Home", e -> UI.getCurrent().navigate(HomeView.class));
		subMenu.addItem("Sign out", e -> {
			 audit.setChangedBy(UserName);

				audit.setAction("SIGNOUT");	
				//audit.setEntityId(id);
				audit.setChangeDate(LocalDateTime.now());
				audit.setStatuss("Log Out");
				auditservice.save(audit);
		  
		 securityservice.logout();
		 });
		
		
		
		LocalDate currentDate = LocalDate.now();
	    String formattedDate = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	    Div dateLabel = new Div( formattedDate);
	    
	    
	    HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
	    
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
	
	 
	 public String chechUserRole() {
	
		 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		 
		 if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			 
	            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	            
	            // Retrieve the username
	            String username = userDetails.getUsername();
	            UserName = userDetails.getUsername();
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
