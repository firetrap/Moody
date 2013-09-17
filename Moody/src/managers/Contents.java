package managers;

import java.util.concurrent.ExecutionException;

import model.EnumWebServices;
import model.MoodyConstants;
import android.content.Context;
import android.content.res.Resources;
import connections.DataAsyncTask;

public class Contents {

	// Session Manager Class
	Session session;

	// Get data from server/cache and store
	public Object getCourseContents(String courseId, Resources resources,
			Context context) {

		session = new Session(context);

		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		try {
			Object getContent;
			DataStore data = new DataStore();
			String fileName = EnumWebServices.CORE_COURSE_GET_CONTENTS.name()
					+ courseId;

			if (isInCache(context, fileName)) {
				getContent = data.getData(context, fileName);
				return getContent;
			} else {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_COURSE_GET_CONTENTS, courseId)
						.get();

				// STORE COURSE DATA FOR FUTURE ACCESS
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

	public boolean isInCache(Context context, String fileName) {
		Object content = new DataStore().getData(context, fileName);
		return content == null ? false : true;
	}
}