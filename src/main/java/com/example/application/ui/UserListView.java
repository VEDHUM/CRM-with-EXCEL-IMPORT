package com.example.application.ui;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.application.Layouts.MainLayout;
import com.example.application.backend.entity.Contact;
import com.example.application.backend.entity.Users;
import com.example.application.backend.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;


@Route(value = "userview", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class UserListView extends VerticalLayout {
	
	private Grid<Users> grid = new Grid<>(Users.class);
	UserService userservice;
	TextField filtertext = new TextField();
	Button updatebutton = new Button();
    Button deletebutton = new Button();
	Button addbutton;
	private PasswordEncoder passwordEncoder;
	
	 private BeanValidationBinder<Users> binder = new BeanValidationBinder<>(Users.class);
	    
	   TextField username = new TextField("User Name");
	   TextField password = new TextField("Password");
       ComboBox<Boolean> enabled = new  ComboBox<>("Enabled");
       ComboBox<String> role = new ComboBox<>("ROLE");
	
	public  UserListView(UserService userservice,PasswordEncoder passwordEncoder) {
		
		this.userservice = userservice;
		this.passwordEncoder = passwordEncoder;
		configureGrid();
		configureFilter();
		setSizeFull();
		add(filtertext,configurebuttons(),grid);
		updateList();
	}

	private void configureFilter() {
		
		filtertext.setPlaceholder("Filter by....");
		filtertext.setClearButtonVisible(true);
		filtertext.setValueChangeMode(ValueChangeMode.LAZY);
		filtertext.addValueChangeListener(e -> updateList());
		
	}

	private void updateList() {
		
			grid.setItems(userservice.findAll(filtertext.getValue()));
	}
	

	private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.getStyle().set("position", "relative");
        grid.setSizeFull();
        
//        grid.setColumns("username","enabled","role");
        
        
//        grid.addColumn(Users::getPassword).setHeader("Password").setAutoWidth(true);
            
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
    
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

    }
private HorizontalLayout configurebuttons(){
		
		//ADD CONTACT BUTTON
		addbutton = new Button("Add", new Icon(VaadinIcon.PLUS_CIRCLE));
		addbutton.addClickListener(click -> Addbuton());
		addbutton.addClassName("add-button");
		
		//UPDATE CONTACT BUTTON
		updatebutton = new Button("Update", new Icon(VaadinIcon.EDIT), click -> openUpdateDialog());
		updatebutton.addClassName("update-button");
		
		
		
		//DELETE CONTACT BUTTON
	    deletebutton = new Button("Delete", new Icon(VaadinIcon.TRASH), click -> deletedialog());
		deletebutton.addClassName("delete-button");
		
		//REFRESH BUTTON
				Button refresh = new Button("Refresh",new Icon(VaadinIcon.REFRESH),click ->refreshGrid());
				refresh.addClassName("Refresh-button");
		
		HorizontalLayout toolbar = new HorizontalLayout(addbutton,updatebutton,deletebutton,refresh);
    	toolbar.addClassName("toolbar");
    	
    	return toolbar;

}

private void deletedialog() {
	
	Set<Users> user = grid.getSelectedItems();
	
	Dialog dialog = new Dialog();
	dialog.setHeaderTitle("Delete User");
	
	Button yes = new Button("Yes",new Icon(VaadinIcon.CHECK), e-> {
		user.forEach(selectedcontact -> userservice.delete(selectedcontact));
		//userservice.delete(user);
		dialog.close();
		updateList();
	});
	yes.addClassName("Save-button");
	Button no = new Button("No",new Icon(VaadinIcon.CLOSE), e-> dialog.close());
	no.addClassName("Cancel-Button");
	 HorizontalLayout button = new HorizontalLayout(yes,no);
    dialog.getFooter().add(button);
   
	dialog.open();
}

private void openUpdateDialog() {
	
	Set<Users> users = grid.getSelectedItems();
	
	Dialog dialog = new Dialog();
	dialog.setHeaderTitle("Update User");
	
	 Users user = users.iterator().next();
	 
     binder.forField(username).asRequired("UserName is Required").bind(Users::getUsername, Users::setUsername);
     
     binder.forField(password).asRequired("Pasword is Required").bind(Users::getPassword, Users::setPassword);
     
     binder.forField(enabled).asRequired("Set Enable").bind(Users::isEnabled, Users::setEnabled);
     
     binder.forField(role).asRequired("Set ROLE").bind(Users::getRole, Users::setRole);
     
     
     enabled.setItems(true,false);
     role.setItems("ROLE_ADMIN","ROLE_USER");
     
     binder.setBean(user);
     
     FormLayout tool = new FormLayout();
     tool.add(username,password,enabled,role);
     
     Button save = new Button("Save", new Icon(VaadinIcon.CHECK) );
     save.addClassName("Save-button");
     save.addClickListener( e -> {
    	 if(binder.isValid()) {
    	 userservice.save(user);
         grid.getDataProvider().refreshItem(user);
         updateList();
         dialog.close();
         Notification.show("CONTACT UPDATED SUCCESSFULLY ");
    	 }
    	 else {
    		 Notification.show("Correct Fields ");
    	 }
     });
     
     
     Button cancel = new Button("Cancel",new Icon(VaadinIcon.CLOSE), e-> dialog.close());
     cancel.addClassName("Cancel-Button");
     HorizontalLayout button = new HorizontalLayout(save,cancel);
     dialog.add(tool,button);
     dialog.open();
	
}

private void Addbuton() {
	
	 Dialog dialog = new Dialog();
     dialog.setHeaderTitle("Add User");
     dialog.addClassName("user-dialog");
     Users user = new Users();
     
     binder.forField(username).asRequired("UserName is Required").bind(Users::getUsername, Users::setUsername);
     
     binder.forField(password).asRequired("Pasword is Required").bind(Users::getPassword, Users::setPassword);
     
     binder.forField(enabled).asRequired("enabled is Required").bind(Users::isEnabled, Users::setEnabled);
     
     
     enabled.setItems(true,false);
     
     
     binder.setBean(user);
     
     FormLayout tool = new FormLayout();
     tool.add(username,password,enabled);
     
     Button save = new Button("Save", new Icon(VaadinIcon.CHECK));
     save.addClassName("Save-button");
     save.addClickListener( e-> {
    	 if(binder.isValid()) {
    	 userservice.save(binder.getBean());
    	 updateList();
    	 dialog.close();
     }
     });
     
     
     Button cancel = new Button("Cancel",new Icon(VaadinIcon.CLOSE), e-> dialog.close());
     cancel.addClassName("Cancel-Button");
     HorizontalLayout button = new HorizontalLayout(save,cancel);
     dialog.add(tool,button);
     dialog.open();   
         
}
private void refreshGrid() {
	updateList();
}

}
