package managers;

import java.util.concurrent.ExecutionException;

import model.EnumWebServices;
import model.MoodyConstants;
import android.content.Context;
import android.content.res.Resources;
import connections.CopyOfDataAsyncTask;

public class CopyOfContents {

	// Session Manager Class
	Session session;

	// Get data from server and store
	public Object getCourseContents(String courseId, Resources resources,
			Context context) {

		session = new Session(context);

		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		try {
			Object getContent = new CopyOfDataAsyncTask().execute(url, token,
					EnumWebServices.CORE_COURSE_GET_CONTENTS, courseId).get();

			CopyOfDataStore data = new CopyOfDataStore();
			String fileName = EnumWebServices.CORE_COURSE_GET_CONTENTS.name()
					+ courseId;
			// STORE COURSE DATA FOR FUTURE ACCESS
			data.storeData(context, getContent, fileName);
			return getContent;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

}
