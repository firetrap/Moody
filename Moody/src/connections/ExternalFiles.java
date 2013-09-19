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
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import android.content.Context;
import android.text.Html;

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
			} else {

			}
			String out = new PlainText().getPlainText(doc.body());
			return out;

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

	private static class PlainText {

		/**
		 * Format an Element to plain-text
		 * 
		 * @param element
		 *            the root element to format
		 * @return formatted text
		 */
		public String getPlainText(Element element) {
			FormattingVisitor formatter = new FormattingVisitor();
			NodeTraversor traversor = new NodeTraversor(formatter);
			traversor.traverse(element); // walk the DOM, and call .head() and
											// .tail() for each node

			return formatter.toString();
		}

		// the formatting rules, implemented in a breadth-first DOM traverse
		private class FormattingVisitor implements NodeVisitor {
			private static final int maxWidth = 80;
			private int width = 0;
			private StringBuilder accum = new StringBuilder(); // holds the
																// accumulated
																// text

			// hit when the node is first seen
			public void head(Node node, int depth) {
				String name = node.nodeName();
				if (node instanceof TextNode)
					append(((TextNode) node).text()); // TextNodes carry all
														// user-readable text in
														// the
														// DOM.
				else if (name.equals("li"))
					append("\n " + Html.fromHtml(" &#8226"));
			}

			// hit when all of the node's children (if any) have been visited
			public void tail(Node node, int depth) {
				String name = node.nodeName();
				if (name.equals("br"))
					append("\n");
				else if (StringUtil.in(name, "p", "h1", "h2", "h3", "h4", "h5"))
					append("\n\n");
				else if (name.equals("a"))
					append(String.format(" <%s>", node.absUrl("href")));
			}

			// appends text to the string builder with a simple word wrap method
			private void append(String text) {
				if (text.startsWith("\n"))
					width = 0; // reset counter if starts with a newline. only
								// from
								// formats above, not in natural text
				if (text.equals(" ")
						&& (accum.length() == 0 || StringUtil.in(
								accum.substring(accum.length() - 1), " ", "\n")))
					return; // don't accumulate long runs of empty spaces

				if (text.length() + width > maxWidth) { // won't fit, needs to
														// wrap
					String words[] = text.split("\\s+");
					for (int i = 0; i < words.length; i++) {
						String word = words[i];
						boolean last = i == words.length - 1;
						if (!last) // insert a space if not the last word
							word = word + " ";
						if (word.length() + width > maxWidth) { // wrap and
																// reset
																// counter
							accum.append("\n").append(word);
							width = word.length();
						} else {
							accum.append(word);
							width += word.length();
						}
					}
				} else { // fits as is, without need to wrap text
					accum.append(text);
					width += text.length();
				}
			}

			public String toString() {

				return accum.toString().replaceAll("[<>]", "");
			}
		}
	}

}
