package com.org.kotlin.urlShortener.exception;

public class ObjectAlreadyPresentException extends RuntimeException {
	public ObjectAlreadyPresentException(String s) {
		super(s);
	}
}
