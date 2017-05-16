package info.kapable.utils.txttomail;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;
import info.kapable.utils.txttomail.htmlprocessor.HTMLProcessor;
import info.kapable.utils.txttomail.htmlprocessor.HTMLProcessorBuilder;
import info.kapable.utils.txttomail.textprocessor.TextProcessor;
import info.kapable.utils.txttomail.textprocessor.TextProcessorBuilder;

/**
 * EmailSender is an singleton object :
 * * * build HTML and TXT part of email <br>
 * * format mime file with attachement and link via contentId<br>
 * * send email via Javax.mail api<br>
 */
public class EmailSender {
	private static final Logger logger = LoggerFactory.getLogger(EmailSender.class);
	
	/**
	 * Config to access properties
	 */
	private static Properties config;

	/**
	 * Singleton object
	 */
	private static EmailSender instance;
	
	public static EmailSender getInstance(Properties config) {
		EmailSender.config = config;
		if(instance == null) {
			instance = new EmailSender();
		}
		return instance;
	}

	public static EmailSender getInstance() {
		if(instance != null) {
			instance = new EmailSender();
		}
		return instance;
	}

	/**
	 * Build email multipart and send it
	 * @param email the email to send
	 * @throws TemplateProcessingException if error during process
	 * @return the javax.mail object sended
	 */
	public MimeMessage send(Email email) throws TemplateProcessingException {
		Map<String, String> headers = email.getHeaders();
		Map<String, String> attachements = email.getAttachements();
		
		// Create the message
		Session session = Session.getInstance(EmailSender.config);
		MimeMessage message = new MimeMessage(session) {
			    @Override
			    protected void updateMessageID() { } // Prevent MimeMessage from overwriting our Message-ID
		};
		Multipart multiPart = new MimeMultipart("alternative");
		
		try {
			// insert in message object the fromTag if exist
			if (headers.get(getProperty("fromTag")) != null) {
				message.setFrom(new InternetAddress(headers
						.get(getProperty("fromTag"))));
			}
			
			if (getProperty("messageIdPrefix") != null) {
				message.setHeader("Message-ID", getProperty("messageIdPrefix") + "-" + UUID.randomUUID().toString() + "@" + getProperty("messageIdSuffix"));
			}
			// insert in message object the toTag if exist
			if (headers.get(getProperty("toTag")) != null) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress
						.parse(headers.get(getProperty("toTag"))));
			}
			// insert in message object the ccTag if exist
			if (headers.get(getProperty("ccTag")) != null) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress
						.parse(headers.get(getProperty("ccTag"))));
			}
			// Get the subject template and process it to insert value from metadata 
			String subjectTemplate = getProperty("mail.subject.format");
			HTMLProcessor p = HTMLProcessorBuilder.getStringProcessor(
					subjectTemplate, email, headers);
			StringWriter strWriter = new StringWriter();
			p.process("", new HashMap<String, Object>(), strWriter);
			message.setSubject(strWriter.toString());

			// build the textPart of mail
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(bodyTxt(email), "utf-8");
			multiPart.addBodyPart(textPart);

			// build the htmlPart of mail
			MimeBodyPart htmlPart = new MimeBodyPart();
			StringWriter outHTMLContent = new StringWriter();
			bodyHTML(email, outHTMLContent);
			htmlPart.setContent(outHTMLContent.toString(),
					"text/html; charset=utf-8");
			multiPart.addBodyPart(htmlPart);
			
			// for each attachment get file and put it in mimeMessage
			Iterator<Map.Entry<String, String>> it = attachements
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				String filename = entry.getValue();
				File f = new File(filename);
				if(!f.exists()) {
					f = new File(EmailSender.getProperty("template.base.path") + "/" +filename);
				}
				if ((f.exists()) && (!f.isDirectory())) {
					MimeBodyPart messageBodyPart = new MimeBodyPart();
					DataSource source = new javax.activation.FileDataSource(
							f.getAbsolutePath());
					messageBodyPart
							.setDataHandler(new javax.activation.DataHandler(
									source));
					messageBodyPart.setFileName(Utils.basename(f));
					messageBodyPart.setContentID(entry.getKey());
					multiPart.addBodyPart(messageBodyPart);
				} else {
					logger.error("File not found : " + filename);
				}
			}
			// build message
			message.setContent(multiPart);
			// send message 
			if (!TxtToMail.testUnit) {
				logger.info("Message sent : to=" + headers.get(getProperty("toTag")) + ", subject=" + headers.get(getProperty("subjectTag")));
				javax.mail.Transport.send(message);
			}
			
			return message;
		} catch (MessagingException e) {
			// in case of error
			throw new TemplateProcessingException(e);
		}
	}

	/**
	 * This method return property value from key
	 * @param key the key to find property
	 * @return the value of property
	 * @throws TemplateProcessingException if key not found in properties throw TemplateProcessingException
	 */
	public static String getProperty(String key) throws TemplateProcessingException {
		// case when config is null
		if(config == null) {
			throw new TemplateProcessingException("Unable to find key '" + key + "' in properties, no properties set in Email singleton, please call Email.setConfig before use getProperty");
		}
		// get val from key
		String val = config.getProperty(key);
		// case when val is null
		if (val == null) {
			throw new TemplateProcessingException("Unable to find key '" + key + "' in properties file");
		}
		return val;
	}
	

	/**
	 * Return an HTML String for HTML Part of email
	 * @param email 
	 * @param out an optional writer to output email
	 * @return
	 * @throws TemplateProcessingException
	 */
	private String bodyHTML(Email email, Writer out) throws TemplateProcessingException {
		// writer header
		HTMLProcessor headerProcessor = HTMLProcessorBuilder
				.getTemplateProcessor("head", email, EmailSender.config, email.getHeaders());
		headerProcessor.process("", new HashMap<String, Object>(), out);
		
		// for each line
		for (int i = 0; i < email.getBody().size(); i++) {
			String line = email.getBody().get(i);
			String tag = Utils.getTag(line);
			Map<String,Object> mapExtrat = new HashMap<String, Object>();
			// if not the last 
			if (i < email.getBody().size() - 1) {
				String nextTag = Utils.getTag(email.getBody().get(i+1));
				mapExtrat.put("nextTag", nextTag);
			}
			// if not the firts
			if (i > 0) {
				String previusTag = Utils.getTag(email.getBody().get(i-1));
				mapExtrat.put("previusTag", previusTag);
			}
			// process
			HTMLProcessor p = HTMLProcessorBuilder.getTemplateProcessor("tag."
					+ tag, email, EmailSender.config, email.getHeaders());
			p.process(Utils.getValue(line), mapExtrat, out);
		}

		// write footer
		HTMLProcessor footerProcessor = HTMLProcessorBuilder
				.getTemplateProcessor("foot", email, EmailSender.config, email.getHeaders());
		footerProcessor.process("", new HashMap<String, Object>(), out);

		return out.toString();
	}

	/**
	 * For each line format BODY part using the appropriate TextProcessor
	 * @param email 
	 * @return the textPart of Email
	 * @throws TemplateProcessingException if error while process a line
	 */
	private String bodyTxt(Email email) throws TemplateProcessingException {
		Writer out = new StringWriter();
		List<String> body = email.getBody();
		for (String line : body) {
			String tag = Utils.getTag(line);
			getTextProcessor(tag).process(Utils.getValue(line), out);
		}
		return out.toString();
	}

	
	/**
	 * Search in property to find the textProcessor for a TAG
	 * @param tag the TAG part of line
	 * @return the TextProcessor to use
	 * @throws TemplateProcessingException if no TextProcessor was found
	 */
	private TextProcessor getTextProcessor(String tag)
			throws TemplateProcessingException {
		TextProcessor p;
		if (EmailSender.config.getProperty("tag." + tag + ".text") == null) {
			p = TextProcessorBuilder.getDefaultProcessor();
		} else {
			p = TextProcessorBuilder.getProcessor(getProperty("tag." + tag
					+ ".text"));
		}
		if (p == null) {
			throw new TemplateProcessingException(
					"Unable to find TextProcessor for tag : '" + tag
							+ "'. Please, check tag." + tag
							+ ".text in properties file");
		}
		return p;
	}

	public static Properties getConfig() {
		return config;
	}

}
