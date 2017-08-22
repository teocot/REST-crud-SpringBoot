package com.teo.crud.data.service;

import java.util.Collection;
import com.teo.crud.data.model.Customer;

/**
 * CustomerService interface to provide crud functionality to clients
 * 
 * This decouples the clients of the service from the implementation.
 * 
 * In this application we will use the JPARepository, which is a spring boot implementation
 * of the CRUDRepository.
 * 
 * @author teodor cotruta
 *
 */
public interface CustomerService {

	/**
	 * Create a customer in the database
	 * 
	 * @param customer to be created
	 * @return The customer which has been created
	 * 			or null, if the record to create has an id
	 */
	Customer create(Customer customer);

	/**
	 * Reads or finds a customer from/in the database
	 * 
	 * @param id, which is a customer id to be found
	 * @return The customer entity record with the customerId = id 
	 * 			if there is no such customer, the method returns null
	 */
	Customer read(Long id);
	
	/**
	 * Update a customer record in the database.
	 * The default implementation in the repository is an upsert:
	 * this means that the record is created if it doesn't exists.
	 * What we want to do is avoid creating records and only update
	 * them when the records exist. Therefore we check if the record exists 
	 * and only then update the record. If the record doens't exist
	 * we throw a runtime exception
	 * 
	 * @param id, which is a customer id to be found
	 * @return The customer entity record with the customerId = id 
	 * 			if there is no such customer, the method returns null
	 * 
	 * @Exception customer not found
	 */
	Customer update(Customer customer);
	
	/**
	 * deletes a record if it exists
	 * 
	 * @param id is the customerId of the record to be deleted.
	 */
	void delete(Long id);

	/**
	 * finds all the customer in the database and returns a collection
	 * of entity records.
	 * 
	 * @return all customer in a Collection<Customer> 
	 */
	Collection<Customer> readAll();
	
	/**
	 * If the cache is enabled, each method call in this class 
	 * will cache the returned records.
	 * this will evict the cache.
	 * 
	 * @return void, there is no return from this method.
	 */
    void evictCache();

}
