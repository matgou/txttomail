package info.kapable.utils.txttomail;

import static org.junit.Assert.*;
import info.kapable.utils.txttomail.Utils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

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

	@Test
	public void testConstructorIsPrivate() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
	  Constructor<Utils> constructor = Utils.class.getDeclaredConstructor();
	  assertTrue(Modifier.isPrivate(constructor.getModifiers()));
	  constructor.setAccessible(true);
	  constructor.newInstance();
	}
}
