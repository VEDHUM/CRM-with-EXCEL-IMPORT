package com.example.application.ui;



import com.example.application.Layouts.UserLayout;
import com.example.application.backend.entity.Contact;
import com.example.application.backend.service.ContactService;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;


@Route(value= "user", layout = UserLayout.class)
@PageTitle("Contact | CRM")
@PermitAll
public class UserView extends VerticalLayout{
	
	
	private Grid<Contact> grid = new Grid<>(Contact.class);
	TextField filtertext = new TextField();
	Button update = new Button();
	
	private ContactService contactService;
	Dialog dialog = new Dialog();
	
	BeanValidationBinder<Contact> binder = new BeanValidationBinder<>(Contact.class);
	
	ComboBox<Contact.Status> status = new ComboBox<>("Status");
		
	public UserView(ContactService contactService) 
	{
		this.contactService = contactService;
		addClassName("user-view");
		setSizeFull();
		configureGrid();
		configureFilter();
		
		update = new Button("UPDATE", click -> updateStatus());
		update.addClassName("update-button");
		update.setEnabled(false);
		
		add(filtertext,update,grid);
		updateList();
		
	}

	private void updateStatus() {
		
		Contact contact = grid.asSingleSelect().getValue();
		
		Dialog dialog = new Dialog();
		
		dialog.setHeaderTitle("Update Contact Status");
		
		binder.forField(status)
		.asRequired("Update Status")
		.bind(Contact::getStatus, Contact::setStatus);
		
		status.setItems(Contact.Status.values());
		
		binder.setBean(contact);
		
		FormLayout formLayout = new FormLayout();
        formLayout.add(status);
        
        Button save = new Button("Save", new Icon(VaadinIcon.CHECK), event ->{
        	 contactService.save(binder.getBean());
	           grid.getDataProvider().refreshItem(contact);
	           updateList();
	           dialog.close();
	           Notification.show("STATUS UPDATED SUCCESSFULLY ");
        });
        save.addClassName("Save-button");
		
        Button cancelButton = new Button("Cancel",new Icon(VaadinIcon.CLOSE), event -> dialog.close());
        cancelButton.addClassName("Cancel-Button");
        cancelButton.addClickShortcut(Key.ESCAPE);
        
        HorizontalLayout bar = new HorizontalLayout(save, cancelButton);
        dialog.add(formLayout, bar);
      
        dialog.open();
	}

	private void configureFilter() {
		
		filtertext.setPlaceholder("Filter by....");
		filtertext.setClearButtonVisible(true);
		filtertext.setValueChangeMode(ValueChangeMode.LAZY);
		filtertext.addValueChangeListener(e -> updateList());
		
	}



	private void configureGrid() {
		grid.addClassName("contact-grid");
		grid.setSizeFull();
		grid.getStyle().set("position", "relative");
		grid.setColumns("id","firstName","lastName","email","phone","status");
		grid.getColumns().forEach(col -> col.setAutoWidth(true));
		grid.setSelectionMode(Grid.SelectionMode.SINGLE);
		
		grid.asSingleSelect().addValueChangeListener(event ->
        editContact(event.getValue()));
		
		
	}



	  public void editContact(Contact contact) {
	        if (contact == null) {
	            update.setEnabled(false);
	        } else {
	        	update.setEnabled(true);
	        }
	    }

	private void updateList() {
		grid.setItems(contactService.findAll(filtertext.getValue()));
	}
	

	

}
