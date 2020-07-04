package com.danianepg.widget.exceptions;

/**
 * Exception thrown when errors occur while converting a
 * {@link com.danianepg.widget.entities.Widget} entity to a RESTful/HATEOS
 * format.
 *
 * @author Daniane P. Gomes
 *
 */
public class HateosMapperException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HateosMapperException() {
		super("Could not convert element!");
	}

}
