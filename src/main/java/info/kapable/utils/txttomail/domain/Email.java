package info.kapable.utils.txttomail.domain;

import info.kapable.utils.txttomail.TxtToMail;
import info.kapable.utils.txttomail.Utils;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;
import info.kapable.utils.txttomail.htmlprocessor.HTMLProcessor;
import info.kapable.utils.txttomail.htmlprocessor.HTMLProcessorBuilder;
import info.kapable.utils.txttomail.textprocessor.TextProcessor;
import info.kapable.utils.txttomail.textprocessor.TextProcessorBuilder;

import java.io.File;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
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

/**
 * Email is a singleton to <br>
 * * store all data needed to process email<br>
 * * build HTML and TXT part of email <br>
 * * format mime file with attachement and link via contentId<br>
 * * send email via Javax.mail api<br>
 * 
 * @author Mathieu GOULIN
 */
public class Email {

	private static final Logger logger = LoggerFactory.getLogger(Email.class);
	
	/**
	 * The singleton object storage
	 */
	private static Email email;
	/**
	 * Config to access properties
	 */
	private Properties config;
	/**
	 * Map to store specific headers: from, to, cc and all specific metadata 
	 */
	private Map<String, String> headers;
	/**
	 * Map to store contentId of attachements
	 */
	private Map<String, String> attachements;

	/**
	 * List to store each line of text file passing is parameters
	 */
	private List<String> body;
	/**
	 * The multipart object to store each part of email message
	 */
	private Multipart multiPart;
	/**
	 * The email message
	 */
	private MimeMessage message;

	/**
	 * Getter to singleton
	 * @return the uniq instance of Email
	 */
	public static Email getEmail() {
		if (email == null) {
			email = new Email();
		}
		return email;
	}

	/**
	 * Getter for config, if config needed during processing
	 * @return the config loaded in Email
	 */
	public Properties getConfig() {
		return this.config;
	}

	/**
	 * Setter for config, in this method the message will be instantiate from configuration<br>
	 * So smtp parameters like smtp.mail.host need to be in config<br>
	 * @param config the config to load
	 */
	public void setConfig(Properties config) {
		this.config = config;
		// Create the message
		Session session = Session.getInstance(this.config);
		message = new MimeMessage(session);
	}

	/**
	 * The private constructor to singleton
	 */
	private Email() {
		// Init fields
		this.headers = new HashMap<String, String>();
		this.attachements = new HashMap<String, String>();
		this.body = new ArrayList<String>();
		this.multiPart = new MimeMultipart("alternative");
	}
	
	/**
	 * Call this Method at each line you want to store in email<br>
	 * Line can contain and headers tag : some metadata, ...<br>
	 * or an attachment <br>
	 * or an line to process<br>
	 * 
	 * @param line the line to parse
	 * @throws TemplateProcessingException this exception is throw when getProperty throw it
	 */
	public void convert(String line) throws TemplateProcessingException {
		// Get tag and value from line
		String tag = Utils.getTag(line);
		String value = Utils.getValue(line);

		// if it's a metadata ...
		if (getProperty("headerTags").contains(tag)) {
			this.headers.put(tag, value);
		// if it's an attachement
		} else if (getProperty("attachementTag").equals(tag)) {
			addAttachement(value);
		// else add to body (some array, image, text)
		} else {
			this.body.add(line);
		}
	}

	/**
	 * This method return property value from key
	 * @param key the key to find property
	 * @return the value of property
	 * @throws TemplateProcessingException if key not found in properties throw TemplateProcessingException
	 */
	private String getProperty(String key) throws TemplateProcessingException {
		// case when config is null
		if(this.config == null) {
			throw new TemplateProcessingException("Unable to find key '" + key + "' in properties, no properties set in Email singleton, please call Email.setConfig before use getProperty");
		}
		// get val from key
		String val = this.config.getProperty(key);
		// case when val is null
		if (val == null) {
			throw new TemplateProcessingException("Unable to find key '" + key + "' in properties file");
		}
		return val;
	}

