package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import static org.junit.Assert.assertTrue;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.ImageHandle;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

public class ImageHandleTest {

	@Test
	public void test() {
		ImageHandle h = new ImageHandle();
		String result = (String) h.exec("src/main/resources/templates/Logo.png");
		System.out.println(result);
		assertTrue(true);
	}
}
