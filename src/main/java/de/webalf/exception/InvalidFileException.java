package de.webalf.exception;

/**
 * @author Alf
 * @since 15.11.2022
 */
public class InvalidFileException extends RuntimeException {
	public InvalidFileException(String title) {
		super(title);
	}

	public InvalidFileException(String title, Throwable cause) {
		super(title, cause);
	}
}
