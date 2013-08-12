package connections;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import connections.XMLParser.Key;

//receives a String[] where params[0] it's an url, params[1] its an string required to parse, params[2] its an string to the required method above
public class DownloadXmlTask extends AsyncTask<String, Void, String> {
	@Override
	protected String doInBackground(String... params) {

		try {

			return loadXmlFromNetwork(params[0], params[1], params[2]);
		} catch (IOException e) {
			return "xupa";
		} catch (XmlPullParserException e) {
			return e.getMessage();
		}
	}

	private String loadXmlFromNetwork(String urlString, String parserString,
			String methodParams) throws XmlPullParserException, IOException {

		if (methodParams.equalsIgnoreCase("xml")) {
			InputStream stream = null;
			XMLParser moodyXmlParser = new XMLParser();

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
