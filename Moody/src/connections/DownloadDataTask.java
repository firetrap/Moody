package connections;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.htmlcleaner.XPatherException;
import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import connections.XMLparser.Key;

//receives a String[] where params[0] it's an url, params[1] its an string required to parse, params[2] its an string to the required method above
public class DownloadDataTask extends AsyncTask<String, Void, String> {
	@Override
	protected String doInBackground(String... params) {

		try {
			return loadFromNetwork(params[0], params[1], params[2]);
		} catch (IOException e) {
			return e.getMessage();
		} catch (XmlPullParserException e) {
			return e.getMessage();
		}
	}

	private String loadFromNetwork(String urlString, String parserString,
			String methodParams) throws XmlPullParserException, IOException {

		if (methodParams.equalsIgnoreCase("xml")) {
			InputStream stream = null;
			XMLparser moodyXmlParser = new XMLparser();

			List<Key> keys = null;

			try {
				stream = downloadUrl(urlString);
				keys = moodyXmlParser.parse(stream);
				// Makes sure that the InputStream is closed after the app is
				// finished using it.
			} finally {
				if (stream != null) {
					stream.close();
				}
			}

			for (Key entry : keys) {
				if (entry.keyName.equals(parserString)) {

					return entry.value;

				}

			}
		}
		if (methodParams.equalsIgnoreCase("html")) {
			InputStream stream = null;
			HTMLparser parser = new HTMLparser();

			try {
				stream = downloadUrl(urlString);
				return parser.getSiteStats(stream);
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (urlString.equals(null) && parserString.equals(null)
				&& methodParams.equals(null)) {
//			InputStream stream = null;
//			XMLparser moodyXmlParser = new XMLparser();
//
//			List<Key> keys = null;
//			HashMap<String, String> xmlList = new HashMap<String, String>();
//			try {
//				stream = downloadUrl(urlString);
//				keys = moodyXmlParser.parse(stream);
//				// Makes sure that the InputStream is closed after the app is
//				// finished using it.
//			} finally {
//				if (stream != null) {
//					stream.close();
//				}
//			}
//
//			for (Key entry : keys) {
//				if (entry.keyName.equals(parserString)) {
//					xmlList.put(entry.keyName, entry.value);
//
//					return xmlList.;
//
//				}
//
//			}
//
		}

		return null;
	}

	// Given a string representation of a URL, sets up a connection and gets
	// an input stream.
	private InputStream downloadUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(10000 /* milliseconds */);
		conn.setConnectTimeout(15000 /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		// Starts the query
		conn.connect();
		InputStream stream = conn.getInputStream();
		return stream;
	}

	@Override
	protected void onPostExecute(String result) {

	}

}
