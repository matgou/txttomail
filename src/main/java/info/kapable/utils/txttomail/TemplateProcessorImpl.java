package info.kapable.utils.txttomail;

import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.PropertyConfigurator;

/**
 * This implementation of TemplateProcessor load input file and config<br>
 * And launch Email singleton object method to populate it and flush it <br>
 * 
 * @author MGOULIN
 *
 */
public class TemplateProcessorImpl implements TemplateProcessor {
	/**
	 * Input Stream from inputFile
	 */
	private InputStream input;
	/**
	 * Writer to output
	 */
	private Writer output;

	/**
	 * Constructor to initialize TemplateProcessor from args
	 * @param inputFilePath the path of input text file
	 * @param configFilePath the path of properties file
	 * @param outputFilePath the path of output html file
	 * @throws TemplateProcessingException if some error during the process a TemplateProcessingException is throw
	 */
	public TemplateProcessorImpl(String inputFilePath, String configFilePath,
			String outputFilePath) throws TemplateProcessingException {
		try {
			// Load input file Path
			this.input = new FileInputStream(inputFilePath);

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
				this.output = new BufferedWriter(new OutputStreamWriter(
						new FileOutputStream(outputFilePath)));
			}

			// Push config to Email singleton
			Email.getEmail().setConfig(config);
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
		props.setProperty("log4j.appender.file.File", logFile); 
		LogManager.resetConfiguration(); 
		PropertyConfigurator.configure(props); 
	}

	@Override
	public void process() throws TemplateProcessingException {
		BufferedReader in = new BufferedReader(
				new InputStreamReader(this.input));
		try {
			String line;
			// Read all line
			while ((line = in.readLine()) != null) {
				// Load line to Email object
				Email.getEmail().convert(line);
			}
		} catch (IOException e) {
			// If some error when reading inputStream
			throw new TemplateProcessingException(e);
		}
		// Send email
		Email.getEmail().flush(this.output);
	}
}