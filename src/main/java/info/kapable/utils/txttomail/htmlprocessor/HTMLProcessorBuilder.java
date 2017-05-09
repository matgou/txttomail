package info.kapable.utils.txttomail.htmlprocessor;

import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.util.Map;
import java.util.Properties;

/**
 * Return a html processor by tag
 * 
 * @author MGOULIN
 *
 */
public class HTMLProcessorBuilder {
	public static HTMLProcessor getTemplateProcessor(String string,
			Email email, Properties config, Map<String, String> headers)
			throws TemplateProcessingException {
		if (config.getProperty(string + ".html.template") != null) {
			return new FreemarkerHTMLProcessor(config.getProperty(string
					+ ".html.template"), email, headers);
		}
		return new FreemarkerHTMLProcessor(
				config.getProperty("tag.RAW.html.template"), email, headers);
	}

	public static HTMLProcessor getStringProcessor(String subjectTemplate, Email email,
			Map<String, String> headers) throws TemplateProcessingException {
		return new FreemarkerStringProcessor(subjectTemplate, email, headers);
	}
}