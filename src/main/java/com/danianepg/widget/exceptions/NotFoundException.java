package com.danianepg.widget.exceptions;

/**
 * Exception thrown when the a requested element is not found.
 * 
 * @author Daniane P. Gomes
 *
 */
public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NotFoundException() {
		super("Could not find requested element!");
	}

}
