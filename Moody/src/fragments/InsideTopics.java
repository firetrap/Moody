package fragments;

import managers.Contents;
import managers.DataStore;
import managers.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.moody.R;

import android.app.Fragment;
import android.content.Context;
import android.opengl.Visibility;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class InsideTopics extends Fragment {

	JSONObject jsonObject;
	// Session Manager Class
	Session session;

	public InsideTopics() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new Session(getActivity().getApplicationContext());

		final String courseId = getArguments().getString("courseId");
		final String courseName = getArguments().getString("courseName");
		final String topicId = getArguments().getString("topicId");
		final Context activityContext = getActivity().getApplicationContext();

		// Always tries to get the JSON from cache if it doesn't exist it will
		// return null, so it will download from moodle site

		final String fileName = "coursesContent-" + courseId;

		jsonObject = new DataStore().getJsonData(activityContext, fileName);
		if (jsonObject == null) {
			// Get the topics from internet in json
			jsonObject = new Contents().getCourse(courseId, getResources(),
					activityContext);
		}

		return createTopicsRows(jsonObject, courseName, courseId, topicId);
	}

	private View createTopicsRows(JSONObject jsonObject, String courseName,
			String courseId, String topicId) {
		final LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		String topicName = getTopicName(jsonObject, topicId);

		final View inflateHeader = createTopicsHeader(courseName, topicName,
				inflater);

		insertPoint.addView(inflateHeader);

		// createTopicsContent(jsonContent, inflater, insertPoint, courseId);

		return insertPoint;
	}

	/**
	 * @param jsonObject
	 * @param topicId
	 * @return
	 */
	private String getTopicName(JSONObject jsonObject, String topicId) {
		String topicName = "";
		JSONArray topics;
		try {
			topics = jsonObject.getJSONArray("array");

			for (int j = 0; j < topics.length(); j++) {
				final JSONObject arrayCursor = topics.getJSONObject(j);
				String idValue = arrayCursor.getString("id");
				if (idValue.equalsIgnoreCase(topicId)) {
					topicName = arrayCursor.getString("name");
					break;
				}

			}

		}

		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return topicName;
	}

	private View createTopicsHeader(String courseName, String topicName,
			LayoutInflater inflater) {

		final View topicsHeaderView = inflater.inflate(
				R.layout.topics_preview_header, null);

		final TextView path = (TextView) topicsHeaderView
				.findViewById(R.id.course_path_textView);

		path.setText(Html.fromHtml("Courses > " + courseName + " > "
				+ "<font color=#68d5fe>" + topicName + "</font>"));

		final ImageButton addFavorites = (ImageButton) topicsHeaderView
				.findViewById(R.id.add_favorites_button_);
		addFavorites.setVisibility(View.GONE);
		return topicsHeaderView;

	}

}
