package connections;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.content.Context;
import android.os.Environment;

public class ExternalFiles {

	/**
	 * @param fileUrl
	 * @param fileName
	 * @return
	 */
	public String getParseFile(Context context, String fileUrl, String fileName) {

		getFile(context, fileUrl, fileName);
		Document doc;
		String src;
		try {
			File file = new File(context.getCacheDir(), "/" + fileName);
			doc = Jsoup.parse(file, "UTF-8");
			if (doc.outerHtml().contains("src")) {
				Elements element = doc.select("[src]");
				src = element.attr("src");
				if (src.contains("youtube")) {
					src = src.split("\\?")[0].replace("v/", "watch?v=");
					return src;
				}
			}
			return doc.outerHtml();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * @param fileUrl
	 * @param fileName
	 */
	private void getFile(Context context, String fileUrl, String fileName) {
		// get URL content
		try {
			URL url = new URL(fileUrl);
			URLConnection conn = url.openConnection();

			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));

			String inputLine;

			// save to this filename
			File file = new File(context.getCacheDir() + "/" + fileName);

			if (!file.exists()) {
				file.createNewFile();
			}

			// use FileWriter to write file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine);
			}

			bw.close();
			br.close();

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
