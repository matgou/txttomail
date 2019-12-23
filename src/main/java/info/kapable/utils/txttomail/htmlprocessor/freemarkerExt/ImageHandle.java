package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import info.kapable.utils.txttomail.domain.Email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

public class ImageHandle implements TemplateMethodModelEx {

	public static final int EMBEDED_MODE = 0;
	public static final int ATTACHED_MODE = 1;
	private Email email;
	private int mode = EMBEDED_MODE;
	
	public ImageHandle(Email email) {
		super();
		this.email = email;
	}

	public ImageHandle() {
		super();
	}

	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		String text = (String) DeepUnwrap.unwrap((TemplateModel) args.get(0));
		return this.exec(text);
	}

	public Object exec(String text) {
		if(this.mode == EMBEDED_MODE) {
			try {
				return returnEmbedding(text);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(this.mode == ATTACHED_MODE) {
			return returnAttached(text);
		}
		return returnAttached(text);
	}

	public String returnAttached(String text) {
		String key = email.addAttachement(text);
		return "cid:" + key;
	}
	
	public String returnEmbedding(String fileName)
			throws IOException {
		if(fileName.contains("data:image/png;base64")) {
			return fileName;
		}
		File file = new File(fileName);
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.getEncoder().encode(bytes);
		String encodedString = new String(encoded);

		return "data:image/png;base64," + encodedString;
	}

	private static byte[] loadFile(File file) throws IOException {
	    InputStream is = new FileInputStream(file);

	    long length = file.length();
	    if (length > Integer.MAX_VALUE) {
	        // File is too large
	    }
	    byte[] bytes = new byte[(int)length];
	    
	    int offset = 0;
	    int numRead = 0;
	    while (offset < bytes.length
	           && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	        offset += numRead;
	    }

	    if (offset < bytes.length) {
	        throw new IOException("Could not completely read file "+file.getName());
	    }

	    is.close();
	    return bytes;
	}
}
