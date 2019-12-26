package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import static org.junit.Assert.assertTrue;

import info.kapable.utils.txttomail.domain.Email;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.ImageHandle;
import junit.framework.Assert;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

public class ImageHandleTest {

	@Test
	public void testEmbeded() {
		ImageHandle h = new ImageHandle(ImageHandle.EMBEDED_MODE);
		String result = (String) h.exec("src/test/resources/notification-badge-fill.svg");
		System.out.println(result);
		assertTrue("data:image/svg+xml;base64,PHN2ZyB4bWxucz0iaHR0cDovL3d3dy53My5vcmcvMjAwMC9zdmciIHZpZXdCb3g9IjAgMCAyNCAyNCI+CiAgICA8Zz4KICAgICAgICA8cGF0aCBmaWxsPSJub25lIiBkPSJNMCAwaDI0djI0SDB6Ii8+CiAgICAgICAgPHBhdGggZD0iTTEzLjM0MSA0QTYgNiAwIDAgMCAyMSAxMS42NTlWMjFhMSAxIDAgMCAxLTEgMUg0YTEgMSAwIDAgMS0xLTFWNWExIDEgMCAwIDEgMS0xaDkuMzQxek0xOSAxMGE0IDQgMCAxIDEgMC04IDQgNCAwIDAgMSAwIDh6Ii8+CiAgICA8L2c+Cjwvc3ZnPgo=".contentEquals(result));
	}

	@Test
	public void testAttached() {
		Email e = new Email();
		ImageHandle h = new ImageHandle(e, ImageHandle.ATTACHED_MODE);
		String result = (String) h.exec("src/test/resources/notification-badge-fill.svg");
		System.out.println(result);
		assertTrue(result.startsWith("cid:"));
		assertTrue(new Integer(1).equals(e.getAttachements().size()));
	}
}
