package com.teo.crud.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.teo.crud.data.model.Address;

/**
 * JpaRepository implements all the crud from CRUDRepository
 * 
 * This is based on spring boot framework.
 * 
 * NOTE the Address and Long types in the template (metatype) of the JpaRepository
 * which say that the managed entity is an Address and the primary key is of type Long
 * With these 2 types, the JpaRepository builds all the required queries to CRUD the 
 * records in the database.
 * 
 * There is no requirement to add the Address entity to a persistence file
 * The framework scans all the directories and finds the @Entity annotations
 * 
 * @author teodor cotruta
 *
 */
public interface AddressRepository extends JpaRepository<Address, Long>{
}
