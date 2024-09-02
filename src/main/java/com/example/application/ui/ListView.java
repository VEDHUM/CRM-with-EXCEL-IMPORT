package com.example.application.ui;

import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.application.backend.entity.Contact;
import com.example.application.backend.service.ContactService;
import com.vaadin.flow.component.Key;
//import com.example.application.ui.excel.UploadView;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Route;


@Route("")
public class ListView extends VerticalLayout {

    private Grid<Contact> grid = new Grid<>(Contact.class);
    Grid<Contact> rid = new Grid<>(Contact.class);
    TextField filtertext = new TextField();
    List<Contact> list;
   // UploadView upload;
	private ContactService contactService;
    Button updatebutton;
    Button deletebutton;
    Button addbutton;
    Button excelbutton;
    Contact contact;

    private Binder<Contact> binder = new Binder<>(Contact.class);
    
	   TextField firstName = new TextField("First Name");
	   TextField lastName = new TextField("Last Name");
	   TextField email = new TextField("Email");
	   TextField phone  = new TextField("Phone");
	   
    public ListView(ContactService  contactService) {
    	
    	this.contactService = contactService;
    	addClassName("list-view");
    	setSizeFull();
        configureGrid();
        configureFilter();
        
        add(filtertext,configurebuttons(),grid);
        updateList();
    }
    
    //SEARCH CONTACT
	private void configureFilter() {
		filtertext.setPlaceholder("Filter by name..");
		filtertext.setClearButtonVisible(true);
		filtertext.setValueChangeMode(ValueChangeMode.LAZY);
		filtertext.addValueChangeListener(e -> updateList());
	}

	private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.getStyle().set("position", "relative");
        grid.setSizeFull();
        grid.setColumns("id","firstName", "lastName", "email","phone");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));    
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        //FUNCTION FOR SINGLE SELECT AND GET VALUE
        grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));
            
    
    }

// SINGLE SELECT VALUE
	private void editContact(Contact contact) {
		if(contact == null) {
			updatebutton.setEnabled(false);
			deletebutton.setEnabled(false);
		}
		else {
			updatebutton.setEnabled(true);
			deletebutton.setEnabled(true);
		}
	}
	
	
// BUTTON LAYOUT   
	private HorizontalLayout configurebuttons(){
		
		Dialog dialog = new Dialog();
		//UploadView upload = new UploadView();
		//ADD CONTACT BUTTON
		addbutton = new Button("Add", new Icon(VaadinIcon.PLUS_CIRCLE));
		addbutton.addClickListener(click -> Addbuton());
		addbutton.addClassName("add-button");
		
		//UPDATE CONTACT BUTTON
		updatebutton = new Button("Update", new Icon(VaadinIcon.EDIT), event -> openUpdateDialog());
		updatebutton.addClassName("update-button");
		updatebutton.setEnabled(false);
		
		//DELETE CONTACT BUTTON
	    deletebutton = new Button("Delete", new Icon(VaadinIcon.TRASH), click -> deletedialog());
		deletebutton.addClassName("delete-button");
		deletebutton.setEnabled(false);
		
		//EXCEL FILE UPLOAD BUTTN
		excelbutton = new Button("IMPORT DATA");
		excelbutton.addClickListener(click ->{
				uploadbuton();
				
		});

		excelbutton.addClassName("excel-button");
		
		
		HorizontalLayout toolbar = new HorizontalLayout(addbutton,updatebutton,deletebutton,excelbutton);
    	toolbar.addClassName("toolbar");
    	return toolbar;
    	
    }
	
	
