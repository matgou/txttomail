package info.kapable.utils.txttomail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import info.kapable.utils.txttomail.domain.Email;
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
	 * @param email the email to send
	 * 
	 * @throws TemplateProcessingException if some error during the process a TemplateProcessingException is throw
	 * @throws MessagingException when some error in javax.mail api 
	 * @throws IOException when some error during reading
	 */
	public abstract void send(Email email) throws TemplateProcessingException, IOException, MessagingException;

	public abstract MimeMessage getMessage();

	public abstract void saveToInput(Email email) throws TemplateProcessingException;

	public abstract Email loadEmailFromInput() throws TemplateProcessingException;
}