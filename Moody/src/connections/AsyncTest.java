package connections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

//receives a String[] where params[0] it's an url, params[1] its an string required to parse, params[2] its an string to the required method above
public class AsyncTest extends AsyncTask<String, Void, JSONObject> {

	JSONObject jObj = null;

	@Override
	protected JSONObject doInBackground(String... params) {

		try {
			return loadFromNetwork(params[0], params[1]);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jObj;

	}

	private JSONObject loadFromNetwork(String urlString, String methodParams)
			throws IOException {

		InputStream inputStream = null;
		String json = null;

		try {
			inputStream = downloadUrl(urlString);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					inputStream, "UTF-8"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			json = sb.toString();

			try {

				// If json is an Array it returns an JSONArray inside JSONobject
				if (json.startsWith("[")) {
					jObj = new JSONObject();
					jObj.put("array", new JSONArray(json));
				} 
				
				else {
					jObj = new JSONObject(json);
				}

			} catch (JSONException e) {
				Log.e("JSON Parser", "Error parsing data " + e.toString());
			}

			// return JSON String
			return jObj;

		}

		finally {
			try {
				if (inputStream != null)
					inputStream.close();
			} catch (Exception e) {
			}
		}

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
	protected void onPostExecute(JSONObject json) {

	}

	/**
	 * Returns a Drawable object containing the image located at
	 * 'imageWebAddress' if successful, and null otherwise. (Pre:
	 * 'imageWebAddress' is non-null and non-empty; method should not be called
	 * from the main/ui thread.)
	 */
	public static Drawable createDrawableFromUrl(String imageWebAddress) {
		Drawable drawable = null;

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			InputStream inputStream = new URL(imageWebAddress).openStream();
			drawable = Drawable.createFromStream(inputStream, null);
			inputStream.close();
		} catch (MalformedURLException ex) {
		} catch (IOException ex) {
		}

		return drawable;
	}

}
