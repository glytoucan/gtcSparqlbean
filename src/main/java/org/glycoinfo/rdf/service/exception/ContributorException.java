package org.glycoinfo.rdf.service.exception;

public class ContributorException extends Exception {

	/**
	 * 
	 */
	public ContributorException() {
	}

	/**
	 * @param message
	 */
	public ContributorException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public ContributorException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public ContributorException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * @param message
	 * @param cause
	 * @param enableSuppression
	 * @param writableStackTrace
	 */
	public ContributorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
