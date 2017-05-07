package info.kapable.utils.txttomail.htmlprocessor;

import info.kapable.utils.txttomail.EmailSender;
import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * This class is tools to process a template with freemarker
 * @author MGOULIN
 *
 */
public class FreemarkerHTMLProcessor extends HTMLProcessor
{
	/**
	 * The template used to process
	 */
	protected Template template;
	/**
	 * the metadata
	 */
	private Map<String, String> data;
	/**
	 * Configuration
	 */
	protected Configuration cfg;

	/**
	 * Constructor of object
	 * @param templatePath the path (relative from template.base.path) to the template
	 * @param data metadata array
	 * @throws TemplateProcessingException on error throw
	 */
	public FreemarkerHTMLProcessor(String templatePath, Map<String, String> data) throws TemplateProcessingException
	{
		this.cfg = new Configuration(Configuration.VERSION_2_3_23);
		this.cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		this.cfg.setDefaultEncoding("UTF-8");
		try {
			TemplateLoader[] tl;
			ClassTemplateLoader ctl = new ClassTemplateLoader(getClass(), "/templates");
			String templateBasePath = EmailSender.getConfig().getProperty("template.base.path");
			// if extra directory for template
			if(templateBasePath != null) {
				FileTemplateLoader ftl1 = new FileTemplateLoader(new File(templateBasePath));
				tl = new TemplateLoader[] { ctl, ftl1 };
			} else {
				tl = new TemplateLoader[] { ctl };
			}
			MultiTemplateLoader mtl = new MultiTemplateLoader(tl);

			this.cfg.setTemplateLoader(mtl);
			if (!templatePath.equals("")) {
				this.template = this.cfg.getTemplate(templatePath);
			}
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
		this.data = data;
	}

	@Override
	public void process(String string, Writer out)
			throws TemplateProcessingException
	{
		this.data.put("line", string);
		try {
			this.template.process(this.data, out);
		} catch (TemplateException e) {
			throw new TemplateProcessingException(e);
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
	}
}