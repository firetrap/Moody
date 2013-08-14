package connections;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;

import org.htmlcleaner.XPatherException;
import org.xmlpull.v1.XmlPullParserException;

import android.R.integer;
import android.os.AsyncTask;
import connections.XMLparser.Key;

//receives a String[] where params[0] it's an url, params[1] its an string required to parse, params[2] its an string to the required method above
public class DownloadDataTask extends
		AsyncTask<String, Void, HashMap<String, String>> {

	HashMap<String, String> xmlList = new HashMap<String, String>();

	@Override
	protected HashMap<String, String> doInBackground(String... params) {

		try {
			return loadFromNetwork(params[0], params[1]);
		} catch (IOException e) {
			xmlList.put("Error", e.getMessage().toString());
			return xmlList;
		} catch (XmlPullParserException e) {
			xmlList.put("Error", e.getMessage().toString());
			return xmlList;
		}
	}

	private HashMap<String, String> loadFromNetwork(String urlString,
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
				for (int j = 1; j < keys.size(); j++) {
					if (!xmlList.containsKey(entry.keyName + j)) {
			
						xmlList.put(entry.keyName + Integer.toString(j), entry.value);

						break;
					}
				}
			}

			return xmlList;

		}

		if (methodParams.equalsIgnoreCase("html")) {
			InputStream stream = null;
			HTMLparser parser = new HTMLparser();

			try {
				stream = downloadUrl(urlString);
				xmlList.put("HTML", parser.getSiteStats(stream));
				return xmlList;
			} catch (XPatherException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

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
	protected void onPostExecute(HashMap<String, String> result) {

	}

}
