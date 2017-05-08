package info.kapable.utils.txttomail.textprocessor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Test;

import info.kapable.utils.txttomail.exception.TemplateProcessingException;

public class CsvToTextProcessorTest {

	@Test
	public void test() {
		this.writeCSV();
		CsvToTextProcessor c = new CsvToTextProcessor();
		Writer out = new StringWriter();
		try {
			c.process("testUnit.csv", out);
		} catch (TemplateProcessingException e) {
			assertTrue(false);
		}
		String outString = out.toString();
		assertTrue(outString.contains("foo2"));
	}

	private void writeCSV() {
		try {
			File csv = new File("testUnit.csv");
			if(csv.exists()) {
				csv.delete();
			}
			FileWriter f = new FileWriter(new File("testUnit.csv"));
			f.write("nom, prenom\n");
			f.write("foo, bar\n");
			f.write("foo2, bar2\n");
			f.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
