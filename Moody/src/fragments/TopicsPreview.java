package fragments;

import managers.Contents;
import managers.Session;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
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
	private Object Object;
	private MoodleCourseContent[] courseContent = null;

	public TopicsPreview() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new Session(getActivity().getApplicationContext());

		String courseName = getArguments().getString("courseName");
		String courseId = getArguments().getString("courseId");
		Context activityContext = getActivity().getApplicationContext();

	
		Object = new Contents().getCourse(courseId, getResources(),
				activityContext);

		courseContent = (MoodleCourseContent[]) Object;

		return createTopicsRows(courseContent, courseName, courseId);

	}

	// Create the "row" with the header and the content
	protected View createTopicsRows(MoodleCourseContent[] courseContent,
			String CourseName, String courseId) {
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

		createTopicsContent(courseContent, inflater, insertPoint, courseId);

		return insertPoint;

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

		courseName.setText(Html.fromHtml("Courses > " + "<font color=#68d5fe>"
				+ CourseName + "</font>"));

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
	 * @param courseId
	 */
	protected void createTopicsContent(MoodleCourseContent[] courseContent,
			LayoutInflater inflater, LinearLayout insertPoint, String courseId) {

		for (int j = 0; j < courseContent.length; j++) {

			MoodleModule[] modules = courseContent[j].getMoodleModules();
			if (modules != null) {
				LinearLayout row = new LinearLayout(getActivity());
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View topicsView = inflater.inflate(
						R.layout.topics_preview_context, null);
				TextView topicName = (TextView) topicsView
						.findViewById(R.id.topic_label);
				topicName.setText(courseContent[j].getName());

				String moduleName = "";
				// Loop for the modules array
				for (int i = 0; i < modules.length; i++) {

					String getNameDirty = modules[i].getName();
					String getNamePure = "";

					for (int n = 0; n < getNameDirty.split("\\s+").length; n++) {
						if (n == 5) {
							break;
						}

						getNamePure += getNameDirty.split("\\s+")[n] + " ";
					}

					moduleName += "-" + getNamePure + "\n";

				}
				TextView topicModule = (TextView) topicsView
						.findViewById(R.id.content_preview_textView);
				topicModule.setText(moduleName);

				// Where the textview id will be course id and the textview
				// tag will be the topic id
				topicModule.setId(Integer.parseInt(courseId));
				topicModule.setTag(Long.toString(courseContent[j].getId()));
				row.addView(topicsView);
				insertPoint.addView(row);
			}
		}

	}

}