package info.kapable.utils.txttomail.htmlprocessor.freemarkerExt;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used in freemarker template to remplace a csv path to an array
 * @author MGOULIN
 */
public class LoadCSVMethod implements TemplateMethodModelEx {
	/**	
	 * The separator of CSV
	 */
	private static final String cvsSplitBy = ",";

	@SuppressWarnings("rawtypes")
	public Object exec(List args) throws TemplateModelException {
		String csvFile = (String) DeepUnwrap
				.unwrap((TemplateModel) args.get(0));
		
		return exec(csvFile);
	}

	public Object exec(String csvFile) {
		List<String[]> values = new ArrayList<String[]>();

		File file = new File(csvFile);
		if (!file.exists()) {
			return csvFile;
		}
		try {
			// open file
			BufferedReader br = new BufferedReader(new FileReader(csvFile));
			String lineTab;
			// for each line
			while ((lineTab = br.readLine()) != null) {
				// load array
				String[] tab = lineTab.split(cvsSplitBy);
				values.add(tab);
			}
			br.close(); // close Reader
			// return array
		} catch (IOException e) {
			e.printStackTrace();
		}
		return values;
	}
}