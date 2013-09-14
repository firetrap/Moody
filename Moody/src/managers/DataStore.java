package managers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class DataStore {

	// Load in an object
	public JSONObject getJsonData(Context context, String name) {
		final String filePath = "/" + name + ".data";
		try {

			final ObjectInputStream in = new ObjectInputStream(
					new FileInputStream(new File(new File(
							context.getCacheDir(), "") + filePath)));

			final String jsonString = (String) in.readObject();
			final JSONObject jsonObject = new JSONObject(jsonString);
			in.close();
			return jsonObject;

		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Save the JSONObject in cacheDir
	public void storeJsonData(Context context, JSONObject jsonObject,
			String name) {

		try {

			final File file = new File(context.getCacheDir(), "");
			final String filePath = "/" + name + ".data";
			final FileOutputStream fileO = new FileOutputStream(file + filePath);
			final ObjectOutput out = new ObjectOutputStream(fileO);

			// As JSON object is not serializable so it will be converted to
			// string
			final String jsonString = jsonObject.toString();
			out.writeObject(jsonString);
			out.close();
		} catch (final FileNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (final IOException e) {
			e.getMessage();
			e.printStackTrace();
		}

	}

	public void deleteCache(Context context) {
		try {

			if (context.getCacheDir().exists()) {

				File[] cacheFiles = context.getCacheDir().listFiles();

				if (cacheFiles.length > 0) {

					for (int i = 0; i < cacheFiles.length; i++)
						cacheFiles[i].delete();

				}
			}

		} catch (Exception e) {

		}
	}
}
