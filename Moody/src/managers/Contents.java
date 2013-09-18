package managers;

import java.util.concurrent.ExecutionException;

import model.EnumWebServices;
import model.MoodyConstants;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleUser;
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
	 * @param courseId
	 * @param topicId
	 * @param resources
	 * @param context
	 * @return MoodleModule[]
	 */
	public MoodleModule[] getTopicModules(String courseId, String topicId,
			Resources resources, Context context) {

		MoodleCourseContent[] course = getCourseContent(courseId, resources,
				context);

		for (int i = 0; i < course.length; i++) {
			if (course[i].getId() == Long.parseLong(topicId)) {
				return course[i].getMoodleModules();

			}
		}

		return null;
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

}