package info.kapable.utils.txttomail.textprocessor;

import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.IOException;
import java.io.Writer;

/**
 * No format just print text to writer
 * 
 * @author MGOULIN
 */
public class EchoTextProcessor extends TextProcessor {
	@Override
	public void process(String line, Writer out)
			throws TemplateProcessingException {
		try {
			out.write(String.format("%s\n", new Object[] { line }));
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
	}
}