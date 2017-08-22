package com.teo.crud.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.teo.crud.data.model.Customer;
import com.teo.crud.data.repository.CustomerRepository;

import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CustomerService implementation to provide crud functionality to clients
 * 
 * In this application we will use the JPARepository, which is a spring boot implementation
 * of the CRUDRepository.
 * this class uses a CustomerRepository for Customer entity with a key of type Long
 * 
 * @author Teodor Cotruta
 *
 */
@Service
@EnableTransactionManagement 
public class CustomerServiceImpl implements CustomerService {

	private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class);

	private final CustomerRepository customerRepository;

	@Autowired
	public CustomerServiceImpl(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

	/**
	 * finds all the customer in the database and returns a collection
	 * of entity records.
	 * 
	 * @return all customer in a Collection<Customer> 
	 */
  	@Override
	public Collection<Customer> readAll() {
		logger.info("Start readAll");

		Collection<Customer> customers = customerRepository.findAll();

		logger.info("End readAll");
		return customers;
	}

	/**
	 * Reads or finds a customer from/in the database
	 * 
	 * @param id, which is a customer id to be found
	 * @return The customer entity record with the customerId = id 
	 * 			if there is no such customer, the method returns null
	 */
	@Override
	@Cacheable("customers")
	public Customer read(Long customerId) {
		logger.info("Start read customerId:{}", customerId);

		Customer address = customerRepository.findOne(customerId);

		logger.info("End read id:{}", customerId);
		return address;
	}

	/**
	 * Create a customer in the database
	 * 
	 * @param customer to be created
	 * @return The customer which has been created
	 * 			or null, if the record to create has an id
	 */
	@Override
	@Transactional (readOnly = false)
	@CachePut(value = "customers", key = "#result.customerId")
	public Customer create(Customer customer) {
		logger.info("Start create");

		// Ensure the entity object to be created does NOT exist in the
		// repository. Prevent the default behavior of save() which will update
		// an existing entity if the entity matching the supplied id exists.
		if (customer.getCustomerId() != null) {
			// Cannot create Customer with specified ID value
			logger.error("Attempted to create a Customer, but id attribute was not null.");
			throw new EntityExistsException("The id attribute must be null to persist a new entity.");
		}

		Customer savedCustomer = customerRepository.saveAndFlush(customer);

		logger.info("End create");
		return savedCustomer;
	}


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
    @Override
    @Transactional(readOnly = false)
    @CachePut(value = "customers", key = "#customer.customerId")
    public Customer update(Customer customer) {
        logger.info("Start update customerId: {}", customer.getCustomerId());

        // The default save() does an "upsert", insert if it doens't exist or update
        // if the record exists.
        // To enable clean create without updates, we check if the customer already exists
        // Therefore we don't create new customers
        Customer customerToUpdate = read(customer.getCustomerId());
        if (customerToUpdate == null) {
            // Cannot update Customer that hasn't been persisted
            logger.error("Attempted to update a Customer, but the entity does not exist.");
            throw new NoResultException("Requested entity not found.");
        }

        Customer updatedCustomer = customerRepository.save(customer);

        logger.info("End update id:{}", updatedCustomer.getCustomerId());
        return updatedCustomer;
    }

	/**
	 * deletes a record if it exists
	 * 
	 * @param id is the customerId of the record to be deleted.
	 */
    @Override
    @Transactional(readOnly = false)
    @CacheEvict(value = "customers", key = "#customerId")
    public void delete(Long customerId) {
        logger.info("Start delete customerId:{}", customerId);

        customerRepository.delete(customerId);

        logger.info("End delete id:{}", customerId);
    }

	/**
	 * If the cache is enabled, each method call in this class 
	 * will cache the returned records.
	 * this will evict the cache.
	 * 
	 * @return void, there is no return from this method.
	 */
    @Override
    @CacheEvict(value = "customers", allEntries = true)
    public void evictCache() {
        logger.info("Start evictCache");
        logger.info("End evictCache");
    }

}
