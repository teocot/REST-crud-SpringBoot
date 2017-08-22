package com.teo.crud.data.service.exception;

/**
 * AddressAlreadyExistsException to be used for clients
 * which try to create a duplicate address record in the database.
 * 
 * @author teodor cotruta
 *
 */
public class AddressAlreadyExistsException extends RuntimeException{

	/**
	 * This class is serializable, therefore reuires a serial verions
	 */
	private static final long serialVersionUID = -8549088422520488039L;

	/**
	 * Runtime exception to be used when a record already exists in the 
	 * database and a client wants to create a new one, with the same id 
	 * or identical.
	 * 
	 * @param message to be displayed when calling getMessage()
	 */
	public AddressAlreadyExistsException(String format) {
		super("Address already exists");
	}

}
