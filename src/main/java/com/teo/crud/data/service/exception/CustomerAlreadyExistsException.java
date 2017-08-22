package com.teo.crud.data.service.exception;

/**
 * CustomerAlreadyExistsException to be used for clients
 * which try to create a duplicate customer record in the database.
 * 
 * @author teodor cotruta
 *
 */
public class CustomerAlreadyExistsException extends RuntimeException{

	/**
	 * This class is serializable, therefore reuires a serial verions
	 */
	private static final long serialVersionUID = 3588320910665407894L;

	/**
	 * Runtime exception to be used when a record already exists in the 
	 * database and a client wants to create a new one, with the same id 
	 * or identical.
	 * 
	 * @param message to be displayed when calling getMessage()
	 */
	public CustomerAlreadyExistsException(String message) {
		super("Customer already exists");
	}

}
