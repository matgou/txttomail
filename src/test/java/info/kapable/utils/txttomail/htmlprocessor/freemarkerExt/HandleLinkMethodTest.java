package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import static org.junit.Assert.assertTrue;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.HandleLinkMethod;

import org.apache.commons.lang.StringEscapeUtils;
import org.junit.Test;

public class HandleLinkMethodTest {

	@Test
	public void test() {
		HandleLinkMethod h = new HandleLinkMethod();
		String eacute = StringEscapeUtils.unescapeHtml("&eacute;");
		String result = (String) h.exec(eacute + " http://localhost.localdoamine");
		// Special char handling
		assertTrue(result.contains("&eacute;"));
		// Link handling
		assertTrue(result.contains("href=\"http://localhost.localdoamine"));
		assertTrue(result.contains("<a"));
		

		result = (String) h.exec("<b>" + eacute + " http://localhost.localdoamine</b>");
		assertTrue(result.contains("</b>"));

	}
}
