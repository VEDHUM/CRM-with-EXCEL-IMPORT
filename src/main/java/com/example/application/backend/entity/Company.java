//package com.example.application.backend.entity;
//
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.OneToMany;
//import java.util.LinkedList;
//import java.util.List;
//
//@Entity
//public class Company extends AbstractEntity {
//    private String name;
//
//    @OneToMany(mappedBy = "company", fetch = FetchType.EAGER)
//    private List<Contact> employees = new LinkedList<>();
//
//    public Company() {
//    }
//
//    public Company(String name) {
//        setName(name);
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public List<Contact> getEmployees() {
//        return employees;
//    }
//
//	spring.datasource.url = jdbc:mysql://localhost:3306/persondb
//	spring.datasource.username = root
//	spring.datasource.password = abhi3011
//	
//	spring.jpa.show-sql = true
//	spring.jpa.generate-ddl = true
//	spring.jpa.hibernate.ddl-auto = update
//	spring.jpa.properties.hibernate.dialec =  org.hibernate.diaelct.MySQL8Dialect
//}