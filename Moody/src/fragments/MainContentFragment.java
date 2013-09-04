package fragments;

import java.util.concurrent.ExecutionException;

import managers.SessionManager;
import model.MoodyConstants;
import model.MoodyConstants.MoodySession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import activities.LoginActivity;
import activities.UserDetailsActivity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moody.R;

import connections.DataAsyncTask;

public class MainContentFragment extends Fragment {

	// Session Manager Class
	SessionManager session;

	private JSONObject jsonObj;

	public MainContentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new SessionManager(getActivity().getApplicationContext());

		String courseName = getArguments().getString("courseName");
		String courseId = getArguments().getString("courseId");

		return numberOfRows(getJsonArray(courseId), courseName);
	}

	public View numberOfRows(JSONArray rows, String CourseName) {

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		View topicsHeaderView = inflater.inflate(
				R.layout.topics_preview_header, null);
		TextView courseName = (TextView) topicsHeaderView
				.findViewById(R.id.course_path_textView);
		courseName.setText("Courses > " + CourseName);
		insertPoint.addView(topicsHeaderView);

		for (int j = 0; j < rows.length(); j++) {
			try {
				JSONObject arrayCursor = rows.getJSONObject(j);

				LinearLayout row = new LinearLayout(getActivity());
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View view = inflater.inflate(R.layout.topics_preview_context,
						null);
				TextView topicName = (TextView) view
						.findViewById(R.id.topic_name);
				topicName.setText(arrayCursor.getString("name"));
				row.addView(view);
				insertPoint.addView(row);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return insertPoint;

	}

	public JSONArray getJsonArray(String courseId) {
		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		String con = String.format(MoodyConstants.MoodySession.KEY_PARAMS, url,
				token, "core_course_get_contents&courseid", courseId
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
		return null;

	}

	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.add_favorites_button:
			Toast.makeText(getActivity().getApplicationContext(),
					"ADD FAVORITOS" + v.getId(), Toast.LENGTH_SHORT).show();
			break;
		case R.id.arrow_topics_image_button:
			Toast.makeText(getActivity().getApplicationContext(),
					"ENTROU NO TOPICO" + v.getId(), Toast.LENGTH_SHORT).show();

			break;

		default:
			// Toast.makeText(getApplicationContext(),
			// "ENTROU NO PRIMEIRO :" + v.getId(), Toast.LENGTH_SHORT)
			// .show();
			// throw new RuntimeException("Unknown button ID");
		}
	}
}
