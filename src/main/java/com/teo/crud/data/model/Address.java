package com.teo.crud.data.model;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the address database table.
 * This is a simple entity. The point to note is that I use
 * addressId instead of an 'id' field This is useful when we 
 * do sql queries to easier distinguish between column names in the results.
 * 
 * There is no requirement to add the Address entity to a persistence file
 * The framework scans all the directories and finds the @Entity annotations
 * 
 * @Author Teodor Cotruta
 */
@Entity
@NamedQuery(name="Address.findAll", query="SELECT a FROM Address a")
public class Address implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue 
	@Column(name="ADDRESS_ID")
	private Long addressId;

	private String city;

	private String country;

	@Column(name="STREET_NAME")
	private String streetName;

	@Column(name="STREET_NUMBER")
	private String streetNumber;

	@Column(name="STREET_TYPE")
	private String streetType;

	private String suburb;

	public Address() {
	}

	public Long getAddressId() {
		return this.addressId;
	}

	public void setAddressId(Long addressId) {
		this.addressId = addressId;
	}

	public String getCity() {
		return this.city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return this.country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getStreetName() {
		return this.streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getStreetNumber() {
		return this.streetNumber;
	}

	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}

	public String getStreetType() {
		return this.streetType;
	}

	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}

	public String getSuburb() {
		return this.suburb;
	}

	public void setSuburb(String suburb) {
		this.suburb = suburb;
	}

}