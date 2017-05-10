package info.kapable.utils.txttomail.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception for handle all error during processing <br>
 * When this Exception is throw the process stop
 * 
 * @author Mathieu GOULIN
 */
public class TemplateProcessingException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(TemplateProcessingException.class);
	
	/**
	 * Construct exception from other Exception
	 * @param e the other exception
	 */
	public TemplateProcessingException(Exception e) {
		super(e.getMessage());
		logger.error(e.getMessage(), e);
	}

	/**
	 * Construct exception from an error message
	 * @param message the message to display
	 */
	public TemplateProcessingException(String message) {
		super(message);
		logger.error(message, this);
	}
}
