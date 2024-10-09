package com.example.application.ui;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.application.Layouts.MainLayout;
import com.example.application.backend.entity.AuditTrail;
import com.example.application.backend.entity.Contact;
import com.example.application.backend.service.AuditTrailService;
import com.example.application.backend.service.ContactService;
import com.example.application.controller.ContactController;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.avatar.AvatarVariant;
//import com.example.application.ui.excel.UploadView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.validator.RegexpValidator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.RolesAllowed;


@Route(value= "admin", layout = MainLayout.class)
@PageTitle("Contacts | CRM" )
@RolesAllowed("ADMIN")

public class ListView extends VerticalLayout {
	

    private Grid<Contact> grid = new Grid<>(Contact.class,false);
    List<Contact> list;
    Grid<List<Contact>> rid = new Grid<>();
    TextField filtertext = new TextField();
    public  String cancel;
    
    private AuditTrail audit = new AuditTrail();
    private AuditTrailService auditservice;
	private ContactService contactService;
	Dialog dialog = new Dialog();
	ContactController controller;

    Button updatebutton = new Button();
    Button deletebutton = new Button();
    Button addbutton;
    Button excelbutton;
    Button processbutton;
    Contact contact;
    Button exportbutton;

    private Binder<Contact> binder = new Binder<>(Contact.class);
    
	   TextField firstName = new TextField("First Name");
	   TextField lastName = new TextField("Last Name");
	   TextField email = new TextField("Email");
	   TextField phone  = new TextField("Phone");
	   TextField id = new TextField("ID");
	   ComboBox<Contact.Status> status = new ComboBox<>("Status");
	   
    public ListView(ContactService  contactService, ContactController controller,AuditTrailService auditservice ) {
    	this.auditservice = auditservice;
    	this.contactService = contactService;
    	this.controller = controller;
    	addClassName("list-view");
    	setSizeFull();
        configureGrid();
        configureFilter();
        add(filtertext,configurebuttons(),grid);           
        updateList();
       
        
    }
    
 
    //SEARCH CONTACT
	private void configureFilter() {
		filtertext.setPlaceholder("Filter by....");
		filtertext.setClearButtonVisible(true);
		filtertext.setValueChangeMode(ValueChangeMode.LAZY);
		filtertext.addValueChangeListener(e -> updateList());
	}

	private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.getStyle().set("position", "relative");
        grid.setSizeFull();
     
        grid.addColumn(createAvatarAndNameRenderer()).setHeader("Name").setAutoWidth(true);     
        grid.addColumn(Contact::getPhone).setHeader("Phone no.").setAutoWidth(true);  
        grid.addColumn(Contact::getStatus).setHeader("Status").setAutoWidth(true);  
  
        
        
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
                
        grid.asMultiSelect().addValueChangeListener(evnt -> editContact(evnt.getValue()));
        
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES, GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_COMPACT);

    }
	

	private ComponentRenderer<HorizontalLayout, Contact> createAvatarAndNameRenderer() {
	    return new ComponentRenderer<>(item -> {
	       
	        Avatar avatar = new Avatar();
	        avatar.setName(item.getFirstName()); 
	        avatar.setImage(item.getFirstName().toUpperCase().substring(0)); 
	        avatar.addThemeVariants(AvatarVariant.LUMO_LARGE);
	        
	        String firstname = item.getFirstName();
	        String lastname = item.getLastName();

	       
	        H3 nameLabel = new H3(firstname.toUpperCase() + " " + lastname.toUpperCase());
	        Div emailLabel = new Div(item.getEmail());

	        VerticalLayout layout1 = new VerticalLayout(nameLabel,emailLabel);
	        
	        HorizontalLayout layout = new HorizontalLayout(avatar, layout1);
	        layout.setAlignItems(Alignment.CENTER); 
	      

	        return layout;
	    });
	}
	


	private void editContact(Set<Contact> set) {
		if(set.isEmpty()) {
			updatebutton.setEnabled(false);
			deletebutton.setEnabled(false);
			addbutton.setEnabled(true);
			excelbutton.setEnabled(true);
			exportbutton.setEnabled(true);
		}
		else {
			updatebutton.setEnabled(true);
			deletebutton.setEnabled(true);
			addbutton.setEnabled(false);
			excelbutton.setEnabled(false);
			exportbutton.setEnabled(false);
			
		}
	}
	
	
