package com.teo.crud.data.model;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The persistent class for the customer database table.
 * A customer has a foreign key to an address.
 * This implements a one to many relationship. 
 * The address can be also null, as not all customers have an address, 
 * or at least there is no address in the system.
 * 
 * Given the referential integrity on the address field, once an address
 * is set for a customer, that address cannot be deleted.
 * 
 * Another constraint implied from the referential integrity, is that addresses
 * have to be created before a customer address is created. 
 * If there are no addresses in the database, all the customers will have a null address.
 * 
 * The point to note is that I use
 * customerId instead of an 'id' field This is useful when we 
 * do sql queries to easier distinguish between column names in the results.
 * 
 * There is no requirement to add the Address entity to a persistence file
 * The framework scans all the directories and finds the @Entity annotations
 *
 * @Author Teodor Cotruta
 */
@Entity
@NamedQuery(name="Customer.findAll", query="SELECT c FROM Customer c")
public class Customer implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue //(strategy=GenerationType.TABLE)
	@Column(name="CUSTOMER_ID")
	private Long customerId;

	private String name;

	@Column(name="TELEPHONE_NUMBER")
	private String telephoneNumber;

	@ManyToOne
    @JoinColumn(name="ADDRESS_ID")
	private Address address;

	public Customer() {
	}

	public Long getCustomerId() {
		return this.customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephoneNumber() {
		return this.telephoneNumber;
	}

	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	public Address getAddress() {
		return this.address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

}