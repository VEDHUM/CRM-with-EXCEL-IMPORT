package com.example.application.backend.entity;

	import java.util.List;

	import jakarta.persistence.Entity;
	import jakarta.persistence.EnumType;
	import jakarta.persistence.Enumerated;
	import jakarta.persistence.JoinColumn;
	import jakarta.persistence.ManyToOne;
	import jakarta.validation.constraints.Email;
	import jakarta.validation.constraints.NotEmpty;
	import jakarta.validation.constraints.NotNull;

	@Entity
	public class Contact extends AbstractEntity implements Cloneable {

	 
	    private String firstName = "";

	  
	    private String lastName = "";
	  
	    private String email = "";
	    
	    private String phone; 

		public String getPhone() {
			return phone;
		}

		public void setPhone(String phone) {
			this.phone = phone;
		}

		public String getEmail() {
	        return email;
	    }

	    public void setEmail(String email) {
	        this.email = email;
	    }

	    public String getLastName() {
	        return lastName;
	    }

	    public void setLastName(String lastName) {
	        this.lastName = lastName;
	    }

	    public String getFirstName() {
	        return firstName;
	    }

	    public void setFirstName(String firstName) {
	        this.firstName = firstName;
	    }


	    @Override
	    public String toString() {
	        return firstName + " " + lastName;
	    }
	}


