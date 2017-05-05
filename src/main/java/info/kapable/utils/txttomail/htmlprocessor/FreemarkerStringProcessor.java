package info.kapable.utils.txttomail.htmlprocessor;

import freemarker.template.Template;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;

/**
 * A simple processor to format a string template with data
 * @author MGOULIN
 */
public class FreemarkerStringProcessor extends FreemarkerHTMLProcessor {
	public FreemarkerStringProcessor(String templateStr,
			Map<String, String> data) throws TemplateProcessingException {
		super("", data);
		try {
			this.template = new Template("name", new StringReader(templateStr),
					this.cfg);
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
	}
}