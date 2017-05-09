package info.kapable.utils.txttomail.htmlprocessor;

import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.Writer;
import java.util.Map;

/**
 * Abstract class to format BODY to HTML 
 * @author MGOULIN
 */
public abstract class HTMLProcessor
{
	/**
	 * Function to process paramString to writer
	 * @param paramString the body part
	 * @param mapExtrat extra parameters to passe to processor
	 * @param paramWriter the writer
	 * @throws TemplateProcessingException on error throw TemplateProcessingException
	 */
	public abstract void process(String paramString, Map<String, Object> mapExtrat, Writer paramWriter)
			throws TemplateProcessingException;
}