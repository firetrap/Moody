package managers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import model.ModConstants;

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
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import activities.MainActivity;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.text.Html;

import com.android.moody.R;

import connections.DataAsyncTask;

/**
 * @author firetrap
 * 
 *         This class return the requested data, the procedure is always tries
 *         to get data from cache if it doesn't exist it will download from
 *         server and store in cache
 * 
 */
public class ManContents {

	// ManSession Manager Class
	ManSession session;
	ManDataStore data = new ManDataStore();
	Object getContent;

	public void getAll(Resources resources, Context context) {
		try {
			setUser(resources, context);
			setUserCourses(resources, context);
			for (int i = 0; i < getUserCourses(resources, context).length; i++) {
				String id = Long.toString(getUserCourses(resources, context)[i]
						.getId());
				setCourseContent(id, resources, context);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// MOODLE DATA
	/**
	 * @param resources
	 * @param context
	 * @return MoodleUser
	 */
	public MoodleUser getUser(Resources resources, Context context) {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		try {

			String fileName = MoodleServices.CORE_USER_GET_USERS_BY_ID.name()
					+ userId;

			if (isInCache(context, fileName)) {
				return (MoodleUser) data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						MoodleServices.CORE_USER_GET_USERS_BY_ID, userId).get();
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
	 * 
	 * Create and store in cache for future access a file with the user info
	 * 
	 * @param resources
	 * @param context
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void setUser(Resources resources, Context context)
			throws InterruptedException, ExecutionException {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);

		String fileName = MoodleServices.CORE_USER_GET_USERS_BY_ID.name()
				+ userId;

		getContent = new DataAsyncTask().execute(url, token,
				MoodleServices.CORE_USER_GET_USERS_BY_ID, userId).get();
		data.storeData(context, getContent, fileName);

	}

	/**
	 * @param resources
	 * @param context
	 * @return MoodleCourse[]
	 */
	public MoodleCourse[] getUserCourses(Resources resources, Context context) {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);

		try {

			String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES
					.name() + userId;

			if (isInCache(context, fileName)) {
				return (MoodleCourse[]) data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						MoodleServices.CORE_ENROL_GET_USERS_COURSES, userId)
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
	 * 
	 * Create and store in cache for future access a file with the user courses
	 * 
	 * @param resources
	 * @param context
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void setUserCourses(Resources resources, Context context)
			throws InterruptedException, ExecutionException {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);

		String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES.name()
				+ userId;

		getContent = new DataAsyncTask().execute(url, token,
				MoodleServices.CORE_ENROL_GET_USERS_COURSES, userId).get();
		data.storeData(context, getContent, fileName);

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

		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);

		try {

			String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name()
					+ courseId;

			if (isInCache(context, fileName)) {

				return (MoodleCourseContent[]) data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						MoodleServices.CORE_COURSE_GET_CONTENTS, courseId)
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
	 * 
	 * Create and store in cache for future access a file with the course
	 * contents
	 * 
	 * @param courseId
	 * @param resources
	 * @param context
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private void setCourseContent(String courseId, Resources resources,
			Context context) throws InterruptedException, ExecutionException {

		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);

		String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name()
				+ courseId;

		getContent = new DataAsyncTask().execute(url, token,
				MoodleServices.CORE_COURSE_GET_CONTENTS, courseId).get();

		hasNewContent(resources, context, getContent, fileName);
		data.storeData(context, getContent, fileName);

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

		doc = Jsoup
				.parse((String) new ManDataStore().getData(context, fileName),
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
			new ManDataStore().storeData(context, toStore, fileName);
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
		Object content = new ManDataStore().getData(context, fileName);
		return !(content == null) ? true : false;
	}

	private void hasNewContent(Resources resources, Context context,
			Object newContent, String fileName) {

		MoodleCourseContent[] oldObj = (MoodleCourseContent[]) data.getData(
				context, fileName);
		MoodleCourseContent[] newObj = (MoodleCourseContent[]) newContent;

		if (oldObj != null && newObj != null) {
			if (oldObj.length == newObj.length) {
				sendNotification(context);
			}

			// return Arrays.equals(oldObj, newObj) ? false : true;
		}

	}

	@SuppressLint("NewApi")
	public void sendNotification(Context context) {
		// Prepare intent which is triggered if the
		// notification is selected
		Intent intent1 = new Intent(context, MainActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent1,
				0);

		// Build notification
		// Actions are just fake
		Notification noti = new Notification.Builder(context)
				.setContentTitle("New mail from " + "test@gmail.com")
				.setContentText("Subject")
				.setSmallIcon(R.drawable.notification_icon)
				.setContentIntent(pIntent)
				.addAction(R.drawable.notification_icon, "Call", pIntent)
				.addAction(R.drawable.notification_icon, "More", pIntent)
				.addAction(R.drawable.notification_icon, "And more", pIntent)
				.build();
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(context.NOTIFICATION_SERVICE);
		// Hide the notification after its selected
		noti.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, noti);

	}

}