package info.kapable.utils.txttomail;

import info.kapable.utils.txttomail.exception.TemplateProcessingException;

/**
 * Template processor is a Gate to Email object class <br>
 * Email object store text file parameters and convert it <br>
 * 
 * @author Mathieu GOULIN
 *
 */
public abstract interface TemplateProcessor {
	/**
	 * Process method to launch conversion: prepare and send email
	 * 
	 * @throws TemplateProcessingException if some error during the process a TemplateProcessingException is throw
	 */
	public abstract void process() throws TemplateProcessingException;
}