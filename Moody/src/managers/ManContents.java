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

import restPackage.MoodleContact;
import restPackage.MoodleContactState;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleRestAction;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import android.content.Context;
import android.text.Html;
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
	Object getContent;
	checkContent chkContent;
	Context context;
	ManDataStore data;

	public ManContents(Context context) {
		this.context = context;
		this.data = new ManDataStore(context);

	}

	public void refresh() {
		setContacts();
		setUser();
		setCourses();
		for (int i = 0; i < getCourses().length; i++) {
			String courseId = Long.toString(getCourses()[i].getId());
			String courseName = getCourses()[i].getFullname();
			setContent(courseName, courseId);
		}
	}

	private void setUser() {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		String fileName = MoodleServices.CORE_USER_GET_USERS_BY_ID.name()
				+ userId;
		try {
			getContent = new DataAsyncTask().execute(url, token,
					MoodleServices.CORE_USER_GET_USERS_BY_ID, userId).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.storeData(getContent, fileName);

	}

	public MoodleUser getUser() {
		session = new ManSession(context);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		String fileName = MoodleServices.CORE_USER_GET_USERS_BY_ID.name()
				+ userId;

		if (!isInCache(fileName)) {
			setUser();
		}

		return (MoodleUser) data.getData(fileName);
	}

	private void setCourses() {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES.name()
				+ userId;

		try {
			getContent = new DataAsyncTask().execute(url, token,
					MoodleServices.CORE_ENROL_GET_USERS_COURSES, userId).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		data.storeData(getContent, fileName);

	}

	public MoodleCourse[] getCourses() {
		session = new ManSession(context);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES.name()
				+ userId;

		if (!isInCache(fileName)) {
			setCourses();
		}

		return (MoodleCourse[]) data.getData(fileName);
	}

	private void setContent(String courseName, String courseId) {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);

		String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name()
				+ courseId;

		try {
			getContent = new DataAsyncTask().execute(url, token,
					MoodleServices.CORE_COURSE_GET_CONTENTS, courseId).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isInCache(fileName) && courseName != null) {
			if (new ManFavorites(context).isFavorite(Long.parseLong(courseId))) {
				chkContent = new checkContent(context, courseName, courseId);
				chkContent.hasNewContent(getContent, fileName);
			} else {

			}

		}

		data.storeData(getContent, fileName);
	}

	public MoodleCourseContent[] getContent(String courseId) {
		String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name()
				+ courseId;

		if (!isInCache(fileName)) {
			setContent(null, courseId);
		}

		return (MoodleCourseContent[]) data.getData(fileName);
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

	/**
	 * 
	 * Method that gets the contacts of the user
	 * 
	 * 
	 * @param resources
	 * @param context
	 * 
	 * @return all of the contacts of the user.
	 * @return MoodleContact[]
	 */
	public MoodleContact[] getContacts() {
		session = new ManSession(context);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		String fileName = MoodleServices.CORE_MESSAGE_GET_CONTACTS.name()
				+ userId;

		if (!isInCache(fileName))
			setContacts();

		return (MoodleContact[]) data.getData(fileName);
	}

	private void setContacts() {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);
		String userId = session.getValues(ModConstants.KEY_ID, null);
		String fileName = MoodleServices.CORE_MESSAGE_GET_CONTACTS.name()
				+ userId;

		try {
			getContent = new DataAsyncTask().execute(url, token,
					MoodleServices.CORE_MESSAGE_GET_CONTACTS, null).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		data.storeData(getContent, fileName);
	}

	/**
	 * 
	 * Method to that perform actions on the contact provided an id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	private void actionContact(Long id, MoodleRestAction action) {
		Long[] a = { id };

		actionContacts(a, action);
	}

	/**
	 * + * + * Method that return true if the user has stranger contacts + * +
	 */
	public boolean contactHasStrangers() {
		if (isInCache(MoodleServices.CORE_MESSAGE_GET_CONTACTS.name())) {
			MoodleContact[] contacts = (MoodleContact[]) new ManDataStore(
					context).getData(MoodleServices.CORE_MESSAGE_GET_CONTACTS
					.name());

			for (MoodleContact contact : contacts) {
				if (contact.getState() == MoodleContactState.STRANGERS)
					return true;
			}
		}

		return false;
	}

	/**
	 * 
	 * Method to that perform actions on the contact provided an a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	private void actionContacts(Long[] ids, MoodleRestAction action) {
		session = new ManSession(context);
		String url = session.getValues(ModConstants.KEY_URL, null);
		String token = session.getValues(ModConstants.KEY_TOKEN, null);

		try {

			new DataAsyncTask().execute(url, token, action, ids).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Method that deletes the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void deleteContact(Long id) {
		actionContact(id, MoodleRestAction.DELETE);
	}

	/**
	 * 
	 * Method that deletes the contacts provided a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	public void deleteContacts(Long[] ids) {
		actionContacts(ids, MoodleRestAction.DELETE);
	}

	/**
	 * 
	 * Method that creates the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void createContact(Long id) {
		actionContact(id, MoodleRestAction.CREATE);
	}

	/**
	 * 
	 * Method that creates the contacts provided a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	public void createContacts(Long[] ids) {
		actionContacts(ids, MoodleRestAction.CREATE);
	}

	/**
	 * 
	 * Method that blocks the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void blockContact(Long id) {
		actionContact(id, MoodleRestAction.BLOCK);
	}

	/**
	 * 
	 * Method that blocks the contacts provided a list of ids
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	public void blockContacts(Long[] ids) {
		actionContacts(ids, MoodleRestAction.BLOCK);
	}

	/**
	 * 
	 * Method that ublocks the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param id
	 * 
	 */
	public void unblockContact(Long id) {
		actionContact(id, MoodleRestAction.UNBLOCK);
	}

	/**
	 * 
	 * Method that unblocks the contact provided its id
	 * 
	 * 
	 * @param resources
	 * @param context
	 * @param ids
	 * 
	 */
	public void unblockContacts(Long[] ids) {
		actionContacts(ids, MoodleRestAction.UNBLOCK);
	}

	// MOODLE SPECIFIC INDEX.HTML GET CONTENT
	/**
	 * @param context
	 * @param fileUrl
	 * @param fileName
	 * @return String
	 */
	public String parseFile(String fileUrl, String fileName) {
		Document doc;
		String src;

		if (!isInCache(fileName)) {
			getFile(fileUrl, fileName);
		}

		doc = Jsoup.parse((String) new ManDataStore(context).getData(fileName),
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
	private void getFile(String fileUrl, String fileName) {
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
			new ManDataStore(context).storeData(toStore, fileName);
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
	 * @author firetrap - re-build,corrected and extended to work with android
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
	public boolean isInCache(String fileName) {
		Object content = new ManDataStore(context).getData(fileName);
		return !(content == null) ? true : false;
	}

}