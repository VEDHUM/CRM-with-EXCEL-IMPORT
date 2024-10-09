package com.example.application.ui;

import java.util.Set;

import com.example.application.backend.entity.AuditTrail;
import com.example.application.backend.repository.AuditTrailRepository;
import com.example.application.backend.service.AuditTrailService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import jakarta.annotation.security.PermitAll;

@Route("audit-trail")
@PermitAll
public class AuditTrailView extends VerticalLayout {
	
	AuditTrail audittrail;

    AuditTrailRepository auditTrailRepository;
    AuditTrailService auditService;
    Grid<AuditTrail> grid = new Grid<>(AuditTrail.class);
    Button clearSelected = new Button();

    public AuditTrailView(AuditTrailRepository auditTrailRepository,  AuditTrailService auditService) {
    	
    	this.auditService = auditService;
    	this. auditTrailRepository = auditTrailRepository;	
       
         grid.addClassName("contact-grid");
       
         grid.setColumns("action","changeDate","changedBy","entityId","oldValue","newValue");
         grid.getColumns().forEach(col -> col.setAutoWidth(true));   
         grid.setItems(auditTrailRepository.findAll());
         
         grid.setSelectionMode(Grid.SelectionMode.MULTI);
         
         
         clearSelected = new Button("Clear Selected", e -> clearall());
        clearSelected.addClassName("delete-button");
        
        grid.asMultiSelect().addValueChangeListener(evnt -> editAudit(evnt.getValue()));
        
        setSizeFull();
        add(clearSelected,grid);
        clearSelected.setEnabled(false);
    }
    
    private void editAudit(Set<AuditTrail> audit) {
		
    	if(audit.isEmpty()) {
    		clearSelected.setEnabled(false);
    	}else {
    		clearSelected.setEnabled(true);
    	}
	}

	private void clearall() {
    	
    	Set<AuditTrail> audit = grid.getSelectedItems();
    	
    	if(audit.isEmpty()) {
    		Notification.show("Its null",3000, Notification.Position.MIDDLE);
    	}
    	 
    	Dialog dialog = new Dialog();
    	dialog.setHeaderTitle("Clear Selected Data ?");
    	Button yes = new Button("Confirm", e->{
    	audit.forEach(selectedcontact -> auditService.delete(selectedcontact));
    	grid.setItems(auditTrailRepository.findAll());
    	dialog.close();
    	 });
    	Button no = new Button("Cancel", e-> dialog.close());
    	
    	
    	dialog.add(yes,no);
    	dialog.open();	
  }
}