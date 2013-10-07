package connections;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleRestCourse;
import restPackage.MoodleRestEnrol;
import restPackage.MoodleRestException;
import restPackage.MoodleRestUser;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;

/**
 * @author firetrap
 * 
 */
public class DataAsyncTask extends AsyncTask<Object, Void, Object> {
	Object jObj = null;

	@Override
	protected Object doInBackground(Object... params) {

		try {

			return loadFromNetwork((String) params[0], (String) params[1],
					(MoodleServices) params[2], params[3]);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MoodleRestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	private Object loadFromNetwork(String urlString, String token,
			MoodleServices webService, Object webServiceParams)
			throws UnsupportedEncodingException, MoodleRestException {

		MoodleCallRestWebService.init(
				urlString + "/webservice/rest/server.php", token);

		long userId;
		long courseId;
		switch (webService) {
		case CORE_ENROL_GET_USERS_COURSES:
			userId = Long.parseLong((String) webServiceParams);
			MoodleCourse[] courses = MoodleRestEnrol.getUsersCourses(userId);
			return courses;

		case CORE_USER_GET_USERS_BY_ID:
			userId = Long.parseLong((String) webServiceParams);
			MoodleUser user = MoodleRestUser.getUserById(userId);
			return user;

		case CORE_COURSE_GET_CONTENTS:
			courseId = Long.parseLong((String) webServiceParams);
			MoodleCourseContent[] courseContent = MoodleRestCourse
					.getCourseContent(courseId, null);
			return courseContent;
		case CORE_MESSAGE_CREATE_CONTACTS:
			return null;
		case CORE_MESSAGE_DELETE_CONTACTS:
			return null;
		case CORE_MESSAGE_BLOCK_CONTACTS:
			return null;
		case CORE_MESSAGE_UNBLOCK_CONTACTS:
			return null;
		default:
			return null;
		}

	}

	@Override
	protected void onPostExecute(Object obj) {

	}

	/**
	 * Returns a Drawable object containing the image located at
	 * 'imageWebAddress' if successful, and null otherwise. (Pre:
	 * 'imageWebAddress' is non-null and non-empty; method should not be called
	 * from the main/ui thread.)
	 */
	public static Drawable createDrawableFromUrl(String imageWebAddress) {
		Drawable drawable = null;

		final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		try {
			final InputStream inputStream = new URL(imageWebAddress)
					.openStream();
			drawable = Drawable.createFromStream(inputStream, null);
			inputStream.close();
		} catch (final MalformedURLException ex) {
		} catch (final IOException ex) {
		}

		return drawable;
	}
}
