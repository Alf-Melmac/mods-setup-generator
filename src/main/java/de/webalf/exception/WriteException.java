package de.webalf.exception;

/**
 * @author Alf
 * @since 16.11.2022
 */
public class WriteException extends RuntimeException {
	public WriteException(String message, Throwable cause) {
		super(message, cause);
	}
}
