package com.teo.crud.controller.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.teo.crud.data.model.Address;
import com.teo.crud.data.service.AddressService;
import com.teo.crud.data.service.exception.AddressAlreadyExistsException;

import javax.validation.Valid;

import java.util.Collection;

/**
 * This class provides a rest controller for the REST services 
 * to manipulate address records.
 * The main paths are:
 * 
 * "/address" to read all the records using a GET
 * "/address/{id}" to read one record using a GET
 * "/address" to create a record using POST
 * "/address/{id}" to update the records using PUT
 * "/address/{id}" to delete a record with DELETE
 * 
 * @author teodor cotruta
 */
@RestController
public class AddressREST {

    private static final Logger logger = LoggerFactory.getLogger(AddressREST.class);
    
    /**
     * The AddressService service.
     */
    @Autowired
    private AddressService addressService;

    /**
     * Read all addresses with a GET
     * 
	 * Example:
	 * shell> curl localhost:8080/address
     *
     * @return a json array of all addresses in the database. 
     * 			This can be empty, if there is no address in the database.
     */
    @RequestMapping(
    		value = "/address",
    		method = RequestMethod.GET, 
    		produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Address>> getAddresses() {
        logger.info("Start getAddresses");

        Collection<Address> addresses = addressService.readAll();

        logger.info("End getAddresses");
        return new ResponseEntity<Collection<Address>>(addresses,HttpStatus.OK);
    }

    /**
     * Searches an address having the addressId = id 
	 * Example searching for address with addressId = 1:
	 * shell> curl localhost:8080/address/1
	 * 
     * @param id for the address to GET
     * 
     * @return a json representation of the address found and HTTP status 200
     * 			or empty if the address is not found and HTTP status 404
     */
    @RequestMapping(
            value = "/address/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Address> getAddress(@PathVariable("id") Long id) {
        logger.info("Start getAddress id:{}", id);

        Address address = addressService.read(id);
        if (address == null) {
            return new ResponseEntity<Address>(HttpStatus.NOT_FOUND);
        }

        logger.info("End getAddress id:{}", id);
        return new ResponseEntity<Address>(address, HttpStatus.OK);
    }

    /**
     * Creates an address
     * 
     * In order to invoke this service, POST a json representation of an address 
     * at "<server>:<port>/address" 
     * The service will persist the new address in the database.
     * There is no check to see if the exact same address already exists in the database.
     * 
	 * Example:
	 * shell> curl -H "Content-Type: application/json" -X POST -d "{ \"streetName\" : \"Lake\" , \"city\": \"Takapuna\" , \"country\" : \"New Zealand\" , \"streetNumber\" : \"33a\", \"streetType\" : \"street\"}" http://localhost:8080/address
	 * NOTE: the escapes may not be required on UNIX
	 * The above command will return the json representation of the new address, like:
	 * {"addressId":6,"city":"Takapuna","country":"New Zealand","streetName":"Lake","streetNumber":"33a","streetType":"street","suburb":null}
	 * The addressId is a generated value.
	 * 
     * @param Address to be created
     *       
     * @return a json representation of the address created and HTTP status 201
     * 			or an error, exception from the service layer, 
     * 			if the address can't be created
     */
    @RequestMapping(
            value = "/address",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Address> createAddress(
            @RequestBody Address address) {
        logger.info("Start createAddress");

        Address createdAddress = addressService.create(address);

        logger.info("End createAddress");
        return new ResponseEntity<Address>(createdAddress, HttpStatus.CREATED);
    }

    /**
     * Update an address by sending a json string to 
     * "/address" using a PUT method
     * 
     * Example updating an address with addressId=6:
	 * shell> curl -H "Content-Type: application/json" -X PUT -d "{ \"addressId\":6, \"streetName\" : \"Long Street\" , \"city\": \"Takapuna\" , \"country\" : \"New Zealand\" , \"streetNumber\" : \"33a\", \"streetType\" : \"street\"}" http://localhost:8080/address
	 * which returns the updated address:
	 * shell> {"addressId":6,"city":"Takapuna","country":"New Zealand","streetName":"Long Street","streetNumber":"33a","streetType":"street","suburb":null}
     * Note: escapes may not be required on UNIX or is you use Postman or SoapUI.
     * 
     * @param address
     * @return
     */
    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public ResponseEntity<Address> updateAddress(@RequestBody @Valid final Address address) {
        logger.info("Start updateAddress id:{}", address.getAddressId());

        Address updatedAddress = addressService.update(address);
        if (updatedAddress == null) {
            return new ResponseEntity<Address>(
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("End updateAddress id:{}", address.getAddressId());
        return new ResponseEntity<Address>(updatedAddress, HttpStatus.OK);
    }

    /**
     * Delete an address
     * 
     * In order to delete an address use HTTP DELETE on /address/<addressId>
     * Example:
     * shell> curl -X DELETE localhost:8080/address/6
     * 
     * shell> 
     * after running the above, if we try to read the address we get an empty string.
     * 
     * @param id of the address to be deleted.
     * @return an empty string, if successful or a json containing the error, if unsuccessful 
     * @exception if the address is referenced by a customer record, the address can't be deleted
     * 				in that case the HTTP status is 500 as follows:
     * 				{"timestamp":1473036020529,"status":500,
     * 				"error":"Internal Server Error","exception":"org.springframework.dao.DataIntegrityViolationException","message":"could not execute statement; SQL [n/a]; constraint [FK_ADDRESS]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement","path":"/address/2"}
     */
    @RequestMapping(
            value = "/address/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Address> deleteAddress(
            @PathVariable("id") Long id) {
        logger.info("Start deleteAddress id:{}", id);

        addressService.delete(id);

        logger.info("End deleteAddress id:{}", id);
        return new ResponseEntity<Address>(HttpStatus.NO_CONTENT);
    }


    /**
     * When we try to create an address, which already exists in the database
     * we return the error message with status 409
     * 
     * TODO: create a named query searching for a record matching 
     * 		all the attributes of the new entity to persist. 
     * 		That would be in the jpa layer

     * @param AddressAlreadyExistsException runtime exception
     * @return a String with the error message
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAddressAlreadyExistsException(AddressAlreadyExistsException e) {
        return e.getMessage();
    }

}
