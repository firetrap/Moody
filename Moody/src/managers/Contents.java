package managers;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import model.MoodyConstants;
import model.MoodyConstants.MoodySession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;

import com.example.moody.R;

import connections.DataAsyncTask;

//ESTA CLASSE É DESNECESSARIA APENAS FOI CRIADA DEVIDO AO BUG DO MOODLE NO FUTURO O ELSE ABAIXO IRA SER MOVIDO PARA O TOPICS FRAGMENT
public class Contents {

	// Session Manager Class
	Session session;

	private JSONObject jsonObj;

	// DEVIDO AO BUG DESCRITO ABAIXO ESTE getTopics ESTA A RECEBER MERDA A MAIS
	// = RESOURCES.
	public JSONObject getCourseContent(String courseId, Resources resources,
			Context context) {

		session = new Session(context);

		// ESTE IF SO FOI CRIADO DEVIDO AO NOSSO MOODLE ESTAR COM BUGS AO
		// DEVOLVER OS COURSES CONTENTS SO DEVOLVER 50 CHARACTERES ENQUANTO O
		// MOODLE NAO FOR ACTUALIZADO
		// IRA SER CRIADO O JSON ARRAY MANUALMENTE
		if (courseId.equalsIgnoreCase("5")) {

			String json = null;
			InputStream is = resources.openRawResource(R.raw.json);

			int size;
			try {
				size = is.available();
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();
				json = new String(buffer, "UTF-8");

				jsonObj = new JSONObject();
				jsonObj.put("array", new JSONArray(json));

				// STORE COURSE JSON DATA CONTENT FOR FUTURE ACCESS
				DataStore data = new DataStore();
				String fileName = "coursesContent-" + courseId;
				data.storeJsonData(context, jsonObj, fileName);

				return jsonObj;

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
					null);
			String token = session.getValues(
					MoodyConstants.MoodySession.KEY_TOKEN, null);

			String con = String.format(MoodyConstants.MoodySession.KEY_PARAMS,
					url, token, "core_course_get_contents&courseid", courseId
							+ MoodySession.KEY_JSONFORMAT);

			try {
				jsonObj = new DataAsyncTask().execute(con, "json").get();

				// STORE COURSE JSON DATA CONTENT FOR FUTURE ACCESS
				DataStore data = new DataStore();
				String fileName = "coursesContent-" + courseId;

				data.storeJsonData(context, jsonObj, fileName);

				return jsonObj;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

}
