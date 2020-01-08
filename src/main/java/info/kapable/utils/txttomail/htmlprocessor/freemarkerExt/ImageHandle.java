package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;
import info.kapable.utils.txttomail.EmailSender;
import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.exception.TemplateProcessingException;

public class ImageHandle implements TemplateMethodModelEx {

	public static final int EMBEDED_MODE = 0;
	public static final int ATTACHED_MODE = 1;
	private Email email;
	private int mode = EMBEDED_MODE;

	public ImageHandle(Email email, int mode) {
		super();
		this.email = email;
		this.mode = mode;
	}

	public ImageHandle(int mode) {
		super();
		this.mode = mode;
	}

	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		String text = (String) DeepUnwrap.unwrap((TemplateModel) args.get(0));
		return this.exec(text);
	}

	public Object exec(String text) {
		return returnImageHash(text);
	}

	public String returnImageHash(String text) {
		if (this.mode == EMBEDED_MODE) {
			try {
				return returnEmbedding(text);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (this.mode == ATTACHED_MODE) {
			return returnAttached(text);
		}
		return returnAttached(text);
	}

	public String returnAttached(String text) {
		String key = email.addAttachement(text);
		return "cid:" + key;
	}

	public String returnEmbedding(String fileName) throws IOException {
		if (fileName.contains("data:")) {
			return fileName;
		}
		File file = new File(fileName);
		if (!file.exists()) {
			try {
				file = new File(EmailSender.getProperty("template.base.path") + "/" + fileName);
			} catch (TemplateProcessingException e) {
				throw new IOException(e.getMessage());
			}
		}
		byte[] bytes = loadFile(file);
		Base64 base64 = new Base64();
		byte[] encoded = base64.encode(bytes);
		String encodedString = new String(encoded);
		String mimeType;
		mimeType = getMimeType(file);
		return "data:" + mimeType + ";base64," + encodedString;
	}

	public static String getMimeType(File file) {
		String mime = "";
		String name = file.getName();
		String ext = name.substring(name.length() - 3);
		if(ext.contentEquals("gif")) {
			mime = "image/gif";
		} else if (ext.contentEquals("jpg")) {
			mime = "image/jpeg";
		} else if (ext.contentEquals("png")) {
			mime = "image/png";
		} else if (ext.contentEquals("svg")) {
			mime = "image/svg+xml";
		} else {
			mime = "image/png";
		}
		return mime;
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		is.close();
		return bytes;
	}
}
