package com.org.kotlin.urlShortener.exception;
public class ErrorMessage {
	
	private String message;
	public ErrorMessage(String s) 
    { 
       message =s;
    }

	public String getMessage() {
		return message;
	}
}