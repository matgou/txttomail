package info.kapable.utils.txttomail;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
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
		String argsWithoutInput[] = { "-TO", "matgou@kapable.info", "-SUBJECT", "this is a test", "-TEXT",
				"Hy, <br/> This is a test mail!" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsWithoutInput);
		assertTrue(TxtToMail.rc != 0);

		String argsSendWithoutTo[] = { "-send", "-SUBJECT", "this is a test", "-TEXT",
				"Hy, <br/> This is a test mail!" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSendWithoutTo);
		assertTrue(TxtToMail.rc != 0);

		String argsSendNotAnEmail[] = { "--send", "-TO", "not an email", "-SUBJECT", "this is a test", "-TEXT",
				"Hy, <br/> This is a test mail!" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSendNotAnEmail);
		assertTrue(TxtToMail.rc != 0);

		String argsSendWithNotExistantTagl[] = { "--send", "-TO", "not an email", "-SUBJECT", "this is a test",
				"-NOEXISTANTTAG", "Hy, <br/> This is a test mail!" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSendWithNotExistantTagl);
		assertTrue(TxtToMail.rc != 0);
	}

	@Test
	public void testAllInOneComand() {
		String args[] = { "--send", "-TO", "matgou@kapable.info", "-SUBJECT", "this is a test", "-TEXT",
				"Hy, <br/> This is a test mail!", "-IMAGE", "./src/test/resources/notification-badge-fill.svg", "--output",
				"allinone.eml" };
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
			for (int i = 0; i < multipart.getCount(); i++) {
				String className = multipart.getBodyPart(i).getContent().getClass().toString();
				System.out.println(className);
				if (multipart.getBodyPart(i).getContent().getClass().equals(String.class)) {
					System.out.println((String) multipart.getBodyPart(i).getContent());
				}
			}
		} catch (IOException e) {
			assertTrue(false);
		} catch (MessagingException e) {
			assertTrue(false);
		}
	}

	@Test
	public void testEmbededImage() {

		File f = new File("embededImagemail.template");
		try {
			f.delete();

			FileWriter fw = new FileWriter(f);
			fw.write("SUBJECT:This is a test\n");
			fw.write("TO:test@kapable.info\n");
			fw.write("FROM:matgou@kapable.info\n");
			fw.write("IMAGE:src/test/resources/checkbox-circle-fill.svg\n");
			fw.write("TEXT:Hello Mathieu\n");
			fw.write("TEXT:Hy, <br/>\n");
			fw.write("TEXT:This is a test mail !\n");
			fw.write("CSV:src/main/resources/tab1.csv\n");
			fw.flush();
			fw.close();

			String[] argsFROM = { "-i", "embededImagemail.template", "--html", "embededImagemail.html", "--send" };
			info.kapable.utils.txttomail.TxtToMail.testUnit = true;
			info.kapable.utils.txttomail.TxtToMail.main(argsFROM);

			FileInputStream fis = new FileInputStream("embededImagemail.html");
			byte[] buffer = new byte[10];
			StringBuilder sb = new StringBuilder();
			while (fis.read(buffer) != -1) {
				sb.append(new String(buffer));
				buffer = new byte[10];
			}
			fis.close();

			String HTMLContent = sb.toString();
			assertTrue(HTMLContent.contains("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL"));
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
			f.delete();
		}

		f.delete();
	}

	@Test
	/**
	 * Test REAMDE
	 */
	public void testMain() {
		File f = new File("mail.template");
		f.delete();

		// Emulate : java -jar TxtToMail.jar -i mail.template -FROM matgou@kapable.info
		String[] argsFROM = { "-i", "mail.template", "-FROM", "matgou@kapable.info" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsFROM);

		// Emulate : java -jar TxtToMail.jar -i mail.template -TO test@kapable.info
		String[] argsTO = { "-i", "mail.template", "-TO", "test@kapable.info" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsTO);

		// Emulate : java -jar TxtToMail.jar -i mail.template -SUBJECT "This is a test"
		String[] argsSUBJECT = { "-i", "mail.template", "-SUBJECT", "This is a test" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsSUBJECT);

		// Emulate : java -jar TxtToMail.jar -i mail.template -TEXT "Hy, <br/>"
		String[] argsTEXT1 = { "-i", "mail.template", "-TEXT", "Hy, <br/>" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsTEXT1);

		// Emulate : java -jar TxtToMail.jar -i mail.template -TEXT "This is a test mail
		// !"
		String[] argsTEXT2 = { "-i", "mail.template", "-TEXT", "This is a test mail !" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsTEXT2);

		// Emulate : java -jar TxtToMail.jar -i mail.template -CSV
		// "src/main/resources/tab1.csv"
		String[] argsCSV = { "-i", "mail.template", "-CSV", "src/main/resources/tab1.csv" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsCSV);

		// Emulate : java -jar TxtToMail.jar -i mail.template -PJ
		// "src/main/resources/tab1.csv"
		String[] argsPJ = { "-i", "mail.template", "-PJ", "src/main/resources/tab1.csv" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsPJ);

		// Emulate : java -jar TxtToMail.jar -i mail.template -IMG "Logo.png"
		String[] argsIMG = { "-i", "mail.template", "-IMAGE", "src/test/resources/notification-badge-fill.svg" };
		info.kapable.utils.txttomail.TxtToMail.testUnit = true;
		info.kapable.utils.txttomail.TxtToMail.main(argsIMG);

		// Emulate java -jar TxtToMail.jar -i mail.template -o output.eml -send
		String[] argsSend = { "-i", "mail.template", "-o", "output.eml", "-send" };
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
