/**
 * 
 */
package connections;

import java.net.URL;
import java.util.StringTokenizer;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import android.os.StrictMode;
import android.util.Log;

/**
 * @author firetrap
 * 
 */
public class Token {

	private String mToken;

	/**
	 * 
	 */
	public Token(String mToken) {
		this.mToken = mToken;
	}

	/**
	 * @param args
	 */
	public String getToken() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// decide output
		String value = "";
		String userToken = "";
		try {
			value = getSiteStats();
			StringTokenizer tokens = new StringTokenizer(value, "\"");

			do {
				userToken = tokens.nextToken();

			} while (userToken.length() != 32);

			Log.d("MoodyDebug", "aqui" + userToken);
			return userToken;
		} catch (Exception ex) {
			Log.d("MoodyDebug",
					"userToken failed in: Token()-> " + ex.toString());

		}
		return userToken;

	}

	/**
	 * 
	 * Get the required token from the site.
	 */
	public String getSiteStats() throws Exception {
		String stats = "";

		// config cleaner properties

		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		props.setAllowHtmlInsideAttributes(false);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		// Check if token contains the required http protocol.
		if (mToken.subSequence(0, 7).equals("http://")) {

		} else {
			mToken = "http://" + mToken;
		}
		Log.d("Check", mToken);

		// create URL object
		URL urlToken = new URL(mToken);
		// get HTML page root node
		TagNode root = htmlCleaner.clean(urlToken);

		// query XPath
		Object[] statsNode = root.evaluateXPath("");
		// process data if found any node
		if (statsNode.length > 0) {
			// I already know there's only one node, so pick index at 0.
			TagNode resultNode = (TagNode) statsNode[0];
			// get text data from HTML node
			stats = resultNode.getText().toString();
		}

		Log.d("MoodyDebug", stats);

		return stats;
	}

}
