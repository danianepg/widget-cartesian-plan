package com.danianepg.widget.exceptions;

/**
 * Exception thrown when the fields on an entity are not correctly filled.
 * 
 * @author Daniane P. Gomes
 *
 */
public class ValidationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ValidationException(final String message) {
		super(message);
	}

}
