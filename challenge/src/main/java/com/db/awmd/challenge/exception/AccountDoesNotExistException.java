package com.db.awmd.challenge.exception;

public class AccountDoesNotExistException extends RuntimeException 
{
	private static final long serialVersionUID = 6819322128251361258L;

	public AccountDoesNotExistException(String message) 
	{
	    super(message);
	}
}
