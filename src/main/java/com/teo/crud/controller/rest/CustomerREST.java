package com.teo.crud.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.teo.crud.data.model.Address;
import com.teo.crud.data.model.Customer;
import com.teo.crud.data.service.AddressService;
import com.teo.crud.data.service.CustomerService;
import com.teo.crud.data.service.exception.CustomerAlreadyExistsException;
import javax.validation.Valid;
import java.util.Collection;

/**
 * This class provides a rest controller for the REST services 
 * in order to manage customer records.
 * 
 * The main paths are:
 * "/customer" to read all the customer records using a GET method
 * "/customer/{id}" to read one record using a GET
 * "/customer" to create a new customer record using POST
 * "/customer/{customerId}/address/{addressId}" to PUT update the address of a customer
 * "/customer/{id}" to update a record using PUT. 
 * "/customer/{id}" to delete a record with DELETE
 * 
 * @author teodor cotruta
 */
@RestController
public class CustomerREST {

	private static final Logger logger = LoggerFactory.getLogger(CustomerREST.class);
	/**
	 * The CustomerService service.
	 */
	@Autowired
	private CustomerService customerService;

	@Autowired
	private AddressService addressService;

    /**
     * Read all customer with a GET
     * 
	 * Example:
	 * shell> curl localhost:8080/customer
     *
     * @return a json array of customers and http status 200
     * 			This can be empty, if there is no customer in the database. 
     */
	@RequestMapping(value = "/customer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<Customer>> getCustomers() {
		logger.info("Start getCustomers");

		Collection<Customer> customers = customerService.readAll();

		logger.info("End getCustomers");
		return new ResponseEntity<Collection<Customer>>(customers, HttpStatus.OK);
	}

    /**
     * Searches a customer having the customerId = id 
     *
	 * Example searching for a customer with customerId = 1:
	 * shell> curl localhost:8080/customer/1
	 * 
     * @param id for the address to GET
     * 
     * @return a json representation of the customer found and HTTP status 200
     * 			or empty, if the customer is not found, and HTTP status 404
     */
	@RequestMapping(value = "/customer/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Customer> getCustomer(@PathVariable("id") Long id) {
		logger.info("Start getCustomer id:{}", id);

		Customer customer = customerService.read(id);
		if (customer == null) {
			return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
		}

		logger.info("End getCustomer id:{}", id);
		return new ResponseEntity<Customer>(customer, HttpStatus.OK);
	}

    /**
     * Creates a new Customer
     * 
     * In order to invoke this service, POST a json representation of a customer 
     * at "<server>:<port>/customer" 
     * The service will persist the new customer in the database.
     * There is no check to see if the exact same customer already exists in the database.
     * TODO: check if the customer already exists in the database and throw an exception in that case.
     * 
	 * Example command and return values:
	 * shell> curl -H "Content-Type: application/json" -X POST -d "{  \"name\": \"teo\" , \"telephoneNumber\" : \"123\" ,\"address\" :{ \"addressId\": \"2\",  \"suburb\":\"TAKAPUNA\", \"city\": \"AUCKLAND\" , \"country\" : \"NEW ZEALAND\" , \"streetNumber\" : \"5\", \"streetName\":\"SINGLEVIEW\", \"streetType\" : \"street\"}}" http://localhost:8080/customer
     * shell>  {"customerId":8,"name":"teo","telephoneNumber":"123","address":{"addressId":2,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"SINGLEVIEW","streetNumber":"5","streetType":"STREET","suburb":"TAKAPUNA"}}
     * 
	 * NOTES: - the escapes may not be required on UNIX
	 * 		  - the address linked to the new customer will be the address with the addressId in the JSON 
	 * 			POSTed. There will be no new address created. If there is no address with that addressID, 
	 * 			a null value will be inserted in the customer record.
	 * 		  - in order to change the address of a customer, use the /customer/{customerId}/address/{addressId} path
	 * 		  - The customerId is a generated value and should not be passed in the request json.
	 * 
     * @param Customer to be created
     *       
     * @return a json representation of the customer created and HTTP status 201
     * 			or an error, exception from the service layer, 
     * 			if the customer can't be created. Usually this is becasue a customerId 
     * 			was provided in the request. Being a generated value, it should not be provided.
     */
	@RequestMapping(value = "/customer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
		logger.info("Start createCustomer");

		// we check and fetch the proper address data, by id
		// client may've entered the right id but wrong data.
		// if wrong id, address is set to null
		Address customerAddress = customer.getAddress();
		if (customerAddress != null) {
			customerAddress = addressService.read(customerAddress.getAddressId());
			customer.setAddress(customerAddress);
		}
		Customer createdCustomer = customerService.create(customer);
		logger.info("End createCustomer: {}", createdCustomer);
		return new ResponseEntity<Customer>(createdCustomer, HttpStatus.CREATED);
	}

