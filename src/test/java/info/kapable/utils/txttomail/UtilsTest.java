package info.kapable.utils.txttomail;

import static org.junit.Assert.*;
import info.kapable.utils.txttomail.Utils;

import java.io.File;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void test() {
		assertTrue(Utils.getTag("TAG:BODY").equals("TAG"));
		assertTrue(Utils.getTag("BODY").equals("RAW"));
		assertTrue(Utils.getValue("TAG:BODY").equals("BODY"));
		assertTrue(Utils.getValue("TAG: BODY").equals("BODY"));
		assertTrue(Utils.basename(new File("/tmp/test.txt")).equals("test.txt"));
	}

}
