package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import info.kapable.utils.txttomail.domain.Email;

import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class ImageHandle implements TemplateMethodModelEx {

	private Email email;
	
	public ImageHandle(Email email) {
		super();
		this.email = email;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List args) throws TemplateModelException {
		String text = (String) DeepUnwrap.unwrap((TemplateModel) args.get(0));
		return this.exec(text);
	}

	private Object exec(String text) {
		String key = email.addAttachement(text);
		return "cid:" + key;
	}
}
