package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import static org.junit.Assert.*;
import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.ImageMethod;

import org.junit.Test;

public class ImageMethodTest {

	public final static String image_path = "src/main/resources/templates/Logo.png";
	@Test
	public void test() {
		ImageMethod i = new ImageMethod();
		String href = (String) i.exec(image_path);
		String contentId = href.substring(href.indexOf(":") + 1);
		assertTrue(contentId != null);
		assertTrue(contentId.length() > 0);
		Email email = Email.getEmail();
		assertTrue(email.getAttachements().size() > 0);
		String email_image_path = email.getAttachements().get(contentId);
		assertTrue(email_image_path != null);
		assertTrue(email_image_path.equals(image_path));
	}

}
