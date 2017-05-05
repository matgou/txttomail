package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import static org.junit.Assert.*;
import info.kapable.utils.txttomail.htmlprocessor.freemarkerExt.LoadCSVMethod;

import java.util.List;

import org.junit.Test;

public class LoadCSVMethodTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() {
		LoadCSVMethod csv = new LoadCSVMethod();
		List<String[]> result = (List<String[]>) csv.exec("src/main/resources/tab1.csv");
		assertTrue(result.size() > 1);
		assertTrue(result.get(0)[0].length() > 0);
	}

}
