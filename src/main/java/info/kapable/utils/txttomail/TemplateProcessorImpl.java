package info.kapable.utils.txttomail;

import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This implementation of TemplateProcessor load input file and config<br>
 * And launch Email singleton object method to populate it and flush it <br>
 * 
 * @author MGOULIN
 *
 */
public class TemplateProcessorImpl implements TemplateProcessor {
	private static final Logger logger = LoggerFactory.getLogger(TemplateProcessorImpl.class);

	/**
	 * Writer to output
	 */
	private OutputStream output;
	private OutputStream htmlOutput;

	/**
	 * Interface to send email
	 */
	private EmailSender emailSender;
	private MimeMessage message;

	private String templatePath;
	/**
	 * Constructor to initialize TemplateProcessor from args
	 * @param inputFilePath the path of input text file
	 * @param configFilePath the path of properties file
	 * @param outputFilePath the path of output html file
	 * @param htmlOutputFilePath 
	 * @throws TemplateProcessingException if some error during the process a TemplateProcessingException is throw
	 */
	public TemplateProcessorImpl(String inputFilePath, String configFilePath,
			String outputFilePath, String htmlOutputFilePath) throws TemplateProcessingException {
		try {
			// For output template 
			this.templatePath = inputFilePath;
			
			// load default config
			InputStream defaultConfigInput = TemplateProcessorImpl.class.getClassLoader().getResourceAsStream("config.properties");
			Properties config = new Properties();
			config.load(defaultConfigInput);

			// Load config
			if(configFilePath != null) {
				File f = new File(configFilePath);
				if(f.exists()) {
					InputStream configInput = new FileInputStream(configFilePath);
					Properties customConfig = new Properties();
					customConfig.load(configInput);
					config.putAll(customConfig);
				}
			}

			// Update sysenv from config
			String java_log = config.getProperty("logfile", "application.log");
			this.updateLog4jConfiguration(java_log);

			// Create an output writer
			if (outputFilePath != null) {
				this.output = new FileOutputStream(outputFilePath);
			}

			// Create an output writer
			if (htmlOutputFilePath != null) {
				this.htmlOutput = new FileOutputStream(htmlOutputFilePath);
			}
			// Push config to Email singleton
			emailSender = EmailSender.getInstance(config);
		} catch (FileNotFoundException e) {
			throw new TemplateProcessingException(e);
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
	}

	/**
	 * Constructor to initialize TemplateProcessor from args
	 * @param inputFile the path of input text file
	 * @param customConfig Custom properties object
	 * @param outputFilePath the path of output html file
	 * @param htmlOutputFilePath 
	 * @throws TemplateProcessingException if some error during the process a TemplateProcessingException is throw
	 */
	public TemplateProcessorImpl(File inputFile, Properties customConfig, String outputFilePath, String htmlOutputFilePath) throws TemplateProcessingException {
		try {
			// For output template 
			this.templatePath = inputFile.getAbsolutePath();
			
			Properties config = new Properties();
			// load default config
			InputStream defaultConfigInput = TemplateProcessorImpl.class.getClassLoader().getResourceAsStream("config.properties");
			config.load(defaultConfigInput);

			// Load config
			config.putAll(customConfig);

			// Create an output writer
			if (outputFilePath != null) {
				this.output = new FileOutputStream(outputFilePath);
			}

			// Create an output writer
			if (htmlOutputFilePath != null) {
				this.htmlOutput = new FileOutputStream(htmlOutputFilePath);
			}
			// Push config to Email singleton
			emailSender = EmailSender.getInstance(config);
		} catch (FileNotFoundException e) {
			throw new TemplateProcessingException(e);
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
	}

	/**
	 * Update logfile in log4j
	 * @param logFile
	 */
	private void updateLog4jConfiguration(String logFile) { 
		Properties props = new Properties(); 
		try { 
			InputStream configStream = getClass().getResourceAsStream( "/log4j.properties"); 
			props.load(configStream); 
			configStream.close(); 
		} catch (IOException e) { 
			System.out.println("Errornot laod configuration file "); 
		} 
		props.setProperty("log4j.rootLogger", "INFO, file, stdout"); 
		props.setProperty("log4j.appender.file.File", logFile); 
		props.setProperty("log4j.appender.file","org.apache.log4j.RollingFileAppender");
		props.setProperty("log4j.appender.file.MaxFileSize","10MB");
		props.setProperty("log4j.appender.file.MaxBackupIndex","10");
		props.setProperty("log4j.appender.file.layout","org.apache.log4j.PatternLayout");
		props.setProperty("log4j.appender.file.layout.ConversionPattern","%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n");
		LogManager.resetConfiguration(); 
		PropertyConfigurator.configure(props); 
	}

	public void send(Email email) throws TemplateProcessingException, IOException, MessagingException {
		
		// Send email
		this.message = emailSender.send(email);
		if(output != null ) {
			this.getMessage().writeTo(this.output);
			logger.info("E-mail file written to output");
		}
	}

	public MimeMessage getMessage() {
		return this.message;
	}

	public void saveToInput(Email email) throws TemplateProcessingException {
		try {
			Iterator<Entry<String, String>> it;
			Writer w = new FileWriter(this.templatePath);
			// Process header
			it = email.getHeaders().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> header = it.next();
				w.write(header.getKey() + ":" + header.getValue() + "\n");
			}
			// 
			it = email.getAttachements().entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> attachment = it.next();
				w.write(EmailSender.getProperty("attachementTag") + ":" + attachment.getValue() + "\n");
			}
			// Process body
			for(String line: email.getBody()) {
				w.write(line + "\n");
			}
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public Email loadEmailFromInput() throws TemplateProcessingException {
		Email email = new Email();

		// Load input file Path
		FileInputStream input;
		try {
			if(this.templatePath != null) {
				input = new FileInputStream(this.templatePath);
			} else {
				return email;
			}
		} catch (FileNotFoundException e1) {
			return email;
		}

		BufferedReader in = new BufferedReader(
				new InputStreamReader(input));
		try {
			String line;
			// Read all line
			while ((line = in.readLine()) != null) {
				// Load line to Email object
				email.convert(line);
			}
		} catch (IOException e) {
			// If some error when reading inputStream
			throw new TemplateProcessingException(e);
		}
		try {
			in.close();
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
		return email;
	}

	@Override
	public void saveHtml(Email email) throws TemplateProcessingException {
		PrintWriter p = new PrintWriter(this.htmlOutput);
		String title = emailSender.subject(email);
		p.write("<html><head><title>"+ title + "</title></head><body>");
		emailSender.bodyHTML(email, p);
		p.write("</body></html>");
		p.flush();
		p.close();
	}
}