package com.teo.crud.data.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.teo.crud.data.model.Address;
import com.teo.crud.data.repository.AddressRepository;
import javax.persistence.EntityExistsException;
import javax.persistence.NoResultException;

import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AddressService implementation to provide crud functionality to clients
 * 
 * In this application we will use the JPARepository, which is a spring boot implementation
 * of the CRUDRepository.
 * this class uses an AddressRepository for Address entity with a key of type Long
 * 
 * @author teodor cotruta
 *
 */
@Service
public class AddressServiceImpl implements AddressService {

	private static final Logger logger = LoggerFactory.getLogger(AddressServiceImpl.class);

	@Autowired
	private AddressRepository addressRepository;

	/**
	 * readAll finds all the address records in the database and returns a collection
	 * of entity records.
	 * 
	 * @return all addresses in a Collection<Address> 
	 */
	@Override
	@Transactional(readOnly = true)
	public Collection<Address> readAll() {
		logger.info("Start readAll");

		Collection<Address> addresses = addressRepository.findAll();

		logger.info("End readAll");
		return addresses;
	}

	/**
	 * Reads or finds a address from/in the database
	 * 
	 * @param id, which is a address id to be found
	 * @return The address entity record with the addressId = id 
	 * 			if there is no such address, the method returns null
	 */
	@Override
	@Cacheable(value = "addresses", key = "#addressId")
	@Transactional(readOnly = true)
	public Address read(Long addressId) {
		logger.info("Start read addressId:{}", addressId);

		Address address = addressRepository.findOne(addressId);

		logger.info("End read id:{}", addressId);
		return address;
	}

	/**
	 * Create a address in the database
	 * 
	 * @param address to be created
	 * @return The address which has been created
	 * 			or null, if the record to create has an id
	 */
	@Override
	@Transactional(readOnly = false)
	@CachePut(value = "addresses", key = "#result.addressId")
	public Address create(Address address) {
		logger.info("Start create");

		// Ensure the entity object to be created does NOT exist in the
		// repository. Prevent the default behavior of save() which will update
		// an existing entity if the entity matching the supplied id exists.
		if (address.getAddressId() != null) {
			// Cannot create Address with specified ID value
			logger.error("Attempted to create a Address, but id attribute was not null.");
			throw new EntityExistsException("The id attribute must be null to persist a new entity.");
		}

		Address savedAddress = addressRepository.save(address);

		logger.info("End create");
		return savedAddress;
	}


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
    @Override
    @Transactional(readOnly = false)
    @CachePut(value = "addresses", key = "#address.addressId")
    public Address update(Address address) {
        logger.info("Start update id:{}", address.getAddressId());

        // Ensure the entity object to be updated exists in the repository to
        // prevent the default behavior of save() which will persist a new
        // entity if the entity matching the id does not exist
        Address addressToUpdate = read(address.getAddressId());
        if (addressToUpdate == null) {
            // Cannot update Address that hasn't been persisted
            logger.error(
                    "Attempted to update an Address, but the entity does not exist.");
            throw new NoResultException("Requested entity not found.");
        }

        Address updatedAddress = addressRepository.save(address);

        logger.info("End update id:{}", address.getAddressId());
        return updatedAddress;
    }

	/**
	 * deletes a record if it exists
	 * 
	 * @param id is the addressId of the record to be deleted.
	 */
    @Override
    @Transactional(readOnly = false)
    @CacheEvict(value = "addresses", key = "#addressId")
    public void delete(Long addressId) {
        logger.info("Start delete id:{}", addressId);

        addressRepository.delete(addressId);

        logger.info("End delete id:{}", addressId);
    }

    @Override
    @CacheEvict(value = "addresses",allEntries = true)
    public void evictCache() {
        logger.info("Start evictCache");
        logger.info("End evictCache");
    }

}
