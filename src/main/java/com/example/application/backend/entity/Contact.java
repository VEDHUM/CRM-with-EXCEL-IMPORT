package com.example.application.backend.entity;

	import java.util.List;

import com.vaadin.flow.component.html.Span;

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
		
		public enum Status{
			ImportedLead, NotContacted, Contacted, Customer,ClosedLost
			
		}

	    @NotEmpty
	    private String firstName = "";

	    @NotEmpty
	    private String lastName = "";
	    
	    @Email
	    @NotEmpty
	    private String email = "";
	    
	    @NotEmpty
	    private String phone; 
	    
	    @Enumerated(EnumType.STRING)
	    private Contact.Status status;
	    
	    public String getStatts() {
			return statts;
		}

		public void setStatts(String statts) {
			this.statts = statts;
		}

		private String statts;
		
		private String fault;
		
		private Span status1;



		public Span getStatus1() {
			return status1;
		}

		public void setStatus1(Span status1) {
			this.status1 = status1;
		}

		public String getFault() {
			return fault;
		}

		public void setFault(String fault) {
			this.fault = fault;
		}

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

		public Contact.Status getStatus() {
			return status;
		}

		public void setStatus(Contact.Status status) {
			this.status = status;
		}
	}


