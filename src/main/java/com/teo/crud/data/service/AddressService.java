package com.teo.crud.data.service;

import java.util.Collection;

import com.teo.crud.data.model.Address;

/**
 * AddressService interface to provide crud functionality to clients
 * 
 * This decouples the clients of the service from the implementation.
 * 
 * In this application we will use the JPARepository, which is a spring boot implementation
 * of the CRUDRepository.
 * 
 * @author teodor cotruta
 *
 */
public interface AddressService {
	/**
	 * Create a address in the database
	 * 
	 * @param address to be created
	 * @return The address which has been created
	 * 			or null, if the record to create has an id
	 */
	Address create(Address address);

	/**
	 * Reads or finds a address from/in the database
	 * 
	 * @param id, which is a address id to be found
	 * @return The address entity record with the addressId = id 
	 * 			if there is no such address, the method returns null
	 */
	Address read(Long id);
	
	/**
	 * Update a address record in the database.
	 * The default implementation in the repository is an upsert:
	 * this means that the record is created if it doesn't exists.
	 * What we want to do is avoid creating records and only update
	 * them when the records exist. Therefore we check if the record exists 
	 * and only then update the record. If the record doens't exist
	 * we throw a runtime exception
	 * 
	 * @param id, which is a address id to be found
	 * @return The address entity record with the addressId = id 
	 * 			if there is no such address, the method returns null
	 * 
	 * @Exception address not found
	 */
	Address update(Address address);
	
	/**
	 * deletes a record if it exists
	 * 
	 * @param id is the addressId of the record to be deleted.
	 */
	void delete(Long id);

	/**
	 * readAll finds all the address records in the database and returns a collection
	 * of entity records.
	 * 
	 * @return all addresses in a Collection<Address> 
	 */
	Collection<Address> readAll();
	
	/**
	 * If the cache is enabled, each method call in this class 
	 * will cache the returned records.
	 * this will evict the cache.
	 * 
	 * @return void, there is no return from this method.
	 */
    void evictCache();

}
