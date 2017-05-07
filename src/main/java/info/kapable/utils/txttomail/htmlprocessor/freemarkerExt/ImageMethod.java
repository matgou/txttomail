package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import info.kapable.utils.txttomail.domain.Email;

import java.io.File;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * This class is used in freemarker template to add image inside email
 * @author MGOULIN
 */
public class ImageMethod implements TemplateMethodModelEx {
	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		// Get file and test if file exist
		String filename = (String) DeepUnwrap.unwrap((TemplateModel) args
				.get(0));
		return exec(filename);
	}
	public Object exec(String filename)
	{
		File file = new File(filename);
		if (!file.exists()) {
			filename = Email.getEmail().getConfig()
					.getProperty("template.base.path")
					+ File.separator + filename;
		}

		// put file in attachment
		String key = Email.getEmail().addAttachement(filename);

		// return the contentId
		return "cid:" + key;
	}
}