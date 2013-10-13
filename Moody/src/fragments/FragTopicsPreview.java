package fragments;

import managers.ManContents;
import managers.ManFavorites;
import managers.ManSession;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import com.android.moody.R;

/**
 * @author firetrap
 * 
 */
public class FragTopicsPreview extends Fragment {
	// ManSession Manager Class
	ManSession session;

	public FragTopicsPreview() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		session = new ManSession(getActivity().getApplicationContext());
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
		int id = Integer.parseInt(courseId);

		TextView courseName = (TextView) topicsHeaderView
				.findViewById(R.id.course_path_textView);

		courseName.setText(Html.fromHtml("Courses ><font color=#68d5fe>"
				+ CourseName + "</font>"));

		// If the course is already cached as favorite, hides the button,
		// otherwise configs it's ID
		if (!new ManFavorites(getActivity().getApplicationContext())
				.isFavorite(id)) {
			ImageButton addFavorites = (ImageButton) topicsHeaderView
					.findViewById(R.id.add_favorites_button_);

			addFavorites.setId(id);
			addFavorites.setTag("add_favorites_button_" + courseId);
		} else
			((ImageButton) topicsHeaderView
					.findViewById(R.id.add_favorites_button_))
					.setVisibility(View.GONE);

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

		final MoodleCourseContent[] courseContent = new ManContents(
				getActivity().getApplicationContext()).getContent(courseId);

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
						.findViewById(R.id.topics_preview_title);
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
						.findViewById(R.id.topics_preview_description);
				topicModule.setText(moduleName);
				// Where the LinearLayout from FragTopicsPreview id will be
				// course
				// id and the LinearLayout
				// tag will be the topic id
				LinearLayout layout = (LinearLayout) topicsView
						.findViewById(R.id.topics_preview_layout);
				layout.setId(Integer.parseInt(courseId));
				layout.setTag(Long.toString(courseContent[j].getId()));
				onTopicsPreviewClick(courseContent, layout, j);
				row.addView(topicsView);
				insertPoint.addView(row);
			}
		}

	}

	/**
	 * @param courseContent
	 * @param topicsView
	 * @param contentIdx
	 */
	private void onTopicsPreviewClick(
			final MoodleCourseContent[] courseContent, View layout,
			final int contentIdx) {

		layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String courseId = Integer.toString(v.getId());
				String courseName = courseContent[contentIdx].getName();
				String topicId = (String) v.getTag();

		

				Bundle bundle = new Bundle();
				bundle.putString("courseId", courseId);
				bundle.putString("courseName", courseName);
				bundle.putString("topicId", topicId);

				FragmentManager fragmentManager = getActivity()
						.getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager
						.beginTransaction();

				FragTopics insideTopicsFrag = new FragTopics();
				insideTopicsFrag.setArguments(bundle);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction
						.replace(R.id.mainFragment, insideTopicsFrag);
				fragmentTransaction.commit();
			}
		});
	}
}