package connections;

import interfaces.AsyncResult;

import java.io.InputStream;
import java.net.URL;

import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleMessage;
import restPackage.MoodleRestCourse;
import restPackage.MoodleRestEnrol;
import restPackage.MoodleRestMessage;
import restPackage.MoodleRestUser;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;

/**
 * 
 * This class is responsible to get date from Server and return the required
 * object, it runs in a new thread
 * 
 * @author firetrap
 * 
 */
public class DataAsyncTask extends AsyncTask<Object, Void, Object> {
	Object jObj = null;
	public AsyncResult asyncInterface = null;
	private ProgressDialog dialog;
	private CountDownTimer cvt = createCountDownTimer();
	private Context context;

	public DataAsyncTask(Context context) {
		this.context = context;
		dialog = new ProgressDialog(context);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		cvt.start();
	}

	@Override
	protected Object doInBackground(Object... params) {
		String urlString = (String) params[0];
		String token = (String) params[1];
		MoodleServices webService = (MoodleServices) params[2];
		Object webServiceParams = params[3];

		MoodleCallRestWebService.init(urlString + "/webservice/rest/server.php", token);

		long userId;
		long courseId;

		try {
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
				MoodleCourseContent[] courseContent = MoodleRestCourse.getCourseContent(courseId, null);
				return courseContent;
			case CORE_MESSAGE_CREATE_CONTACTS:
				MoodleRestMessage.createContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_DELETE_CONTACTS:
				MoodleRestMessage.deleteContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_BLOCK_CONTACTS:
				MoodleRestMessage.blockContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_UNBLOCK_CONTACTS:
				MoodleRestMessage.unblockContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_GET_CONTACTS:
				return MoodleRestMessage.getContacts();

			case CORE_MESSAGE_SEND_INSTANT_MESSAGES:
				return MoodleRestMessage.sendInstantMessage((MoodleMessage) webServiceParams);

			case MOODLE_USER_GET_PICTURE:
				InputStream inputStream = new URL(urlString).openStream();
				Drawable drawable = Drawable.createFromStream(inputStream, null);
				inputStream.close();
				return drawable;

			default:
				return null;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * <p>
	 * Method that parses a suposed id list object
	 * </p>
	 * 
	 * @param Object
	 *            ids - The object to be parsed to Long[].
	 * @return resultList - The ids List
	 */
	private Long[] parseIds(Object ids) {

		Long[] resultList = null;

		try {
			resultList = (Long[]) ids;
		} catch (Exception e) {
			resultList = new Long[1];

			resultList[0] = (Long) ids;
		}

		return resultList;
	}

	@Override
	protected void onPostExecute(Object obj) {
		cvt.cancel();
		if (dialog != null && dialog.isShowing())
			dialog.dismiss();
		asyncInterface.pictureAsyncTaskResult(obj);

	}

	private CountDownTimer createCountDownTimer() {
		return new CountDownTimer(250, 10) {
			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				dialog = new ProgressDialog(context);
				dialog.setMessage("Loading...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		};
	}

}