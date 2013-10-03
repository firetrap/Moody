package managers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import android.content.Context;

/**
 * @author firetrap
 * 
 */
public class ManDataStore {

	private static byte[] serialize(Object obj) throws IOException {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		ObjectOutputStream o = new ObjectOutputStream(b);
		o.writeObject(obj);
		return b.toByteArray();
	}

	private static Object deserialize(byte[] bytes) throws IOException,
			ClassNotFoundException {
		ByteArrayInputStream b = new ByteArrayInputStream(bytes);
		ObjectInputStream o = new ObjectInputStream(b);
		return o.readObject();
	}

	// Save the Object in cacheDir
	public void storeData(Context context, Object object, String fileName) {

		try {

			File file = new File(context.getCacheDir(), "");
			String filePath = "/" + fileName + ".data";
			FileOutputStream fileO = new FileOutputStream(file + filePath);
			ObjectOutput out = new ObjectOutputStream(fileO);

			out.writeObject(serialize(object));
			out.close();
		} catch (FileNotFoundException e) {
			e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
			e.getMessage();
			e.printStackTrace();
		}

	}

	// Load the object
	public Object getData(Context context, String fileName) {
		String filePath = "/" + fileName + ".data";
		try {

			File file = new File(context.getCacheDir(), "");
			File path = new File(file + filePath);
			FileInputStream fileI = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fileI);

			byte[] seriallizedObject = (byte[]) in.readObject();
			in.close();
			return deserialize(seriallizedObject);

		} catch (final IOException e) {
			e.printStackTrace();
		} catch (final ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
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
