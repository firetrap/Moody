package managers;

import java.util.concurrent.ExecutionException;

import model.EnumWebServices;
import model.MoodyConstants.MoodySession;
import android.content.Context;
import android.content.res.Resources;
import connections.DataAsyncTask;

public class Contents {

	// Session Manager Class
	Session session;
	DataStore data = new DataStore();
	Object getContent;

	// Always tries to get data from cache if it doesn't exist it will download
	// from moodle site and store in cache
	public Object getCourse(String courseId, Resources resources,
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

	public Object getCourseTopics(String courseId, Resources resources,
			Context context) {
		return data;
	}

	public boolean isInCache(Context context, String fileName) {
		Object content = new DataStore().getData(context, fileName);
		return content == null ? false : true;
	}
}