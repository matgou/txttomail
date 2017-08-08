package info.kapable.utils.txttomail.textprocessor;

/**
 * A Builder to return textProcessor from tag
 * @author MGOULIN
 *
 */
public class TextProcessorBuilder {
	public static TextProcessor defaultProcessor;

	/**
	 * return the default processor Singleton
	 * @return the TextProcessor to use by default
	 */
	public static TextProcessor getDefaultProcessor() {
		if (defaultProcessor == null) {
			defaultProcessor = new EchoTextProcessor();
		}
		return defaultProcessor;
	}

	/**
	 * Return the appropriate processor from a tag
	 * @param tag key to determine the appropriate processor
	 * @return the TextProcessor
	 */
	public static TextProcessor getProcessor(String tag) {
		if (tag.equals("csvToTextProcessor")) {
			return new CsvToTextProcessor();
		}
		return getDefaultProcessor();
	}
}