package com.example.application.helper;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.application.backend.entity.Contact;

public class Helper {
	
	public static String[] HEADERS = {
			"ID",
			"FirstName",
			"LastName",
			"Email",
			"Phone",
	};
	
	public static String SHEET_NAME ="contact_data";
	
	public static ByteArrayInputStream dataToExcel(List<Contact> list) throws IOException {
		
		//create workbook
		Workbook workbook = new XSSFWorkbook();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {
					
			Sheet sheet = workbook.createSheet(SHEET_NAME);
			
			Row row = sheet.createRow(0);
			
			for(int i=0; i < HEADERS.length; i++) {
				Cell cell = row.createCell(i);
				cell.setCellValue(HEADERS[i]);
			}
			
			//VALUE ROWS
			
			int rowIndex = 1;
			for(Contact c: list){
				
				Row dataRow = sheet.createRow(rowIndex);
				rowIndex++;
				
				dataRow.createCell(0).setCellValue(c.getId());
				dataRow.createCell(1).setCellValue(c.getFirstName());
				dataRow.createCell(2).setCellValue(c.getLastName());
				dataRow.createCell(3).setCellValue(c.getEmail());
				dataRow.createCell(4).setCellValue(c.getPhone());
				
			}
			
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());	
		}
		
	  catch(IOException e) {
		e.printStackTrace();
		return null;
	  }
		finally{
			workbook.close();
			out.close();
			
		}

  }
}
