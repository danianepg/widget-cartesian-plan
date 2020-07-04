package com.danianepg.widget.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.danianepg.widget.exceptions.NotFoundException;
import com.danianepg.widget.exceptions.ValidationException;

/**
 * Intercepts all exceptions of the application and handle the status codes and
 * error messages.
 *
 * @author Daniane P. Gomes
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class GeneralExceptionHandler {

	private final Logger logger = LoggerFactory.getLogger(GeneralExceptionHandler.class);

	@ResponseBody
	@ExceptionHandler({ Exception.class, RuntimeException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleGeneric(final Exception ex) {
		this.logger.error("Exception occured: " + ex.getMessage());
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(NotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String handleNotFound(final NotFoundException ex) {
		this.logger.error("NotFoundException occured: " + ex.getMessage());
		return ex.getMessage();
	}

	@ResponseBody
	@ExceptionHandler(ValidationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public String handleNotFound(final ValidationException ex) {
		this.logger.error("ValidationException occured: " + ex.getMessage());
		return ex.getMessage();
	}

}