// BUTTON LAYOUT   
	private HorizontalLayout configurebuttons(){
		
	    AuditTrail audit = new AuditTrail();

		//ADD CONTACT BUTTON
		addbutton = new Button("Add", new Icon(VaadinIcon.PLUS_CIRCLE));
		addbutton.addClickListener(click ->{
			
			Addbuton();

		    
		});
		addbutton.addClassName("add-button");
		
		//UPDATE CONTACT BUTTON
		updatebutton = new Button("Update", new Icon(VaadinIcon.EDIT), event -> {		
			
			audit.setAction("UPDATE");
			openUpdateDialog(null);
			
		});
		updatebutton.addClassName("update-button");
		
		
		
		//DELETE CONTACT BUTTON
	    deletebutton = new Button("Delete", new Icon(VaadinIcon.TRASH), click -> {
	       audit.setAction("DELETE");
	       deletedialog();
	    });
		deletebutton.addClassName("delete-button");
		
		//REFRESH BUTTON
		Button refresh = new Button("Refresh",new Icon(VaadinIcon.REFRESH),click ->refreshGrid());
		refresh.addClassName("Refresh-button");
		
		//EXCEL FILE UPLOAD BUTTN
		excelbutton = new Button("IMPORT DATA");
		excelbutton.addClickListener(click ->{
			audit.setAction("IMPORT");
			uploadbuton();
				
		});

		excelbutton.addClassName("excel-button");
		
		exportbutton = new Button("EXPORT DATA");
		exportbutton.addClassName("download-button");
		exportbutton.addClickListener(event -> {
			audit.setAction("EXPORT");
			Page page = this.getUI().get().getPage();
			page.open("/contact/excel");
			audit.setChangedBy(chechUserRole());
			audit.setAction("EXPORT");	
			audit.setOldValue("Exported Contact Data");  
			audit.setChangeDate(LocalDateTime.now());
			auditservice.save(audit);

		});
		
		updatebutton.setEnabled(false);
		deletebutton.setEnabled(false);
		addbutton.setEnabled(true);
		excelbutton.setEnabled(true);
		exportbutton.setEnabled(true);

		
		HorizontalLayout toolbar = new HorizontalLayout(addbutton,updatebutton,deletebutton,refresh,excelbutton,exportbutton);
    	toolbar.addClassName("toolbar");
    	
    	toolbar.addAndExpand(new HorizontalLayout());
    	
    	toolbar.add(excelbutton,exportbutton);
    	toolbar.setWidthFull();
    	
    	return toolbar;
    	
    }
	
	private void refreshGrid() {
		updateList();
	}
	

	// ADD BUTTON FUNCTION
  public void Addbuton() {
	  
		   Dialog dialog = new Dialog();
	       dialog.setHeaderTitle("Add Contact");
	       dialog.addClassName("add-dialog");
	       
           Contact contact = new Contact();
	      
           
           Grid<Contact> grid = new Grid<>(Contact.class);
           List<Contact> entries = new ArrayList<>();
           
          
           
           grid.setColumns("firstName","lastName","email","phone","status");
           
           phone.setMaxLength(10);
           
           firstName.setClearButtonVisible(true);
           lastName.setClearButtonVisible(true);
           email.setClearButtonVisible(true);
           phone.setClearButtonVisible(true);
           
// VALIDATION FEILDS-------------------------------------------
           
           binder.forField(firstName)
           .asRequired("FirstName is required")
           .withValidator(name -> !name.trim().isEmpty(), "FirstName cannot be Blank")
           .withValidator(name -> !name.matches(".*\\d.*"), "Numbers are not allowed")
           .bind(Contact::getFirstName, Contact::setFirstName);
           
           binder.forField(lastName)
           .asRequired("LastName is required")
           .withValidator(name -> !name.matches(".*\\d.*"), "Numbers are not allowed")
           .bind(Contact::getLastName, Contact::setLastName);
           
           binder.forField(email)
           .asRequired("Email is required")
           .withValidator(Validator.from(emaill -> emaill.contains("@"), "Email must contain '@'"))
           .withValidator(Validator.from(emaill -> emaill.contains(".com"), "Email must contain '.com'"))
           .bind(Contact::getEmail, Contact::setEmail);
           
           binder.forField(phone)
           .asRequired("Phone is required")
           .withValidator(new RegexpValidator("Phone should contain numbers ", "\\d*"))
           .withValidator(phonee -> phonee.length() == 10, "Phone number must be exactly 10 digits")
           .bind(Contact::getPhone, Contact::setPhone);
           
           binder.forField(status)
           .asRequired("Status required")
           .bind(Contact::getStatus, Contact::setStatus);
           

 		   status.setItems(Contact.Status.values());
 		   
		   
	       binder.setBean(contact);
	       
	       Button addTogird = new Button("Add To Grid");
	       addTogird.addClassName("Save-button");
		   addTogird.addClickListener(e ->{
			   
			   String firstname = firstName.getValue();
			   String lastname = lastName.getValue();
			   String emailfeild = email.getValue();
			   String phonefeild = phone.getValue();
			   
			Contact contact1 = new Contact();
			contact1.setFirstName(firstname);
			contact1.setLastName(lastname);
			contact1.setEmail(emailfeild);
			contact1.setPhone(phonefeild);
			contact1.setStatus(status.getValue());			   
			
			
			  
		        boolean emailExists = contactService.checkIfEmailExists(emailfeild);
		        boolean phoneExists = contactService.checkIfPhoneExists(phonefeild);
		        
		        if(emailExists && phoneExists) {
		        	 Notification.show("Both email and phone already exist.", 3000, Notification.Position.MIDDLE);
		        }
		        else if(emailExists) {
		        	Notification.show("Email already exists.", 3000, Notification.Position.MIDDLE);
		        }
		        else if(phoneExists) {
		        	 Notification.show("Phone number already exists.", 3000, Notification.Position.MIDDLE);
		        }
		        else if(firstName.isEmpty() && lastName.isEmpty() && email.isEmpty() && phone.isEmpty() ) {
		  
		        	Notification.show("ALL FEILDS ARE EMPTY", 3000, Notification.Position.MIDDLE);
		        }
		        else if(firstName.isEmpty()) {
		        	Notification.show("FirstName is EMPTY", 3000, Notification.Position.MIDDLE);
		        }
		        else if(lastName.isEmpty()) {
		        	Notification.show("LastName is EMPTY", 3000, Notification.Position.MIDDLE);
		        }
		        else if(email.isEmpty()) {
		        	Notification.show("Email is EMPTY", 3000, Notification.Position.MIDDLE);
		        }
		        else if(phone.isEmpty()) {
		        	Notification.show("Phone is EMPTY", 3000, Notification.Position.MIDDLE);
		        }
		        else if(!binder.isValid()) {
		        	 Notification.show("Correct all Validations", 3000, Notification.Position.MIDDLE);
		        }
			   
		        else {
                   entries.add(contact1);
			       firstName.clear();
			       lastName.clear();
			       email.clear();
			       phone.clear();
			       status.clear();
			       grid.setItems(entries);
			    }
		   });
		     
		   
		   FormLayout tool = new FormLayout();
		   tool.add(firstName,lastName, email, phone, status,addTogird);
		   
		   Button saveButton = new Button(("Save"), new Icon(VaadinIcon.CHECK));
		   saveButton.addClassName("Save-button");
		  

		   
		   saveButton.addClickListener(event -> { 		  
			 
		        	if(!entries.isEmpty()) {
		        	   contactService.save(entries);
		        	  
					   Notification.show("CONTACT ADDED SUCCESSFULLY ");
					   updateList();
					   
					   Contact c1 = entries.iterator().next();
					   
					   String oldValye = buildContactInfoString(c1);
					   
					    audit.setChangedBy(chechUserRole());
						audit.setAction("ADD");	
						audit.setEntityId(contact.getId());
						audit.setChangeDate(LocalDateTime.now());
						audit.setOldValue(oldValye);
						auditservice.save(audit);
					    dialog.close();
					}else {
						 Notification.show("GRID IS EMPTY", 3000, Notification.Position.MIDDLE);
					}
	 	   
	        });

		   
		
		   saveButton.addClickShortcut(Key.ENTER);
	
		   Button cancelButton = new Button("Cancel",new Icon(VaadinIcon.CLOSE) , click -> {
			   dialog.close();
			   });
		   cancelButton.addClassName("Cancel-Button");
		   cancelButton.addClickShortcut(Key.ESCAPE);
		   
		  
		   
		   HorizontalLayout button = new HorizontalLayout(saveButton,cancelButton);
	
		   dialog.add(tool,grid,button);
		   dialog.open();
		   dialog.getFooter().add(saveButton,cancelButton);;
		   dialog.setSizeFull();
	   }
	  
	  
	  
