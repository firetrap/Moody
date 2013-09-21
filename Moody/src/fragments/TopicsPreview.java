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

	public TopicsPreview() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		session = new Session(getActivity().getApplicationContext());
		String courseName = getArguments().getString("courseName");
		String courseId = getArguments().getString("courseId");

		return createTopicsRows(courseName, courseId);
	}

	/**
	 * 
	 * Create the "row" with the header and the content
	 * 
	 * @param CourseName
	 * @param courseId
	 * @return View
	 */
	protected View createTopicsRows(String CourseName, String courseId) {
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

		createTopicsContent(inflater, insertPoint, courseId);

		return insertPoint;

	}

	/**
	 * Method to create the header of the topics preview with the course path
	 * and the "add favorites" button
	 * 
	 * @param CourseName
	 * @param courseId
	 * @param inflater
	 * @return View
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

	/**
	 * Method to create the topics preview with the courses content and add this
	 * content to the "row"
	 * 
	 * @param rows
	 * @param inflater
	 * @param insertPoint
	 * @param courseId
	 */
	protected void createTopicsContent(LayoutInflater inflater,
			LinearLayout insertPoint, String courseId) {

		MoodleCourseContent[] courseContent = new Contents()
				.getCourseContent(courseId, getResources(), getActivity()
						.getApplicationContext());

		for (int j = 0; j < courseContent.length; j++) {

			MoodleModule[] modules = courseContent[j].getMoodleModules();
			if (modules != null) {
				LinearLayout row = new LinearLayout(getActivity());
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View topicsView = inflater.inflate(R.layout.topics_preview_context, null);

				TextView topicName = (TextView) topicsView
						.findViewById(R.id.title);
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
						.findViewById(R.id.description);
				topicModule.setText(moduleName);

				// Where the LinearLayout from TopicsPreview id will be course
				// id and the LinearLayout
				// tag will be the topic id
				LinearLayout layout = (LinearLayout) topicsView
						.findViewById(R.id.topics_preview_layout);
				layout.setId(Integer.parseInt(courseId));
				layout.setTag(Long.toString(courseContent[j].getId()));
				row.addView(topicsView);
				insertPoint.addView(row);
			}
		}

	}

}