	/**
	 * Build email multipart and send it
	 * @param output and Writer to html (nullable)
	 * @throws TemplateProcessingException if error during process
	 */
	public void flush(Writer output) throws TemplateProcessingException {
		try {
			// insert in message object the fromTag if exist
			if (this.headers.get(getProperty("fromTag")) != null) {
				message.setFrom(new InternetAddress(this.headers
						.get(getProperty("fromTag"))));
			}
			// insert in message obejct the toTag if exist
			if (this.headers.get(getProperty("toTag")) != null) {
				message.setRecipients(Message.RecipientType.TO, InternetAddress
						.parse(this.headers.get(getProperty("toTag"))));
			}
			// insert in message object the ccTag if exist
			if (this.headers.get(getProperty("ccTag")) != null) {
				message.setRecipients(Message.RecipientType.CC, InternetAddress
						.parse(this.headers.get(getProperty("ccTag"))));
			}
			// Get the subject template and process it to insert value from metadata 
			String subjectTemplate = getProperty("mail.subject.format");
			HTMLProcessor p = HTMLProcessorBuilder.getStringProcessor(
					subjectTemplate, this.headers);
			StringWriter strWriter = new StringWriter();
			p.process("", strWriter);
			message.setSubject(strWriter.toString());

			// build the textPart of mail
			MimeBodyPart textPart = new MimeBodyPart();
			textPart.setText(bodyTxt(), "utf-8");
			this.multiPart.addBodyPart(textPart);

			// build the htmlPart of mail
			MimeBodyPart htmlPart = new MimeBodyPart();
			StringWriter outHTMLContent = new StringWriter();
			bodyHTML(outHTMLContent);
			htmlPart.setContent(outHTMLContent.toString(),
					"text/html; charset=utf-8");
			this.multiPart.addBodyPart(htmlPart);
			
			// for each attachment get file and put it in mimeMessage
			Iterator<Map.Entry<String, String>> it = this.attachements
					.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<String, String> entry = it.next();
				String filename = entry.getValue();
				File f = new File(filename);
				if ((f.exists()) && (!f.isDirectory())) {
					MimeBodyPart messageBodyPart = new MimeBodyPart();
					DataSource source = new javax.activation.FileDataSource(
							filename);
					messageBodyPart
							.setDataHandler(new javax.activation.DataHandler(
									source));
					messageBodyPart.setFileName(Utils.basename(f));
					messageBodyPart.setContentID(entry.getKey());
					this.multiPart.addBodyPart(messageBodyPart);
				} else {
					logger.error("File not found : " + filename);
				}
			}
			// build message
			message.setContent(this.multiPart);
			// send message 
			if (!TxtToMail.testUnit) {
				logger.info("Message sent.");
				javax.mail.Transport.send(message);
			}
		} catch (MessagingException e) {
			// in case of error
			throw new TemplateProcessingException(e);
		}
	}

	/**
	 * Return an HTML String for HTML Part of email
	 * @param out an optional writer to output email
	 * @return
	 * @throws TemplateProcessingException
	 */
	private String bodyHTML(Writer out) throws TemplateProcessingException {
		// writer header
		HTMLProcessor headerProcessor = HTMLProcessorBuilder
				.getTemplateProcessor("head", this.config, this.headers);
		headerProcessor.process("", out);
		
		// for each line
		for (String line : this.body) {
			String tag = Utils.getTag(line);
			// process
			HTMLProcessor p = HTMLProcessorBuilder.getTemplateProcessor("tag."
					+ tag, this.config, this.headers);
			p.process(Utils.getValue(line), out);
		}

		// write footer
		HTMLProcessor footerProcessor = HTMLProcessorBuilder
				.getTemplateProcessor("foot", this.config, this.headers);
		footerProcessor.process("", out);

		return out.toString();
	}

	/**
	 * For each line format BODY part using the appropriate TextProcessor
	 * @return the textPart of Email
	 * @throws TemplateProcessingException if error while process a line
	 */
	private String bodyTxt() throws TemplateProcessingException {
		Writer out = new StringWriter();

		for (String line : this.body) {
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
		if (this.config.getProperty("tag." + tag + ".text") == null) {
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

	/**
	 * Add an attachment to a message
	 * @param filename the path to the file
	 * @return the conentId of file in MimeMessage
	 */
	public String addAttachement(String filename) {
		String key = UUID.randomUUID().toString();
		this.attachements.put(key, filename);
		return key.toString();
	}

	/**
	 * Return the MimeMessage of Email Singleton
	 * @return the MimeMessage
	 */
	public MimeMessage getMessage() {
		return this.message;
	}
	
	/**
	 * return the value of attachment field 
	 * @return all attachment (contentId => file path) to put in attachment of email
	 */
	public Map<String, String> getAttachements() {
		return attachements;
	}
}