//DELETE BUTTON FUNCTION
   private void deletedialog() {
	   
	   
	   Set<Contact> selectedContacts = grid.getSelectedItems();
	   
	     if (selectedContacts.isEmpty()) {
	        Notification.show("No contacts selected!", 3000, Notification.Position.MIDDLE);
	        return;
	     }
	     
	     
	   Dialog dialog = new Dialog();
	   dialog.setHeaderTitle("Delete contacts:"+ selectedContacts.size());
	   Div message = new Div(" Are you sure to delete this contacts ? ");
	   Button yes = new Button("DELETE",new Icon(VaadinIcon.CHECK), evnt -> {
		   if (!selectedContacts.isEmpty()) {

	    	   selectedContacts.forEach(selectedcontact -> contactService.delete(selectedcontact));
	    	   
	    	   String contactsss = selectedContacts.toString();
	    
	            audit.setChangedBy(chechUserRole());
				audit.setAction("DELETE");	
				//audit.setEntityId(contact.getId());
				audit.setOldValue(contactsss);  // Set old value  
				audit.setChangeDate(LocalDateTime.now());
				auditservice.save(audit);
	        
	           dialog.close();
	           updateList();
	           Notification.show("CONTACT DELETED SUCCESSFULLY ");
	   }
	   });
	   yes.addClassName("Save-button");
	   yes.addClickShortcut(Key.ENTER);
	   yes.getStyle().set("margin-right", "auto");
	   
	   Button no = new Button("CANCEL",new Icon(VaadinIcon.CLOSE), click -> dialog.close());
	   no.addClickShortcut(Key.ESCAPE);
	   no.addClassName("Cancel-Button");
	   HorizontalLayout bar = new HorizontalLayout(yes, no);
	   dialog.add(message,bar);

	   dialog.open();
   }


   // UPDATE BUTTON FUNCTION 
   private void openUpdateDialog(Long id) {
	   
	 Set<Contact> selectedContacts = grid.getSelectedItems() ;
     
     if (selectedContacts.isEmpty()) {
         Notification.show("No contacts selected!", 3000, Notification.Position.MIDDLE);
         return;
     }else if (selectedContacts.size() != 1) {
         Notification.show("Please select exactly one contact to update.", 3000, Notification.Position.MIDDLE);
         return;
     }

       Dialog dialog = new Dialog();
       dialog.setHeaderTitle("Update Contact");
       
       Contact contact = selectedContacts.iterator().next();
       
       Contact oldContact = new Contact();
       oldContact.setFirstName(contact.getFirstName());
       oldContact.setLastName(contact.getLastName());
       oldContact.setEmail(contact.getEmail());
       oldContact.setPhone(contact.getPhone());
       oldContact.setStatus(contact.getStatus());
       
       
       binder.forField(firstName)
       .asRequired("FirstName is required")
       .withValidator(name -> !name.matches(".*\\d.*"), "Numbers are not allowed")
       .bind(Contact::getFirstName, Contact::setFirstName);
       
       binder.forField(lastName)
       .asRequired("LastName is required")
       .withValidator(name -> !name.matches(".*\\d.*"), "Numbers are not allowed")
       .bind(Contact::getLastName, Contact::setLastName);
       
       binder.forField(email)
       .asRequired("Email is required")
       .withValidator(Validator.from(email -> email.contains("@"), "Email must contain '@'"))
       .withValidator(Validator.from(email -> email.contains(".com"), "Email must contain '.com'"))
       .bind(Contact::getEmail, Contact::setEmail);
       
       binder.forField(phone)
       .asRequired("Phone is required")
       .withValidator(new RegexpValidator("Phone should contain numbers ", "\\d*"))
       .withValidator(phone -> phone.length() == 10, "Phone number must be exactly 10 digits")
       .bind(Contact::getPhone, Contact::setPhone);
       
       binder.forField(status)
       .asRequired("Status required")
       .bind(Contact::getStatus, Contact::setStatus);
       
       
      // binder.bindInstanceFields(this);
       status.setItems(Contact.Status.values());
       
 
    	   
          binder.setBean(contact);
       
          FormLayout formLayout = new FormLayout();
          formLayout.add(firstName, lastName, email, phone, status);
       
          phone.setMaxLength(10);
       
          firstName.setClearButtonVisible(true);
          lastName.setClearButtonVisible(true);
          email.setClearButtonVisible(true);
          phone.setClearButtonVisible(true);

          Button saveButton = new Button("Save",new Icon(VaadinIcon.CHECK),  event -> {
			  String emailfeild = email.getValue();
			  String phonefeild = phone.getValue();
			  
		        boolean emailExists = false;
		        boolean phoneExists = false;
			  
			  emailExists = contactService.checkIfEmailExistsExcludingCurrent(emailfeild,  contact.getId());
			  phoneExists = contactService.checkIfPhoneExistsExcludingCurrent(phonefeild,  contact.getId());
		        
		        if(emailExists && phoneExists) {
		        	 Notification.show("Both email and phone already exist.", 3000, Notification.Position.MIDDLE);
		        }
		        else if(emailExists) {
		        	Notification.show("Email already exists.", 3000, Notification.Position.MIDDLE);
		        }
		        else if(phoneExists) {
		        	 Notification.show("Phone number already exists.", 3000, Notification.Position.MIDDLE);
		        }
		        else if(!binder.isValid()) {
		        	 Notification.show("Correct all Validations", 3000, Notification.Position.MIDDLE);
		        }
		        else {
		           contactService.save(binder.getBean());
		           grid.getDataProvider().refreshItem(contact);
		           updateList();
		           
		           String oldValye = buildContactInfoString(oldContact);
		           String newValue = buildContactInfoString(contact);
		           String action = checkWhichIsUpdated(oldContact,contact).toString();
		           
		            cancel = "SAVED";
		            audit.setChangedBy(chechUserRole());
					audit.setAction(action);	
					audit.setEntityId(contact.getId());
					audit.setOldValue(oldValye);  // Set old value
			        audit.setNewValue(newValue);  
					audit.setChangeDate(LocalDateTime.now());
					audit.setStatuss(cancel);
					auditservice.save(audit);
		           dialog.close();
		           Notification.show("CONTACT UPDATED SUCCESSFULLY ");
					   
		        }
    	   
    	   
         });

         saveButton.addClassName("Save-button");
         saveButton.setAutofocus(true);
         saveButton.addClickShortcut(Key.ENTER);
       
         Button cancelButton = new Button("Cancel",new Icon(VaadinIcon.CLOSE), event ->{
        	   
         dialog.close();}
         );
         cancelButton.addClickShortcut(Key.ESCAPE);
         cancelButton.addClassName("Cancel-Button");
         
         HorizontalLayout bar = new HorizontalLayout(saveButton, cancelButton);
         dialog.add(formLayout, bar);
       
       dialog.open();
   }
   
   
   private StringBuilder checkWhichIsUpdated(Contact oldContact,Contact newContact) {
	   
	   StringBuilder actionmessage = new StringBuilder();
	   
	   actionmessage.append("Updated");
	   if(!oldContact.getFirstName().equals(newContact.getFirstName())){
		   actionmessage.append(" ");
		   actionmessage.append(" FirstName");
	   }
	   if(!oldContact.getLastName().equals(newContact.getLastName())){
		   actionmessage.append(" ");
		   actionmessage.append(" LastName");
	   }
	  if(!oldContact.getEmail().equals(newContact.getEmail())){
		  actionmessage.append(" ");
		  actionmessage.append( "  Email");
	   }
	  if(!oldContact.getPhone().equals(newContact.getPhone())){
		  actionmessage.append(" ");
		  actionmessage.append (" PhoneNumber");
	   }
	  if(!oldContact.getStatus().equals(newContact.getStatus())){
		  actionmessage.append(" ");
		  actionmessage.append( " Status");
	   }
	   return actionmessage ;
	}
   
   private String buildContactInfoString(Contact contact) {
	   
	   return "First Name: \n" + contact.getFirstName() +" "+
	           ", Last Name: \n" + contact.getLastName() +" "+
	           ", Email: \n" + contact.getEmail() +" "+
	           ", Phone: \n" + contact.getPhone() +" "+
	           ", Status: \n" + contact.getStatus();   
   }
   
	// FUNCTION FOR FINDING SEARHED NAME
	private void updateList() {
		grid.setItems(contactService.findAll(filtertext.getValue()));
	}
	
	
	//UPLOAD BUTTON
      public void uploadbuton () { 
		
		Dialog dialog = new Dialog();
		
		Grid<Contact> rid = new Grid<>();
	    MemoryBuffer buffer = new MemoryBuffer(); 
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xls", ".xlsx");
        
        Button uploadbutton = new Button("SAVE", new Icon(VaadinIcon.CHECK));
        uploadbutton.addClassName("Save-button");
        uploadbutton.setEnabled(false);
        
        List<Contact> contactList = new ArrayList<>();
        
            processbutton = new Button("Process Excel File", e -> { 
            processExcel(buffer.getInputStream(), contactList);
            rid.setItems(contactList);
            uploadbutton.setEnabled(true);
           
         });
         processbutton.setEnabled(false);
         
         upload.addFinishedListener(e -> {
          processbutton.setEnabled(true);
          
         });
       
         upload.addFileRemovedListener( event ->{
        	 contactList.clear();
        	 rid.setItems(contactList);
        	 processbutton.setEnabled(false);
        	 uploadbutton.setEnabled(false);
         });
        
        processbutton.addClassName("Process-Button");
         uploadbutton.addClickListener( e -> {
            if(!contactList.isEmpty()) {            	
                List<Contact> uniqueContacts = contactList.stream()
                        .filter(contact -> "PASS".equals(contact.getStatts()))
                        .collect(Collectors.toList());
                if(!uniqueContacts.isEmpty()) {
                	
         	       this.contactService.save(uniqueContacts);  
         	       updateList();
         	      String valess = uniqueContacts.toString();
         	      audit.setChangedBy(chechUserRole());
  				  audit.setAction("IMPORTED");	
  		          audit.setNewValue(valess);
  				  audit.setOldValue("Data IMPORTED");  // Set old value  
  				  audit.setChangeDate(LocalDateTime.now());
  				  auditservice.save(audit);
         	      Notification.show("PASSED Datas are Saved" , 3000, Notification.Position.MIDDLE);
         	      dialog.close();
            }else {
            	Notification.show("All Datas Failed" , 3000, Notification.Position.MIDDLE);
            	dialog.close();
            }
            
            
        }
      });
         
         
   //---------------TEMPLETE BUTTON-----------
        Button templete = new Button("Download Templete", e ->
        {
			Page page = this.getUI().get().getPage();
			page.open("/contact/templete");
        });
        templete.addClassName("Cancel-Button");
        templete.getStyle().set("margin-right", "auto");
        
        
        Button cancelButton = new Button(("Cancel"), new Icon(VaadinIcon.CLOSE), click -> dialog.close());
        cancelButton.addClassName("Cancel-Button");
        dialog.setHeaderTitle("Import Data from Excel");
    
        processbutton.getStyle().set("margin-right", "auto");
        processbutton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);
        
        
        rid.setSizeFull();
        
        rid.addColumn(Contact::getFirstName).setHeader("FIRST NAME").setAutoWidth(true);
        rid.addColumn(Contact::getLastName).setHeader("LAST NAME").setAutoWidth(true);
        rid.addColumn(Contact::getEmail).setHeader("EMAIL").setAutoWidth(true);
        rid.addColumn(Contact::getPhone).setHeader("PHONE").setAutoWidth(true);
        rid.addColumn(Contact::getFault).setHeader("FAULT").setAutoWidth(true);
        rid.addColumn(Contact::getStatts).setHeader("STATUS").setAutoWidth(true);
        

        
        dialog.getFooter().add(processbutton);
		dialog.getFooter().add(cancelButton);
		dialog.getFooter().add(uploadbutton);
        dialog.add(templete,upload, rid);
        dialog.setSizeFull();
        dialog.open();

	}
	

