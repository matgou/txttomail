package info.kapable.utils.txttomail;

import static org.junit.Assert.*;
import info.kapable.utils.txttomail.domain.Email;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;

import org.junit.Test;

/**
 * Test global functionality without sending the email
 * 
 * @author MGOULIN
 *
 */
public class GeneralTxtToMailTest {

	@Test
	public void testMain() {
		String[] args = {"-i", "src/main/resources/mail.exemple.txt"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(args);
		Email email = Email.getEmail();
		assertTrue(email.getMessage() != null);
		try {
			// Mail must containt something
			assertTrue(email.getMessage().getContent() != null);

			Multipart multipart = (Multipart) email.getMessage().getContent();
			// With Attachemnet multipart count must more than 2
			assertTrue(multipart.getCount() > 2);
		} catch (IOException e) {
			assertTrue(false);
		} catch (MessagingException e) {
			assertTrue(false);
		}
	}

}
