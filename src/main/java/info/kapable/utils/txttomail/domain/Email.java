package info.kapable.utils.txttomail.domain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import info.kapable.utils.txttomail.EmailSender;
import info.kapable.utils.txttomail.Utils;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.ImageHandle;

/**
 * Email is a object to <br>
 * * store all data needed to process email<br>
 * 
 * @author Mathieu GOULIN
 */
public class Email {
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
	 * The private constructor to singleton
	 */
	public Email() {
		// Init fields
		this.headers = new HashMap<String, String>();
		this.attachements = new HashMap<String, String>();
		this.body = new ArrayList<String>();
	}
	
	/**
	 * Call this Method at each line you want to store in email<br>
	 * Line can contain and headers tag : some metadata, ...<br>
	 * or an attachment <br>
	 * or an line to process<br>
	 * 
	 * @param line the line to parse
	 * @throws TemplateProcessingException this exception is throw when getProperty throw it
	 * @throws IOException 
	 */
	@SuppressWarnings("unlikely-arg-type")
	public void convert(String line) throws TemplateProcessingException, IOException {
		// Get tag and value from line
		String tag = Utils.getTag(line);
		String value = Utils.getValue(line);

		// if it's a metadata ...
		if (EmailSender.getProperty("headerTags").contains(tag)) {
			this.headers.put(tag, value);
		// if it's an attachement
		} else if (EmailSender.getProperty("attachementTag").equals(tag)) {
			this.addAttachement(value);
		// else add to body (some array, image, text)
		} else if (EmailSender.getProperty("imageTag").equals(tag)) {
			String key = value;
			File file = new File(value);
			if (!file.exists()) {
				value = EmailSender.getConfig()
						.getProperty("template.base.path")
						+ File.separator + value;
			}
			Integer mode = Integer.parseInt(EmailSender.getProperty("imageMode"));
			if(mode.equals(ImageHandle.ATTACHED_MODE)) {	
				// put file in attachment
				key = this.addAttachement(value);
			}
			if(mode.equals(ImageHandle.EMBEDED_MODE)) {
				key = new ImageHandle().returnEmbedding(value);
			}
			this.body.add(tag + ":" + key);		
		} else {
			this.body.add(line);
		}
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
	 * return the value of attachment field 
	 * @return all attachment (contentId = file path) to put in attachment of email
	 */
	public Map<String, String> getAttachements() {
		return attachements;
	}

	/**
	 * Return the value of header field
	 * @return all header pre-defined
	 */
	public Map<String, String> getHeaders() {
		return headers;
	}

	/**
	 * return the email body to format
	 * @return a list of each line body
	 */
	public List<String> getBody() {
		return this.body;
	}
}
