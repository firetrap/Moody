package connections;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import model.EnumWebServices;
import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleFileParent;
import restPackage.MoodleRestCourse;
import restPackage.MoodleRestEnrol;
import restPackage.MoodleRestException;
import restPackage.MoodleRestFile;
import restPackage.MoodleRestMessage;
import restPackage.MoodleRestUser;
import restPackage.MoodleUser;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.StrictMode;

//receives a String[] where params[0] it's an url, params[1] its an string required to parse, params[2] its an string to the required method above
public class DataAsyncTask extends AsyncTask<Object, Void, Object> {
	Object jObj = null;

	@Override
	protected Object doInBackground(Object... params) {

		try {

			return loadFromNetwork((String) params[0], (String) params[1],
					(EnumWebServices) params[2], (String) params[3]);
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
			EnumWebServices webService, String webServiceParams)
			throws UnsupportedEncodingException, MoodleRestException {

		MoodleCallRestWebService.init(
				urlString + "/webservice/rest/server.php", token);
		webServiceParams = webServiceParams.trim();

		long userId;
		long courseId;
		switch (webService) {
		case CORE_ENROL_GET_USERS_COURSES:
			userId = Long.parseLong(webServiceParams);
			MoodleCourse[] courses = MoodleRestEnrol.getUsersCourses(userId);
			return courses;

		case CORE_USER_GET_USERS_BY_ID:
			userId = Long.parseLong(webServiceParams);
			MoodleUser user = MoodleRestUser.getUserById(userId);
			return user;

		case CORE_COURSE_GET_CONTENTS:
			courseId = Long.parseLong(webServiceParams);
			MoodleCourseContent[] courseContent = MoodleRestCourse
					.getCourseContent(courseId, null);
			courseContent[0].getName();
			return courseContent;
			
		
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
