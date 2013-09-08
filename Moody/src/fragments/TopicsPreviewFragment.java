package fragments;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import managers.SessionManager;
import model.MoodyConstants;
import model.MoodyConstants.MoodySession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.moody.R;

import connections.DataAsyncTask;

public class TopicsPreviewFragment extends Fragment {

	// Session Manager Class
	SessionManager session;

	private JSONObject jsonObj;

	public TopicsPreviewFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new SessionManager(getActivity().getApplicationContext());

		String courseName = getArguments().getString("courseName");
		String courseId = getArguments().getString("courseId");
		JSONArray getTopics = getTopics(courseId);

		return createTopicsRows(getTopics, courseName, courseId);
	}

	public JSONArray getTopics(String courseId) {

		// ESTE IF SO FOI CRIADO DEVIDO AO NOSSO MOODLE ESTAR COM BUGS AO
		// DEVOLVER OS COURSES CONTENTS SO DEVOLVER 50 CHARACTERES ENQUANTO O
		// MOODLE NAO FOR ACTUALIZADO
		// IRA SER CRIADO O JSON ARRAY MANUALMENTE
		if (courseId.equalsIgnoreCase("5")) {

			String json = null;
			InputStream is = getResources().openRawResource(R.raw.json);

			int size;
			try {
				size = is.available();
				byte[] buffer = new byte[size];
				is.read(buffer);
				is.close();
				json = new String(buffer, "UTF-8");
				
				
				JSONArray jsonArrays = new JSONArray(json);
				return jsonArrays;			
				
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
				JSONArray topicsArray = jsonObj.getJSONArray("array");
				return topicsArray;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;

	}

	public View createTopicsRows(JSONArray rows, String CourseName,
			String courseId) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		View topicsHeaderView = createTopicsHeader(CourseName, courseId,
				inflater);

		insertPoint.addView(topicsHeaderView);

		createTopicsContent(rows, inflater, insertPoint);

		return insertPoint;

	}

	/**
	 * @param CourseName
	 * @param courseId
	 * @param inflater
	 * @return
	 */
	private View createTopicsHeader(String CourseName, String courseId,
			LayoutInflater inflater) {
		View topicsHeaderView = inflater.inflate(
				R.layout.topics_preview_header, null);

		TextView courseName = (TextView) topicsHeaderView
				.findViewById(R.id.course_path_textView);
		courseName.setText("Courses > " + CourseName);

		ImageButton addFavorites = (ImageButton) topicsHeaderView
				.findViewById(R.id.add_favorites_button_);
		addFavorites.setId(Integer.parseInt(courseId));
		addFavorites.setTag("add_favorites_button_" + courseId);
		return topicsHeaderView;
	}

	/**
	 * @param rows
	 * @param inflater
	 * @param insertPoint
	 */
	private void createTopicsContent(JSONArray rows, LayoutInflater inflater,
			LinearLayout insertPoint) {
		for (int j = 0; j < rows.length(); j++) {
			try {

				JSONObject arrayCursor = rows.getJSONObject(j);
				JSONArray modules = arrayCursor.getJSONArray("modules");
				if (modules.length() != 0) {
					LinearLayout row = new LinearLayout(getActivity());
					row.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					View topicsView = inflater.inflate(
							R.layout.topics_preview_context, null);
					TextView topicName = (TextView) topicsView
							.findViewById(R.id.topic_name);
					topicName.setText(arrayCursor.getString("name"));

					String moduleName = "";
					// Loop for the modules array
					for (int i = 0; i < modules.length(); i++) {
						JSONObject singleModule = modules.getJSONObject(i);
						String getNameDirty = (String) singleModule.get("name");
						String[] nameTokens = getNameDirty.split("\\s+");

						String getNamePure = "";

						for (int n = 0; n < nameTokens.length; n++) {
							if (n == 5 ) {
								if(nameTokens.length > 5)
									getNamePure += "...";
								
								break;
							}

							getNamePure += nameTokens[n] + " ";
						}

						moduleName += ("-" + getNamePure + "\n");
					}
					TextView topicModule = (TextView) topicsView
							.findViewById(R.id.content_preview_textView);
					topicModule.setText(moduleName);

					// Where the textview id will be topic id
					topicModule.setId(Integer.parseInt(arrayCursor
							.getString("id")));

					row.addView(topicsView);
					insertPoint.addView(row);
				}

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
