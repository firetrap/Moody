/**
 * 
 */
package com.example.MoodyConnections;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Fábio
 * 
 */
public class Connections extends AsyncTask<URL, Integer, Long> {


	/**
	 * 
	 */

	// Construtor da class que recebe a actividade por argumento neste caso para
	// o toast
	public Connections() {
		// TODO Auto-generated constructor stub
		

	}

	private void readStream(InputStream in) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(in));
			String line = "";
			while ((line = reader.readLine()) != null) {
				Log.d("Read", line);

			}

		} catch (IOException e) {
			Log.d("Nread", e.toString());
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

	protected void onProgressUpdate(Integer... progress) {
		// setProgressPercent(progress[0]);
	}

	protected void onPostExecute(Long result) {
//		Toast.makeText( null, "Connectado com Sucesso",Toast.LENGTH_LONG).show();
	}

	
	protected Long doInBackground(URL... urls) {
		try {
			// URL url = new URL("http://moodle.ests.ips.pt/");
			HttpURLConnection con = (HttpURLConnection) urls[0].openConnection();
			readStream(con.getInputStream());
			Log.d("Connected", urls[0].toString());

		} catch (Exception e) {
			Log.d("Nconnected", e.toString());
			e.printStackTrace();

		}
		return null;
	}

}
