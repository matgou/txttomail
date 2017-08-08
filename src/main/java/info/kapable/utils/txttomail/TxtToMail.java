package info.kapable.utils.txttomail;

import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.IOException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class of application <br>
 * Use commons.cli to parse input arguments <br>
 * <br>
 * -c or --config configFile is to determine path for configuration file <br>
 * -i or --input inputFile is a text file with on each line "TAG: BODY" <br>
 * 	* TAG is an descriptor (sea example config)  <br>
 *  * BODY is the line data (text, path to attachments, email) <br>
 * -o is and option to save html output on file <br>
 * 
 * @author Mathieu GOULIN
 *
 */
public class TxtToMail {
	private static final Logger logger = LoggerFactory.getLogger(TxtToMail.class);

	/**
	 * Set true when in testUnit context
	 */
	public static boolean testUnit = false;
	private static MimeMessage message;

	public static int rc;

	private static void exit(int rc) {
		TxtToMail.rc  = rc;
		if(TxtToMail.testUnit == false) {
			System.exit(rc);
		}
	}
	/**
	 * The main function to start email sending
	 * @param args Arguments : -i, inputFilePath, -c, configFilePath
	 */
	public static void main(String[] args) {
		// Default null to handle no -o option
		String outputFilePath = null; // path to html output file
		String htmlOutputFilePath = null; // path to eml output file
		String inputFilePath; // path to text input file
		String configFilePath = null; // path to properties format file
		message=null;
		rc = 0;
		
		// Parsing Option, sea apache.commons.cli
		Options options = new Options();

		options.addOption("c", "config", true, "Properties file");
		options.addOption("i", "input", true, "Txt input file");
		options.addOption("o", "output", true, "Txt output file");
		options.addOption("s", "send", false, "Send the email from input file template");
		options.addOption("h", "html", true, "Simulate sending by creating an HTML file");
		
		options.addOption("help", false, "Print help");

		CommandLineParser parser = new org.apache.commons.cli.DefaultParser();
		try {
			CommandLine cmd = parser.parse(options, args, true);
			// Help option => display help and exit != 0
			if (cmd.hasOption("help")) {
				help(options);
				exit(1);
			}

			// Input option is mandatory
			if (!cmd.hasOption("input") && !cmd.hasOption("send")) {
				throw new ParseException(
						"input file path parameters is required");
			}
			inputFilePath = cmd.getOptionValue("input");

			// Config option is optional
			if (cmd.hasOption("config")) {
				configFilePath = cmd.getOptionValue("config");
			}
			

			// output option is optional
			if (cmd.hasOption("output")) {
				outputFilePath = cmd.getOptionValue("output");
			}
			
			// output option is optional
			if (cmd.hasOption("html")) {
				htmlOutputFilePath = cmd.getOptionValue("html");
			}
			
			TemplateProcessor p = new TemplateProcessorImpl(inputFilePath,
					configFilePath, outputFilePath, htmlOutputFilePath);
			
			List<String> listArgs = cmd.getArgList();
			Email email = p.loadEmailFromInput();
			boolean hasTo = false;
			boolean hasSubject = false;
			for(int n = 0; n < listArgs.size(); n++) {
				if(listArgs.size() >= n + 2) {
					String tag = listArgs.get(n).substring(listArgs.get(n).lastIndexOf("-")+1);
					String value = listArgs.get(n+1);
					email.convert(tag + ":" + value);
					if(tag.contentEquals(EmailSender.getProperty("toTag"))) {
						hasTo = true;
					}
					if(tag.contentEquals(EmailSender.getProperty("subjectTag"))) {
						hasSubject = true;
					}
				} else {
					throw new ParseException("No value for args : " + listArgs.get(n));
				}
				// switch value
				n = n+1;
			}
			// output option is optional
			if (cmd.hasOption("html")) {
				p.saveHtml(email);
			}
			
			if(cmd.hasOption("send")) {
				if((!hasTo || !hasSubject) && !cmd.hasOption("input"))
				{
					throw new ParseException("if you want send email please specify value for : \n* " + EmailSender.getProperty("toTag") + "\n* " + EmailSender.getProperty("subjectTag"));
				}
				// With option start a new processor from input file to mail
				p.send(email);
				message = p.getMessage();
			} else {
				p.saveToInput(email);
			}
		} catch (ParseException e) {
			// Handle ParsingException
			System.err.println("Parsing failed.  Reason: " + e.getMessage());
			help(options);
			exit(1);
		} catch (TemplateProcessingException e) {
			// If TemplateProcessor throw and Exception exit != 1
			exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Error while write output", e);
			exit(1);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			logger.error("Error in message", e);
			exit(1);
		}
	}

	/**
	 * Show Help on display
	 * @param options
	 */
	private static void help(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("TxtToMail", options);
	}

	public static MimeMessage getMimeMessage() {
		return message;
	}
}