private void processExcel(InputStream inputStream, List<Contact> list) {
   	
	
   	//Contact p = new Contact();
       try (Workbook workbook = new XSSFWorkbook(inputStream)) {
           Sheet sheet = null;
           for(int i = 0;  i < workbook.getNumberOfSheets(); i++) {
        	   Sheet currentSheet = workbook.getSheetAt(i);   
        	   sheet = currentSheet;
           

          
           	int rowNumber = 0;
   			Iterator<Row> iterator = sheet.iterator();
   			
               while (iterator.hasNext()) {
                   Row row = iterator.next();

                   if (rowNumber == 0) {
                       rowNumber++;
                       continue;
                   }
                   
                   String firstname = row.getCell(0).getStringCellValue();
                   String lastname = row.getCell(1).getStringCellValue();
                   String email = row.getCell(2).getStringCellValue();
                   String phone = getCellValueAsString(row.getCell(3));

                       Contact contact = new Contact();
                       contact.setFirstName(firstname);
                       contact.setLastName(lastname);
                       contact.setEmail(email);
                       contact.setPhone(phone);
                       
                    
                       boolean isEmailDuplicate = contactService.checkIfEmailExists(email);
                       boolean isPhoneDuplicate = contactService.checkIfPhoneExists(phone);
                       boolean isFirstNameFalse = contactService.checkIfFirstNameValid(firstname);
                       boolean isLastName = contactService.checkIfLastNameValid(lastname);
                       
                       StringBuilder faultMessage = new StringBuilder();
                       boolean hasErrors = false;
                       
                       if(isEmailDuplicate) {
                    	   faultMessage.append(" Email already EXISTS. ");
                    	   hasErrors = true;
                    	   }
                       if(isPhoneDuplicate) {
                    	   faultMessage.append(" Phone no. already EXISTS.");
                    	   hasErrors = true;
                       }
                       if(isFirstNameFalse) {
                    	   faultMessage.append(" First Name contains numeric values. ");
                    	    hasErrors = true;
                    	}
                    	if (isLastName) {
                    	    faultMessage.append(" Last Name contains numeric values. ");
                    	    hasErrors = true;
                    	}if(phone.length() != 10) {
                    		 faultMessage.append(" Phone no. Greater or less Than 10 digits.");
                     	    hasErrors = true;
                    	}
                    	
                    	if(hasErrors) {
                    		contact.setFault(faultMessage.toString().trim());
                    		contact.setStatts("FAIL");
                    		}
                    			
                    		
                    	else {
                    		 contact.setFault("All DATA are VALID");
                    		 contact.setStatts("PASS");
                    	}
                       
                       
                       list.add(contact);
       
               }
           }
                                 
        Notification.show("Excel file processed successfully!",3000, Notification.Position.MIDDLE);
        processbutton.setEnabled(false);

        
       } catch (Exception e) {
           Notification.show("Error processing Excel file: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
           e.printStackTrace();
       }            
   }

 private String getCellValueAsString(Cell cell) {
	if (cell == null) {
        return "";
    }
    switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString();
        case BOOLEAN:
            return Boolean.toString(cell.getBooleanCellValue());
        case FORMULA:
            return cell.getCellFormula();
        	
        case BLANK:
            return "";
            
        default:
            return "";
    }
}
 public String chechUserRole() {
		
	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	 
	 if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // Retrieve the username
            String username = userDetails.getUsername();
           
            return username;
	 }
	 return "No authenticated user found.";
 }

	
}

	