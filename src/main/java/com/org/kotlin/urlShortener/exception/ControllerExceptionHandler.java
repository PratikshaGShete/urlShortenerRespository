package com.org.kotlin.urlShortener.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Component
public class ControllerExceptionHandler {
	
	private static final Logger logger = LoggerFactory.getLogger("Exception_Controller");

	@ExceptionHandler(InvalidParameterException.class)
	public ResponseEntity<ErrorMessage> handleInvalidParameterException(InvalidParameterException e) {
		ErrorMessage msg = new ErrorMessage(e.getMessage());
		logger.error("Invalid Url Exception : {} ", msg);
		logger.error("Exception stackTrace: {}", e.toString());
		return new ResponseEntity<ErrorMessage>(msg,HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(ObjectAlreadyPresentException.class)
	public ResponseEntity<ErrorMessage> objectAlreadyPresentException(ObjectAlreadyPresentException e) {
		ErrorMessage msg = new ErrorMessage(e.getMessage());
		logger.error("Url already present : {} ", msg);
		logger.error("Exception stackTrace: {}", e.toString());
		return new ResponseEntity<ErrorMessage>(msg,HttpStatus.CONFLICT);
	}

}
