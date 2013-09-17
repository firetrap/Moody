package managers;

import java.util.concurrent.ExecutionException;

import model.EnumWebServices;
import model.MoodyConstants.MoodySession;
import android.content.Context;
import android.content.res.Resources;
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

	/**
	 * @param resources
	 * @param context
	 * @return
	 */
	public Object getUser(Resources resources, Context context) {
		session = new Session(context);
		String url = session.getValues(MoodySession.KEY_URL, null);
		String token = session.getValues(MoodySession.KEY_TOKEN, null);
		String userId = session.getValues(MoodySession.KEY_ID, null);
		try {

			String fileName = EnumWebServices.CORE_USER_GET_USERS_BY_ID.name()
					+ userId;

			if (isInCache(context, fileName)) {
				return getContent = data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_USER_GET_USERS_BY_ID, userId)
						.get();
				data.storeData(context, getContent, fileName);
				return getContent;
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
	 * @return
	 */
	public Object getUserCourses(Resources resources, Context context) {
		session = new Session(context);
		String url = session.getValues(MoodySession.KEY_URL, null);
		String token = session.getValues(MoodySession.KEY_TOKEN, null);
		String userId = session.getValues(MoodySession.KEY_ID, null);

		try {

			String fileName = EnumWebServices.CORE_ENROL_GET_USERS_COURSES
					.name() + userId;

			if (isInCache(context, fileName)) {
				return getContent = data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_ENROL_GET_USERS_COURSES, userId)
						.get();
				data.storeData(context, getContent, fileName);
				return getContent;
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
	 * @return
	 */
	public Object getSingleCourse(String courseId, Resources resources,
			Context context) {

		session = new Session(context);
		String url = session.getValues(MoodySession.KEY_URL, null);
		String token = session.getValues(MoodySession.KEY_TOKEN, null);

		try {

			String fileName = EnumWebServices.CORE_COURSE_GET_CONTENTS.name()
					+ courseId;

			if (isInCache(context, fileName)) {
				return getContent = data.getData(context, fileName);
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_COURSE_GET_CONTENTS, courseId)
						.get();
				data.storeData(context, getContent, fileName);
				return getContent;
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

	public Object getSingleCourseTopics(String courseId, Resources resources,
			Context context) {
		return data;
	}

	/**
	 * @param context
	 * @param fileName
	 * @return
	 */
	public boolean isInCache(Context context, String fileName) {
		Object content = new DataStore().getData(context, fileName);
		return content == null ? false : true;
	}
}