// ADD BUTTON FUNCTION
	  public void Addbuton() {
		   
		  
		   Dialog dialog = new Dialog();
	       dialog.setHeaderTitle("Add Contact");
	       dialog.addClassName("add-dialog");
           Contact contact = new Contact();
           
     
           phone.setMaxLength(10);
           
           firstName.setClearButtonVisible(true);
           lastName.setClearButtonVisible(true);
           email.setClearButtonVisible(true);
           phone.setClearButtonVisible(true);
         
 		   binder.bindInstanceFields(this);
	       binder.setBean(contact);
		   FormLayout tool = new FormLayout();
		   tool.add(firstName,lastName, email, phone);
		   
		   Button saveButton = new Button(("Add"));
		   saveButton.addClassName("save-button");
		   saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
		   saveButton.addClickListener(event -> {
			   contactService.save(binder.getBean());
			   dialog.close();
			   Notification.show("CONTACT ADDED SUCCESSFULLY ");
			   updateList();
		   });
		   saveButton.getStyle().set("margin-right", "auto");
		   saveButton.addClickShortcut(Key.ENTER);
		   
		   Button cancelButton = new Button("Cancel" , click -> dialog.close());
		   cancelButton.addClassName("cancel-button");
		   cancelButton.addClickShortcut(Key.ESCAPE);
		   
		   dialog.getFooter().add(saveButton);
		   dialog.getFooter().add(cancelButton);
		   dialog.add(tool);
		   dialog.open();
	   }
	   
	  
	  //DELETE BUTTON FUNCTION
   private void deletedialog() {
	   
	   Contact contact = grid.asSingleSelect().getValue();
	   Dialog dialog = new Dialog();
	   dialog.setHeaderTitle("Delete contact   '"+ contact.getFirstName()+" "+contact.getLastName()+"'");
	   Div message = new Div(" Are you sure to delete this contact ? ");
	   Button yes = new Button("DELETE", evnt -> {
		   if (contact != null) {
	    	   deletebutton.setEnabled(true);
	           contactService.delete(contact);
	           dialog.close();
	           updateList();
	           Notification.show("CONTACT DELETED SUCCESSFULLY ");
	   }
	   });
	   yes.addThemeVariants(ButtonVariant.LUMO_PRIMARY,ButtonVariant.LUMO_ERROR);
	   yes.addClickShortcut(Key.ENTER);
	   yes.getStyle().set("margin-right", "auto");
	   
	   Button no = new Button("CANCEL", click -> dialog.close());
	   no.addClickShortcut(Key.ESCAPE);
	   //HorizontalLayout bar = new HorizontalLayout(yes, no);
	   dialog.add(message);
	   dialog.getFooter().add(yes);
	   dialog.getFooter().add(no);
	   dialog.open();
   }


   // UPDATE BUTTON FUNCTION 
   private void openUpdateDialog() {
       Contact contact = grid.asSingleSelect().getValue();
       if (contact == null) {
           return;
       }

       Dialog dialog = new Dialog();
       dialog.setHeaderTitle("Update Contact '"+ contact.getFirstName()+" "+contact.getLastName()+"'");
       
       
       binder.bindInstanceFields(this);
       binder.setBean(contact);
       
       FormLayout formLayout = new FormLayout();
       formLayout.add(firstName, lastName, email, phone);
       
       phone.setMaxLength(10);
       
       firstName.setClearButtonVisible(true);
       lastName.setClearButtonVisible(true);
       email.setClearButtonVisible(true);
       phone.setClearButtonVisible(true);

       Button saveButton = new Button("Save", event -> {
           contactService.save(binder.getBean());
           grid.getDataProvider().refreshItem(contact);
           updateList();
           dialog.close();
           Notification.show("CONTACT UPDATED SUCCESSFULLY ");
       });
       
       saveButton.setAutofocus(true);
       saveButton.addClickShortcut(Key.ENTER);
       
       Button cancelButton = new Button("Cancel", event -> dialog.close());
       cancelButton.addClickShortcut(Key.ESCAPE);
       
       HorizontalLayout bar = new HorizontalLayout(saveButton, cancelButton);
       dialog.add(formLayout, bar);
       dialog.open();
   }
   
	// FUNCTION FOR FINDING SEARHED NAME
	private void updateList() {
		grid.setItems(contactService.findAll(filtertext.getValue()));
	}
	
	
	//UPLOAD BUTTON
      public void uploadbuton () { 
		
		Dialog dialog = new Dialog();
		dialog.setSizeFull();
		Grid<Contact> rid = new Grid<>(Contact.class);
	    MemoryBuffer buffer = new MemoryBuffer(); 
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes(".xls", ".xlsx");


        Button processbutton = new Button("Process Excel File", e -> { processExcel(buffer.getInputStream());
//        dialog.close();
        });
        

       
        Button cancelButton = new Button(("Cancel"), click -> dialog.close());
        dialog.setHeaderTitle("Import Data from Excel");
        processbutton.getStyle().set("margin-right", "auto");
        processbutton.addClickShortcut(Key.ENTER);
        cancelButton.addClickShortcut(Key.ESCAPE);
        
        rid.setColumns("firstName", "lastName", "email","phone");
        rid.setSizeFull();
        rid.getColumns().forEach(col -> col.setAutoWidth(true));
        
        
//        Button uploadbutton = new Button("Upload",  e -> { addlist();
//        dialog.close();
//        });
        
        dialog.getFooter().add(processbutton);
		dialog.getFooter().add(cancelButton);
//		dialog.getFooter().add(uploadbutton);
        dialog.add(upload,rid);
        dialog.open();
        addlist();
	}
	

private void processExcel(InputStream inputStream) {
   	
	
   	List<Contact> list = new ArrayList<>();
       try (Workbook workbook = new XSSFWorkbook(inputStream)) {
           Sheet sheet = workbook.getSheet("Sheet1"); 

          
           	int rowNumber = 0;
   			Iterator<Row> iterator = sheet.iterator();
   			
               while (iterator.hasNext()) {
                   Row row = iterator.next();

                   if (rowNumber == 0) {
                       rowNumber++;
                       continue;
                   }

                   Iterator<Cell> cells = row.iterator();

                   int cid = 0;

                   Contact p = new Contact();

                   while (cells.hasNext()) {
                       Cell cell = cells.next();

                       switch (cid) {
                           case 0:
                               p.setFirstName(cell.getStringCellValue());
                               break;
                           case 1:
                               p.setLastName(cell.getStringCellValue());
                               break;
                           case 2:
                               p.setEmail(cell.getStringCellValue());
                               break;
                           case 3:
                              String phone = getCellValueAsString(row.getCell(cid));
                              p.setPhone(phone);
                               break;
                           default:
                               break;
                       }
                       cid++;

                   }

                   list.add(p);


               }
               Notification.show("Excel file processed successfully!");

        
       } catch (Exception e) {
           Notification.show("Error processing Excel file: " + e.getMessage());
           e.printStackTrace();
       }
       
      rid.setItems(list);
      this.contactService.save(list);
      updateList();
   }

  private void addlist() {
	
	  grid.setItems(contactService.findAll());
	}


 private String getCellValueAsString(Cell cell) {
	if (cell == null) {
        return "";
    }
    switch (cell.getCellType()) {
        case STRING:
            return cell.getStringCellValue();
        case NUMERIC:
                return BigDecimal.valueOf(cell.getNumericCellValue()).toPlainString(); // Convert numeric value to a plain string  
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
	
	
}

	