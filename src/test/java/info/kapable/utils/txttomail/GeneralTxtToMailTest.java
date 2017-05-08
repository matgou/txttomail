package info.kapable.utils.txttomail;

import static org.junit.Assert.*;

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
	public void testInvalidSyntax() {
		String argsWithoutInput[] = {"-TO", "matgou@kapable.info", "-SUBJECT", "this is a test", "-TEXT", "Hy, <br/> This is a test mail!"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsWithoutInput);
		assertTrue(TxtToMail.rc != 0);
		
		String argsSendWithoutTo[] = {"-send", "-SUBJECT", "this is a test", "-TEXT", "Hy, <br/> This is a test mail!"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSendWithoutTo);
		assertTrue(TxtToMail.rc != 0);
		
		String argsSendNotAnEmail[] = {"--send", "-TO", "not an email", "-SUBJECT", "this is a test", "-TEXT", "Hy, <br/> This is a test mail!"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSendNotAnEmail);
		assertTrue(TxtToMail.rc != 0);
	}
	@Test
	public void testAllInOneComand() {
		String args[] = {"--send", "-TO", "matgou@kapable.info", "-SUBJECT", "this is a test", "-TEXT", "Hy, <br/> This is a test mail!"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(args);
		
		// Do some check
		MimeMessage email = info.kapable.utils.txttomail.TxtToMail.getMimeMessage();
		assertTrue(email != null);
		try {
			// Mail must contain something
			assertTrue(email.getContent() != null);

			Multipart multipart = (Multipart) email.getContent();
			// With Attachment multipart count must more than 2
			assertTrue(multipart.getCount() >= 2);
		} catch (IOException e) {
			assertTrue(false);
		} catch (MessagingException e) {
			assertTrue(false);
		}
	}
	@Test
	/**
	 * Test REAMDE
	 */
	public void testMain() {
		// Emulate : java -jar TxtToMail.jar -i mail.template -FROM matgou@kapable.info
		String[] argsFROM = {"-i", "mail.template","-FROM", "matgou@kapable.info"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsFROM);
		
		// Emulate : java -jar TxtToMail.jar -i mail.template -TO test@kapable.info
		String[] argsTO = {"-i", "mail.template","-TO", "test@kapable.info"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsTO);
		
		// Emulate : java -jar TxtToMail.jar -i mail.template -SUBJECT "This is a test"
		String[] argsSUBJECT = {"-i", "mail.template","-SUBJECT", "This is a test"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSUBJECT);
		
		// Emulate : java -jar TxtToMail.jar -i mail.template -TEXT "Hy, <br/>"
		String[] argsTEXT1 = {"-i", "mail.template","-TEXT", "Hy, <br/>"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsTEXT1);
		
		// Emulate : java -jar TxtToMail.jar -i mail.template -TEXT "This is a test mail !"
		String[] argsTEXT2 = {"-i", "mail.template","-TEXT", "This is a test mail !"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsTEXT2);
		

		// Emulate : java -jar TxtToMail.jar -i mail.template -CSV "src/main/resources/tab1.csv"
		String[] argsCSV = {"-i", "mail.template","-CSV", "src/main/resources/tab1.csv"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsCSV);
		
		// Emulate : java -jar TxtToMail.jar -i mail.template -PJ "src/main/resources/tab1.csv"
		String[] argsPJ = {"-i", "mail.template","-PJ", "src/main/resources/tab1.csv"};
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsPJ);

		// Emulate java -jar TxtToMail.jar -i mail.template -o output.eml -send
		String[] argsSend = {"-i", "mail.template", "-o", "output.eml", "-send"};
		info.kapable.utils.txttomail.TxtToMail.main(argsSend);
		
		// Do some check
		MimeMessage email = info.kapable.utils.txttomail.TxtToMail.getMimeMessage();
		assertTrue(email != null);
		try {
			// Mail must contain something
			assertTrue(email.getContent() != null);

			Multipart multipart = (Multipart) email.getContent();
			// With Attachment multipart count must more than 2
			assertTrue(multipart.getCount() > 2);
		} catch (IOException e) {
			assertTrue(false);
		} catch (MessagingException e) {
			assertTrue(false);
		}
	}

}
