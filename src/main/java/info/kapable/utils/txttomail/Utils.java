package info.kapable.utils.txttomail;

import java.io.File;
import info.kapable.utils.txttomail.other.CoverageIgnore;
/**
 * This class for static Util method <br>
 * 
 * @author Mathieu GOULIN
 *
 */
public final class Utils {
	
	/**
	  * A Constant to determine separator between TAG and BODY when parsing line
	  */
	private static final String TAG_SEPARATOR = ":";
	
	/**
	 * private constructor to prevent class instantiation
	 */
	@CoverageIgnore
	private Utils () {
    }

	/**
	 * From line return the TAG part
	 * @param line the line to parse
	 * @return the TAG part (on left of TAG_SEPARATOR)
	 */
	public static String getTag(String line) {
		int i = line.indexOf(TAG_SEPARATOR);
		if(i < 0) {
			return "RAW";
		}
		return line.substring(0, i);
	}

	/**
	 * From line return the BODY part
	 * @param line the line to parse
	 * @return the BODY Part of line (on right of TAG_SEPARATOR) without space at start
	 */
	public static String getValue(String line) {
		return String.format("%s",
				new Object[] { line.substring(line.indexOf(TAG_SEPARATOR) + 1)
						.replaceAll("^\\s", "") });
	}

	/**
	 * From file return the basename of file
	 * @param f a file 
	 * @return the name of file
	 */
	public static String basename(File f) {
		return f.getName();
	}
}