package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import static org.junit.Assert.assertTrue;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.HandleLinkMethod;

import org.junit.Test;

public class HandleLinkMethodTest {

	@Test
	public void test() {
		HandleLinkMethod h = new HandleLinkMethod();

		String result = (String) h.exec("é http://localhost.localdoamine");
		// Special char handling
		assertTrue(result.contains("&eacute;"));
		// Link handling
		assertTrue(result.contains("href=\"http://localhost.localdoamine"));
		assertTrue(result.contains("<a"));
		

		result = (String) h.exec("<b>é http://localhost.localdoamine</b>");
		assertTrue(result.contains("</b>"));

	}
}
