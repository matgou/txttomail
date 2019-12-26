package info.kapable.utils.txttomail;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

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
		String tempDir = "target/JunitTemp";
		String filePrefix = "embededImagemail";
		File dTemp = new File(tempDir);
		File fTemplate = new File(tempDir + "/" + filePrefix + ".template");
		File fConfig = new File(tempDir + "/" + filePrefix + ".properties");
		File fTemplateHeader = new File(tempDir + "/" + filePrefix + ".header.ftl");
		File fTemplateFooter = new File(tempDir + "/" + filePrefix + ".footer.ftl");

		try {
			try {
				FileUtils.deleteDirectory(dTemp);
			} catch (NoSuchFileException e) {
				e.printStackTrace();
			}
			Files.createDirectory(dTemp.toPath());

			FileWriter fwTemplate = new FileWriter(fTemplate);
			FileWriter fwConfig = new FileWriter(fConfig);
			OutputStream fwTemplateHeader = new FileOutputStream(fTemplateHeader);
			OutputStream fwTemplateFooter = new FileOutputStream(fTemplateFooter);
			
			// Create Properties file
			fwConfig.write("mail.smtp.host=smtp.orange.fr\n");
			fwConfig.write("logfile=" + tempDir + "/" + filePrefix + ".log\n");
			fwConfig.write("head.html.template=" + filePrefix + ".header.ftl\n");
			fwConfig.write("foot.html.template=" + filePrefix + ".footer.ftl\n");
			fwConfig.write("headers.txttomail-junit=true\n");
			fwConfig.write("template.base.path=" + tempDir + "\n");
			fwConfig.write("mail.subject.format=[${headers[\"TYPE\"]}] ${headers[\"SUBJECT\"]}\n");
			fwConfig.flush();
			fwConfig.close();

			fwTemplate.write("SUBJECT:This is a test\n");
			fwTemplate.write("TYPE:INFO\n");
			fwTemplate.write("TO:matgou@kapable.info\n");
			fwTemplate.write("FROM:matgou@kapable.info\n");
			fwTemplate.write("TEXT:Hello Mathieu\n");
			fwTemplate.write("TEXT:Hy, <br/>\n");
			fwTemplate.write("TEXT:This is a test mail !\n");
			fwTemplate.write("IMAGE:src/test/resources/checkbox-circle-fill.svg\n");
			fwTemplate.write("ARRAY:src/test/resources/tab1.csv\n");
			fwTemplate.flush();
			fwTemplate.close();
			
			Path header = new File("src/test/resources/header.ftl").toPath();
			Files.copy(header , fwTemplateHeader);
			fwTemplateHeader.flush();
			fwTemplateHeader.close();
			
			Path footer = new File("src/test/resources/footer.ftl").toPath();
			Files.copy(footer , fwTemplateFooter);
			fwTemplateFooter.flush();
			fwTemplateFooter.close();
			
			
			String[] argsFROM = { "-c", tempDir + "/" + filePrefix + ".properties", "-i", tempDir + "/" + filePrefix + ".template", "--html", tempDir + "/" + filePrefix + ".html", "--send" };
			info.kapable.utils.txttomail.TxtToMail.testUnit = true;
			info.kapable.utils.txttomail.TxtToMail.main(argsFROM);
			MimeMessage email = info.kapable.utils.txttomail.TxtToMail.getMimeMessage();
			String headerVerif[] = email.getHeader("txttomail-junit");
			assertTrue(headerVerif[0].contentEquals("true"));
			FileInputStream fis = new FileInputStream(tempDir + "/" + filePrefix + ".html");
			byte[] buffer = new byte[10];
			StringBuilder sb = new StringBuilder();
			while (fis.read(buffer) != -1) {
				sb.append(new String(buffer));
				buffer = new byte[10];
			}
			fis.close();

			String HTMLContent = sb.toString();
			//System.out.println("HTML = " + HTMLContent);
			assertTrue(HTMLContent.contains("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+CiAgICA8Zz4KICAgICAgICA8cGF0aCBmaWxsPSJub25lIiBkPSJNMCAwaDI0djI0SDB6Ii8+CiAgICAgICAgPHBhdGggZD0iTTEyIDIyQzYuNDc3IDIyIDIgMTcuNTIzIDIgMTJTNi40NzcgMiAxMiAyczEwIDQuNDc3IDEwIDEwLTQuNDc3IDEwLTEwIDEwem0tLjk5Ny02bDcuMDctNy4wNzEtMS40MTQtMS40MTQtNS42NTYgNS42NTctMi44MjktMi44MjktMS40MTQgMS40MTRMMTEuMDAzIDE2eiIvPgogICAgPC9nPgo8L3N2Zz4K"));
			assertTrue(HTMLContent.contains("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+CiAgICA8Zz4KICAgICAgICA8cGF0aCBmaWxsPSJub25lIiBkPSJNMCAwaDI0djI0SDB6Ii8+CiAgICAgICAgPHBhdGggZD0iTTEyLjg2NiAzbDkuNTI2IDE2LjVhMSAxIDAgMCAxLS44NjYgMS41SDIuNDc0YTEgMSAwIDAgMS0uODY2LTEuNUwxMS4xMzQgM2ExIDEgMCAwIDEgMS43MzIgMHpNMTEgMTZ2Mmgydi0yaC0yem0wLTd2NWgyVjloLTJ6Ii8+CiAgICA8L2c+Cjwvc3ZnPgo="));
			String subject = email.getSubject();
			assertTrue(subject.contentEquals("[INFO] This is a test"));
		} catch (IOException e) {
			e.printStackTrace();
			assertTrue(false);
		} catch (MessagingException e) {
			e.printStackTrace();
			assertTrue(false);
		}
	}

	@Test
	/**fTemplate REAMDE
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
		String[] argsPJ = { "-i", "mail.template", "-PJ", "src/test/resources/tab1.csv" };
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
