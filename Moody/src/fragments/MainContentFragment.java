package fragments;

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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
		LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		RelativeLayout beforeTopics = new RelativeLayout(getActivity());
		// RelativeLayout params
		beforeTopics.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		beforeTopics.setBackgroundResource(R.color.C_White);
		beforeTopics.setId(0);
		beforeTopics.setTag("before_topic_tag");

		TextView coursePath = new TextView(getActivity());
		coursePath.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		coursePath.setBackgroundColor(getResources().getColor(R.color.C_White));
		coursePath.setPadding(5, 10, 0, 10);
		coursePath.setText("Courses > " + CourseName);
		coursePath.setTextColor(getResources().getColor(R.color.C_Blue_Light));
		coursePath.setId(1);
		coursePath.setTag("course_path_tag");
		beforeTopics.addView(coursePath);

		ImageButton addFavorites = new ImageButton(getActivity());

		// Add favorits params
		addFavorites.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		addFavorites.setBackgroundResource(R.color.C_White);
		addFavorites
				.setContentDescription(getString(R.string.add_favorite_description));
		addFavorites.setPadding(0, 10, 50, 10);
		addFavorites.setImageResource(R.drawable.add_favorites);
		addFavorites.setId(2);
		addFavorites.setTag("add_favorites_tag");

		RelativeLayout.LayoutParams addFavoritesParams = new RelativeLayout.LayoutParams(
				addFavorites.getLayoutParams());

		addFavoritesParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		addFavorites.setLayoutParams(addFavoritesParams);
		beforeTopics.addView(addFavorites);

		insertPoint.addView(beforeTopics);

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		for (int j = 0; j < rows.length(); j++) {
			try {
				JSONObject arrayCursor = rows.getJSONObject(j);

				LinearLayout row = new LinearLayout(getActivity());
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View view = inflater.inflate(
						R.layout.testes, null);
				TextView topicName = (TextView) view.findViewById(R.id.topic_name);
				topicName.setText(arrayCursor.getString("name"));
				
				
				
				row.addView(view);
				insertPoint.addView(row);

				//
				// RelativeLayout topics = new RelativeLayout(getActivity());
				// ImageButton arrow = new ImageButton(getActivity());
				// TextView topicName = new TextView(getActivity());
				// TextView contentPreview = new TextView(getActivity());
				// View viewBottom = new View(getActivity());
				// View viewTop = new View(getActivity());
				//
				// // RelativeLayout params
				// topics.setLayoutParams(new LayoutParams(
				// android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				// topics.setBackgroundResource(R.drawable.fill_light_grey);
				// topics.setId(j + 3);
				// topics.setTag("topic_tag_" + j);
				//
				// // Arrow params
				// arrow.setLayoutParams(new LayoutParams(
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				// arrow.setBackgroundResource(R.color.transparent);
				// arrow.setContentDescription(getString(R.string.arrow_description));
				// arrow.setPadding(0, 0, 5, 0);
				// arrow.setImageResource(R.drawable.right_arrow);
				// arrow.setId(j + 4);
				// arrow.setTag("arrow_tag");
				//
				// RelativeLayout.LayoutParams arrowParams = new
				// RelativeLayout.LayoutParams(
				// arrow.getLayoutParams());
				// arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
				// arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
				// arrow.setLayoutParams(arrowParams);
				//
				// // Add textview topics
				// topicName.setLayoutParams(new LayoutParams(
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				// topicName.setBackgroundResource(R.drawable.border_course_context);
				//
				// topicName.setPadding(20, 0, 20, 0);
				// topicName.setText(arrayCursor.getString("name"));
				// topicName.setId(j + 5);
				// topicName.setTag("topic_name_tag");
				//
				// RelativeLayout.LayoutParams topicsNameParams = new
				// RelativeLayout.LayoutParams(
				// topicName.getLayoutParams());
				// topicsNameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				// topicsNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				// topicsNameParams.setMargins(0, 10, 0, 0);
				// topicName.setLayoutParams(topicsNameParams);
				//
				// // Add textview content preview
				// contentPreview.setLayoutParams(new LayoutParams(
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				// android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				// contentPreview.setText(getString(R.string.content_preview));
				//
				// RelativeLayout.LayoutParams contentPreviewParams = new
				// RelativeLayout.LayoutParams(
				// contentPreview.getLayoutParams());
				// contentPreviewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				// contentPreviewParams.addRule(RelativeLayout.LEFT_OF,
				// arrow.getId());
				// contentPreviewParams.addRule(RelativeLayout.BELOW,
				// topicName.getId());
				// contentPreviewParams.setMargins(0, 10, 30, 10);
				// contentPreview.setId(j + 6);
				// contentPreview.setTag("content_preview_tag");
				// contentPreview.setLayoutParams(contentPreviewParams);
				//
				// // Add view to fill space bottom of topics
				// viewBottom.setLayoutParams(new LayoutParams(
				// android.view.ViewGroup.LayoutParams.FILL_PARENT, 10));
				// viewBottom.setBackgroundResource(R.drawable.border_inside);
				// RelativeLayout.LayoutParams viewBottomParams = new
				// RelativeLayout.LayoutParams(
				// viewBottom.getLayoutParams());
				// viewBottomParams.addRule(RelativeLayout.ALIGN_BOTTOM,
				// contentPreview.getId());
				// viewBottomParams.setMargins(0, 0, 0, -10);
				// viewBottom.setLayoutParams(viewBottomParams);
				//
				// // Add view to fill space in the top of topics
				// viewTop.setLayoutParams(new LayoutParams(
				// android.view.ViewGroup.LayoutParams.FILL_PARENT, 10));
				// viewTop.setBackgroundColor(getResources().getColor(R.color.C_White));
				// RelativeLayout.LayoutParams viewTopParams = new
				// RelativeLayout.LayoutParams(
				// viewTop.getLayoutParams());
				// viewTopParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
				// viewTopParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				// viewTop.setLayoutParams(viewTopParams);
				//
				// topics.addView(arrow);
				// topics.addView(topicName);
				// topics.addView(contentPreview);
				// topics.addView(viewBottom);
				// topics.addView(viewTop);
				//
				// row.addView(topics);
				// insertPoint.addView(row);

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

}