    /**
     * Update a customer by sending a json string to 
     * "/customer" using a PUT method
     * 
     * Example updating a customer with customerId=8:
	 * shell> curl -H "Content-Type: application/json" -X PUT -d "{  \"customerId\": 8,\"name\": \"teo\" , \"telephoneNumber\" : \"123\" ,\"address\" :{ \"addressId\": \"2\",  \"suburb\":\"TAKAPUNA\", \"city\": \"AUCKLAND\" , \"country\" : \"NEW ZEALAND\" , \"streetNumber\" : \"5\", \"streetName\":\"SINGLEVIEW\", \"streetType\" : \"street\"}}" http://localhost:8080/customer
     * shell> {"customerId":8,"name":"teodor cotruta","telephoneNumber":"123","address":{"addressId":2,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"SINGLEVIEW","streetNumber":"5","streetType":"STREET","suburb":"TAKAPUNA"}}
     * 
     * Notes: - escapes may not be required on UNIX or is you use Postman or SoapUI.
     *        - this is similar with a create, however the http method is a PUT and the
     *        	input has a customerId value.
     *        
     * @param customer to be updated
     * @return the updated customer 
     */
	@RequestMapping(value = "/customer", method = RequestMethod.PUT)
	public ResponseEntity<Customer> updateCustomer(@RequestBody @Valid final Customer customer) {
		logger.info("Start updateCustomer id:{}", customer.getCustomerId());

		Customer updatedCustomer = customerService.update(customer);
		if (updatedCustomer == null) {
			return new ResponseEntity<Customer>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		logger.info("End updateCustomer");
		return new ResponseEntity<Customer>(updatedCustomer, HttpStatus.OK);
	}

	/**
	 * Update the customer's address using a PUT 
	 * This uses the path: "/customer/{customerId}/address/{addressId}"
	 * 
	 * How it works: 
	 * 	-This method will link a customer with customerId to an address with addressId.
	 * 	-If the customerId doens't exist, nothing happens
	 * 	-If the addressId doens't exist, the customer's address will be set to null.
	 * 	-If both ids are valid, the customer's address is set to the address with addressId.
	 * 
	 * Example: 
	 * shell> curl -X PUT http://localhost:8080/customer/5/address/1
	 * shell> {"customerId":5,"name":"teo","telephoneNumber":"123","address":{"addressId":1,"city":"AUCKLAND","country":"NEW ZEALAND","streetName":"BIG STREET","streetNumber":"2/34","streetType":"AVENUE","suburb":"NEW MARKET"}}

	 * @param customerId of the customer to be updated
	 * @param addressId to be linked to the customer
	 * 
	 * @return the updated customer in json format or
	 * 			empty string if the customer id is invalid.
	 */
	@RequestMapping(value = "/customer/{customerId}/address/{addressId}", method = RequestMethod.PUT)
	public ResponseEntity<Customer> updateCustomerAddress(@PathVariable("customerId") Long customerId,
			@PathVariable("addressId") Long addressId) {
		logger.info("Start updateCustomer id:{} with address id: {}", customerId, addressId);

		Customer customer = customerService.read(customerId);
		if (customer == null) {
			logger.info("No customer for id: " + customerId);
			return new ResponseEntity<Customer>(HttpStatus.INTERNAL_SERVER_ERROR);
		}

		Address address = addressService.read(addressId);
		customer.setAddress(address);

		Customer updatedCustomer = customerService.update(customer);

		logger.info("End updateCustomerAddress");
		return new ResponseEntity<Customer>(updatedCustomer, HttpStatus.OK);
	}

	/**
	 * Delete a customer
	 * 
	 * Request method is DELETE
	 * 
	 * Example usage of DELETE and GET, just after DELETE:
	 * -- start here
	 * shell>curl -X DELETE localhost:8080/customer/1
	 * 
	 * shell>curl localhost:8080/customer/1
	 *
	 * shell>
	 * -- end here
	 * 
	 * NOTE: Above we can see there is no longer a record returned with the GET after DELETE
	 * 
	 * @param id of the customer to be deleted. 
	 * @return http status: 204, no content
	 * 
	 */
	@RequestMapping(value = "/customer/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Customer> deleteCustomer(@PathVariable("id") Long id) {
		logger.info("Start deleteCustomer id:{}", id);

		customerService.delete(id);

		logger.info("End deleteCustomer id:{}", id);
		return new ResponseEntity<Customer>(HttpStatus.NO_CONTENT);
	}

	/**
	 * TODO: exception, have to compare customers
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public String handleCustomerAlreadyExistsException(CustomerAlreadyExistsException e) {
		return e.getMessage();
	}

}
