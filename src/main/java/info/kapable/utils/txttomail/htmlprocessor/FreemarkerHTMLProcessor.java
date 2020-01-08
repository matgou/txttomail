package info.kapable.utils.txttomail.htmlprocessor;

import info.kapable.utils.txttomail.EmailSender;
import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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
	private Map<String, Object> data;
	/**
	 * Configuration
	 */
	protected Configuration cfg;

	/**
	 * Constructor of object
	 * @param templatePath the path (relative from template.base.path) to the template
	 * @param email the email to add some attachment if needed (for img)
	 * @param data metadata array
	 * @throws TemplateProcessingException on error throw
	 */
	public FreemarkerHTMLProcessor(String templatePath, Email email, Map<String, String> data) throws TemplateProcessingException
	{
		this.data = new HashMap<String, Object>();
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
		this.data.put("headers", data);
		this.data.put("email", email);
	}

	@Override
	public void process(String string, Map<String, Object> mapExtrat, Writer out)
			throws TemplateProcessingException
	{
		Map<String, Object> map = mapExtrat;
		Iterator<Entry<String, Object>> it = this.data.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Object> o = it.next();
			map.put(o.getKey(), o.getValue());
		}
		map.put("line", string);
		try {
			this.template.process(map, out);
		} catch (TemplateException e) {
			throw new TemplateProcessingException(e);
		} catch (IOException e) {
			throw new TemplateProcessingException(e);
		}
	}
}
