/**
 * 
 */
package connections;

import java.io.IOException;
import java.io.InputStream;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 * @author firetrap
 * 
 */

/**
 * Inicialize the requirements for getSiteStats the required token from the
 * site.
 */
public class HTMLparser {

	TagNode rootNode;
	String stats = "";

	/**
	 * 
	 * Get the required token from the site.
	 */
	public String getSiteStats(InputStream stream) throws IOException,
			XPatherException {
		String stats = "";

		// config cleaner properties

		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		props.setAllowHtmlInsideAttributes(false);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		TagNode root = htmlCleaner.clean(stream);

		// query XPath
		Object[] statsNode = root.evaluateXPath("");
		// process data if found any node
		if (statsNode.length > 0) {
			// I already know there's only one node, so pick index at 0.
			TagNode resultNode = (TagNode) statsNode[0];
			// get text data from HTML node
			stats = resultNode.getText().toString();
		}

		return stats;
	}
}
