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

	// Save the JSONObject in cacheDir
	public void storeJsonData(Context context, JSONObject jsonObject,
			String name) {

		try {

			File file = new File(context.getCacheDir(), "");
			FileOutputStream fileO = new FileOutputStream(file + "/" + name
					+ ".data");
			ObjectOutput out = new ObjectOutputStream(fileO);

			// As JSON object is not serializable so it will be converted to
			// string
			String jsonString = jsonObject.toString();
			out.writeObject(jsonString);
			out.close();
		} catch (FileNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}

	}

	// Load in an object
	public JSONObject getJsonData(Context context, String name) {

		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					new File(new File(context.getCacheDir(), "") + "/" + name
							+ ".data")));

			String jsonString = (String) in.readObject();
			JSONObject jsonObject = new JSONObject(jsonString);
			in.close();
			return jsonObject;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Save the course organized by id->name
	public void storeCourses(Context context,
			HashMap<String, String> organizedCourses, String name) {

		try {

			File file = new File(context.getCacheDir(), "");
			FileOutputStream fileO = new FileOutputStream(file + "/" + name
					+ ".data");
			ObjectOutput out = new ObjectOutputStream(fileO);

			out.writeObject(organizedCourses);
			out.close();
		} catch (FileNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}

	}

	// Load courses organized
	public HashMap<String, String> getCourses(Context context, String name) {

		try {
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(
					new File(new File(context.getCacheDir(), "") + "/" + name
							+ ".data")));

			@SuppressWarnings("unchecked")
			HashMap<String, String> courses = (HashMap<String, String>) in
					.readObject();
			in.close();
			return courses;

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

}
