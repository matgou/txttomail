package info.kapable.utils.txttomail.textprocessor;

import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.Writer;

/**
 * A TextProcessor is a way to format BODY to Text
 * @author MGOULIN
 */
public abstract class TextProcessor {
	/**
	 * Method to write formated body to Writer
	 * @param paramString the non-formated BODY part
	 * @param paramWriter the writer
	 * @throws TemplateProcessingException if error durring processing
	 */
	public abstract void process(String paramString, Writer paramWriter)
			throws TemplateProcessingException;
}