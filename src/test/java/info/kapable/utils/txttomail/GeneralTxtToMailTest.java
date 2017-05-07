package info.kapable.utils.txttomail;

import static org.junit.Assert.*;
import info.kapable.utils.txttomail.domain.Email;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;

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
		MimeMessage email = info.kapable.utils.txttomail.TxtToMail.getMimeMessage();
		assertTrue(email != null);
		try {
			// Mail must containt something
			assertTrue(email.getContent() != null);

			Multipart multipart = (Multipart) email.getContent();
			// With Attachemnet multipart count must more than 2
			assertTrue(multipart.getCount() > 2);
		} catch (IOException e) {
			assertTrue(false);
		} catch (MessagingException e) {
			assertTrue(false);
		}
	}

}
