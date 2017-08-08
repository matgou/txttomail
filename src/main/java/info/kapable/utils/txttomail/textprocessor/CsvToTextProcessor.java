package info.kapable.utils.txttomail.textprocessor;

import info.kapable.utils.txttomail.exception.TemplateProcessingException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

/**
 * Text processor to format CSV to Text : array with same size
 * 
 * @author MGOULIN
 *
 */
public class CsvToTextProcessor extends TextProcessor {
	/**
	 * the separator of CSV
	 */
	protected String cvsSplitBy = ",";

	@Override
	public void process(String line, Writer out) throws TemplateProcessingException {
		// the csv path
		String csvFile = line;
		String lineTab = "";
		// array to store column with
		List<Integer> maxLenght = new ArrayList<Integer>();
		// array to store value
		List<String[]> values = new ArrayList<String[]>();
		try {
			// Open file
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			// for each line
			while ((lineTab = br.readLine()) != null) {
				// transform row to java-array
				String[] tab = lineTab.split(this.cvsSplitBy);
				values.add(tab);
				// find max column size
				for (int i = 0; i < tab.length; i++) {
					int l = tab[i].length();
					try {
						if (l > ((Integer) maxLenght.get(i)).intValue()) {
							maxLenght.set(i, Integer.valueOf(l));
						}
					} catch (IndexOutOfBoundsException e) {
						// if first maxLenght array is empty 
						maxLenght.add(Integer.valueOf(l));
					}
				}
			}
			// close Reader
			br.close();
			for (String[] lineValue : values) {
				out.write("|");
				for (int i = 0; i < lineValue.length; i++) {
					out.write(String.format("%" + maxLenght.get(i) + "s|",
							new Object[] { lineValue[i] }));
				}
				out.write("\n");
			}
		} catch (IOException e) {
			// on open error
			e.printStackTrace();
		}
	}
}