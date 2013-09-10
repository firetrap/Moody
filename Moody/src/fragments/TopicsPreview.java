package fragments;

import managers.Contents;
import managers.DataStore;
import managers.Session;

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

public class TopicsPreview extends Fragment {

	// Session Manager Class
	Session session;
	JSONObject jsonObject;

	public TopicsPreview() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new Session(getActivity().getApplicationContext());

		String courseName = getArguments().getString("courseName");
		String courseId = getArguments().getString("courseId");
		Context activityContext = getActivity().getApplicationContext();

		// Always tries to get the JSON from cache if it doesn't exist it will
		// return null, so it will download from moodle site
		jsonObject = new DataStore().getJsonData(activityContext,
				("coursesContent" + courseId));
		if (jsonObject == null) {
			// Get the topics from internet in json
			jsonObject = new Contents().getCourseContent(courseId,
					getResources(), activityContext);
		}

		try {
			return createTopicsRows(jsonObject, courseName, courseId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	// Method to create the header of the topics preview with the course path
	// and the "add favorites" button
	/**
	 * @param CourseName
	 * @param courseId
	 * @param inflater
	 * @return
	 */
	protected View createTopicsHeader(String CourseName, String courseId,
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

	// Method to create the topics preview with the courses content and add this
	// content to the "row"
	/**
	 * @param rows
	 * @param inflater
	 * @param insertPoint
	 * @throws JSONException
	 */
	protected void createTopicsContent(JSONObject jsonContent,
			LayoutInflater inflater, LinearLayout insertPoint)
			throws JSONException {

		JSONArray rows = jsonContent.getJSONArray("array");

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
							if (n == 5) {
								if (nameTokens.length > 5)
									getNamePure += "...";

								break;
							}

							getNamePure += nameTokens[n] + " ";

						}
						if (i >= 4) {
							moduleName += ".....";
							break;
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

	// Create the "row" with the header and the content
	protected View createTopicsRows(JSONObject jsonContent, String CourseName,
			String courseId) throws JSONException {
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

		createTopicsContent(jsonContent, inflater, insertPoint);

		return insertPoint;

	}

}
