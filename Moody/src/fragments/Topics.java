package fragments;

import managers.Contents;
import managers.Session;
import model.MoodyConstants;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.example.moody.R;

public class Topics extends Fragment {

	Object course;
	// Session Manager Class
	Session session;
	String courseId;
	Long topicId;
	String courseName;

	public Topics() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new Session(getActivity().getApplicationContext());

		courseId = getArguments().getString("courseId");
		topicId = Long.parseLong(getArguments().getString("topicId"));
		courseName = getArguments().getString("courseName");

		MoodleCourseContent[] courseTopics = new Contents()
				.getCourseContent(courseId, getResources(), getActivity()
						.getApplicationContext());

		MoodleCourseContent singleTopic = new Contents().getTopic(topicId,
				courseTopics);

		return createTopics(singleTopic, courseName, courseId, topicId);
	}

	private View createTopics(MoodleCourseContent singleTopic,
			String courseName, String courseId, Long topicId) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		String topicName = singleTopic.getName();
		View inflateHeader = onCreateHeader(courseName, topicName, inflater);

		insertPoint.addView(inflateHeader);

		createTopicsContent(singleTopic, inflater, insertPoint);

		return insertPoint;
	}

	private View onCreateHeader(String courseName, String topicName,
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

	/**
	 * @param singleTopic
	 * @param inflater
	 * @param insertPoint
	 * @param topicId
	 */
	protected void createTopicsContent(MoodleCourseContent singleTopic,
			LayoutInflater inflater, LinearLayout insertPoint) {

		MoodleModule[] modulesArray = singleTopic.getMoodleModules();

		if (modulesArray != null)
			getModules(inflater, insertPoint, modulesArray);

	}

	/**
	 * @param inflater
	 * @param insertPoint
	 * @param modulesArray
	 */
	private void getModules(LayoutInflater inflater, LinearLayout insertPoint,
			MoodleModule[] modulesArray) {

		for (int i = 0; i < modulesArray.length; i++) {

			MoodleModule singleModule = modulesArray[i];

			LinearLayout row = new LinearLayout(getActivity());
			row.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			View topicsContent = inflater.inflate(R.layout.topic, null);

			TextView moduleName = (TextView) topicsContent
					.findViewById(R.id.module_label);
			TextView topicContent = (TextView) topicsContent
					.findViewById(R.id.module_text_content);
			if (!singleModule.getDescription().isEmpty()) {
				
				
				
				moduleName.setVisibility(View.VISIBLE);
				moduleName.setText(singleModule.getName());

				topicContent.setVisibility(View.VISIBLE);
				String moduleDescription = singleModule.getDescription();

				topicContent.setText(Html.fromHtml(moduleDescription));
				topicContent
						.setMovementMethod(LinkMovementMethod.getInstance());
			}

			if (singleModule.getContent() != null) {
				getModuleContents(singleModule, topicsContent);
			}

			row.addView(topicsContent);
			insertPoint.addView(row);
		}
	}

	/**
	 * @param singleModule
	 * @param topicsContent
	 */
	private void getModuleContents(MoodleModule singleModule, View topicsContent) {
		MoodleModuleContent[] moduleContents = singleModule.getContent();

		for (int j = 0; j < moduleContents.length; j++) {

			if (!moduleContents[j].getFileURL().isEmpty()) {

				TextView moduleFile = (TextView) topicsContent
						.findViewById(R.id.module_files);
				moduleFile.setVisibility(View.VISIBLE);
				String url = moduleContents[j].getFileURL();

				if (moduleContents[j].getType().equals("file")) {
					url += "&token="
							+ session.getValues(MoodyConstants.KEY_TOKEN, null);
					if (!(moduleContents[j].getFilename()
							.equalsIgnoreCase("index.html"))) {

						moduleFile.setText(Html.fromHtml("<a href=" + url + ">"
								+ moduleContents[j].getFilename() + "</a>"));

						moduleFile.setMovementMethod(LinkMovementMethod
								.getInstance());

					} else {

						// the index fileName is
						// contentFileName+CourseId+TopicId+ContentId
						String indexURL = new Contents().parseFile(
								getActivity().getApplicationContext(), url,
								moduleContents[j].getFilename() + courseId
										+ topicId + j);

						if (indexURL.contains("youtube")) {

							moduleFile.setText(indexURL);

						} else {
							moduleFile.setText(Html.fromHtml(indexURL));

						}
						Linkify.addLinks(moduleFile, Linkify.WEB_URLS);
					}
				}
				if (moduleContents[j].getType().equals("url")) {
					String fileName = moduleContents[j].getFilename();
					fileName = ((fileName == null) || fileName.isEmpty()) ? "External Content"
							: moduleContents[j].getFilename();

					moduleFile.setText(Html.fromHtml("<a href=" + url + ">"
							+ fileName + "</a>"));

					moduleFile.setMovementMethod(LinkMovementMethod
							.getInstance());

				}

			}

		}
	}
}
