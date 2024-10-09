package com.example.application.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import com.example.application.backend.service.ContactService;
import com.example.application.login.LoginView;

@RestController
@RequestMapping("/contact")

public class ContactController {
	
	@Autowired
	private ContactService service ;
	
	@RequestMapping("/excel")
	public ResponseEntity<Resource> download() throws IOException{
		
		String filename = "contact.xlsx";
		
		ByteArrayInputStream actualData = service.getActualData();
		InputStreamResource file = new InputStreamResource(actualData);
		
		ResponseEntity<Resource> body = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename ="+filename).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		
		return body;
	}
	
	@RequestMapping("/templete")
	public ResponseEntity<Resource> downloadtemplate() throws IOException{
		
		String filename = "templete.xlsx";
		
		ByteArrayInputStream templete = service.getTemplete();
		InputStreamResource file = new InputStreamResource(templete);
		
		ResponseEntity<Resource> body = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename ="+filename).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		
		return body;
	}
	
	@RequestMapping("/pdf")
	public ResponseEntity<Resource> downloadpdf() throws IOException{
		
		String filename = "contact.pdf";
		
		ByteArrayInputStream actualData = service.getActualData();
		InputStreamResource file = new InputStreamResource(actualData);
		
		ResponseEntity<Resource> body = ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename ="+filename).contentType(MediaType.parseMediaType("application/vnd.ms-excel")).body(file);
		
		return body;
	}
	
	
	
//	@GetMapping("/admin")
//	public ResponseEntity<Class> adminUser(){
//		return ResponseEntity.ok(MainLayout.class);
//	}
	
	@GetMapping("/Adminn")
	public RedirectView adminUser() {
		return new RedirectView("/admin");
	}
	
//	@GetMapping("/normal")
//	public ResponseEntity<Class> normalUser(){
//		return ResponseEntity.ok(LoginView.class);
//	}

}
