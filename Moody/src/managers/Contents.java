package managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import model.EnumWebServices;
import model.MoodyConstants;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleUser;
import android.content.Context;
import android.content.res.Resources;
import android.text.Html;

import com.example.moody.R;

import connections.DataAsyncTask;

/**
 * @author firetrap
 * 
 *         This class return the requested data, the procedure is always tries
 *         to get data from cache if it doesn't exist it will download from
 *         server and store in cache
 * 
 */
public class Contents {

	// Session Manager Class
	Session session;
	DataStore data = new DataStore();
	Object getContent;

	// MOODLE DATA
	/**
	 * @param resources
	 * @param context
	 * @return MoodleUser
	 */
	public MoodleUser getUser(Resources resources, Context context) {
		session = new Session(context);
		String url = session.getValues(MoodyConstants.KEY_URL, null);
		String token = session.getValues(MoodyConstants.KEY_TOKEN, null);
		String userId = session.getValues(MoodyConstants.KEY_ID, null);
		try {

			String fileName = EnumWebServices.CORE_USER_GET_USERS_BY_ID.name()
					+ userId;

			if (isInCache(context, fileName)) {
				return (MoodleUser) data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_USER_GET_USERS_BY_ID, userId)
						.get();
				data.storeData(context, getContent, fileName);
				return (MoodleUser) getContent;
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * @param resources
	 * @param context
	 * @return MoodleCourse[]
	 */
	public MoodleCourse[] getUserCourses(Resources resources, Context context) {
		session = new Session(context);
		String url = session.getValues(MoodyConstants.KEY_URL, null);
		String token = session.getValues(MoodyConstants.KEY_TOKEN, null);
		String userId = session.getValues(MoodyConstants.KEY_ID, null);

		try {

			String fileName = EnumWebServices.CORE_ENROL_GET_USERS_COURSES
					.name() + userId;

			if (isInCache(context, fileName)) {
				return (MoodleCourse[]) data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_ENROL_GET_USERS_COURSES, userId)
						.get();
				data.storeData(context, getContent, fileName);
				return (MoodleCourse[]) getContent;
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * @param courseId
	 * @param resources
	 * @param context
	 * 
	 * @return All course contents, in another words returns an array with all
	 *         topics
	 * @return MoodleCourseContent[]
	 */
	public MoodleCourseContent[] getCourseContent(String courseId,
			Resources resources, Context context) {

		session = new Session(context);
		String url = session.getValues(MoodyConstants.KEY_URL, null);
		String token = session.getValues(MoodyConstants.KEY_TOKEN, null);

		try {

			String fileName = EnumWebServices.CORE_COURSE_GET_CONTENTS.name()
					+ courseId;

			if (isInCache(context, fileName)) {

				return (MoodleCourseContent[]) data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_COURSE_GET_CONTENTS, courseId)
						.get();
				data.storeData(context, getContent, fileName);
				return (MoodleCourseContent[]) getContent;
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * @param topicId
	 * @param courseContent
	 * 
	 * @return a single topic from a course
	 * @return MoodleCourseContent
	 */
	public MoodleCourseContent getTopic(Long topicId,
			MoodleCourseContent[] courseContent) {
		for (int j = 0; j < courseContent.length; j++) {
			if (courseContent[j].getId() == topicId) {
				return courseContent[j];
			}

		}
		return null;
	}

	// MOODLE SPECIFIC INDEX.HTML GET CONTENT
	/**
	 * @param context
	 * @param fileUrl
	 * @param fileName
	 * @return String
	 */
	public String parseFile(Context context, String fileUrl, String fileName) {
		Document doc;
		String src;

		if (!isInCache(context, fileName)) {
			getFile(context, fileUrl, fileName);
		}

		doc = Jsoup.parse((String) new DataStore().getData(context, fileName),
				"UTF-8");
		if (!(doc.outerHtml().contains("src"))) {

		} else {
			Elements element = doc.select("[src]");
			src = element.attr("src");
			if (src.contains("youtube")) {
				src = src.split("\\?")[0].replace("v/", "watch?v=");
				return src;
			}
		}

		return new PlainText().getPlainText(doc.body());

	}

	/**
	 * @param fileUrl
	 * @param fileName
	 */
	private void getFile(Context context, String fileUrl, String fileName) {
		String inputLine;
		String outPut = "";
		try {
			// get URL content
			URL url = new URL(fileUrl);
			URLConnection conn = url.openConnection();
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			while ((inputLine = br.readLine()) != null) {
				outPut += inputLine;
			}
			Object toStore = outPut;
			new DataStore().storeData(context, toStore, fileName);
			br.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * HTML to plain-text. This example program demonstrates the use of jsoup to
	 * convert HTML input to lightly-formatted plain-text. That is divergent
	 * from the general goal of jsoup's .text() methods, which is to get clean
	 * data from a scrape.
	 * <p/>
	 * Note that this is a fairly simplistic formatter -- for real world use
	 * you'll want to embrace and extend.
	 * 
	 * @author Jonathan Hedley
	 * @author firetrap re-build and corrected to work with android
	 */
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
			@Override
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
			@Override
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

				if (!(text.length() + width > maxWidth)) { // fits as is,
															// without need to
															// wrap text
					accum.append(text);
					width += text.length();
				} else { // won't fit, needs to
							// wrap
					String words[] = text.split("\\s+");
					for (int i = 0; i < words.length; i++) {
						String word = words[i];
						boolean last = i == words.length - 1;
						if (!last) // insert a space if not the last word
							word = word + " ";
						if (!(word.length() + width > maxWidth)) {
							accum.append(word);
							width += word.length();
						} else { // wrap and
									// reset
									// counter
							accum.append("\n").append(word);
							width = word.length();
						}
					}
				}
			}

			@Override
			public String toString() {

				return accum.toString().replaceAll("[<>]", "");

			}
		}
	}

	/**
	 * @param context
	 * @param fileName
	 * @return boolean
	 */
	public boolean isInCache(Context context, String fileName) {
		Object content = new DataStore().getData(context, fileName);
		return !(content == null) ? true : false;
	}

	// FAVORITES

	public void insertFavorite(long id, Context context, Resources resource) {
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add(id);

		actionFavorite(ids, context, resource);
	}

	public void removeFavorite(ArrayList<Long> ids, Context context,
			Resources resource) {
		actionFavorite(ids, context, resource);
	}

	public void actionFavorite(ArrayList<Long> ids, Context context,
			Resources resource) {
		String userId = new Session(context).getValues(MoodyConstants.KEY_ID,
				null);
		String fileName = resource.getString(R.string.favorites_file_name)
				+ userId;
		ArrayList<Long> idList = getFavorites(context, resource);

		for (Long id : ids) {

			if (isFavorite(id, context, resource))
				idList.remove(id);
			else
				idList.add(id);

		}

		new DataStore().storeData(context, idList, fileName);
	}

	public ArrayList<Long> getFavorites(Context context, Resources resource) {
		String userId = new Session(context).getValues(MoodyConstants.KEY_ID,
				null);
		String fileName = resource.getString(R.string.favorites_file_name)
				+ userId;

		return (isInCache(context, fileName)) ? (ArrayList<Long>) data.getData(
				context, fileName) : new ArrayList<Long>();
	}

	public boolean isFavorite(long id, Context context, Resources resource) {
		return getFavorites(context, resource).contains(id);
	}

}