package info.kapable.utils.txttomail.exception;

import static org.junit.Assert.*;

import org.junit.Test;

public class TemplateProcessingExceptionTest {

	@Test
	public void test() {
		Exception e1 = new TemplateProcessingException("ERROR");
		assertTrue(e1.getMessage().contentEquals("ERROR"));
		
		Exception e2 = new TemplateProcessingException(new Exception("ERROR"));
		assertTrue(e2.getMessage().contentEquals("ERROR"));
	}

}
