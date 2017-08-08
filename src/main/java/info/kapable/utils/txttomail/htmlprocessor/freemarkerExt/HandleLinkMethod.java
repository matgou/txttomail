package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * This class is using in freemarker template to escape basic text
 * @author MGOULIN
 */
public class HandleLinkMethod implements TemplateMethodModelEx {
	private static final String URL_REGEX = "((\\\\\\\\|(https?|ftp|file)://)[-a-zA-Z0-9$+&@#/%\\\\?=~_|!:,.;]*[-a-zA-Z0-9+&@#\\$/%=~_|])";

	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		String text = (String) DeepUnwrap.unwrap((TemplateModel) args.get(0));
		return this.exec(text);
	}

	/**
	 * Format the BODY (text parameters) to escape special char and transform link using regex
	 * @param text the BODY
	 * @return the formated text
	 */
	public String exec(String text) {
		return StringEscapeUtils
				.escapeHtml(text)
				.replace("&lt;", "<").replace("&gt;", ">")
				.replaceAll(URL_REGEX,"<a style=\"color: #6aa517; text-decoration: underline;\" href=\"$1\">$1</a>");
	}
}