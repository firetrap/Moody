package fragments;

import managers.DataStore;
import managers.Session;
import model.EnumWebServices;
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

import connections.ExternalFiles;

public class Topics extends Fragment {

	Object course;
	// Session Manager Class
	Session session;

	public Topics() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new Session(getActivity().getApplicationContext());

		String courseId = getArguments().getString("courseId");
		Long topicId = Long.parseLong(getArguments().getString("topicId"));
		String courseName = getArguments().getString("courseName");
		Context activityContext = getActivity().getApplicationContext();

		String fileName = EnumWebServices.CORE_COURSE_GET_CONTENTS.name()
				+ courseId;

		course = new DataStore().getData(activityContext, fileName);
		MoodleCourseContent[] courseTopics = (MoodleCourseContent[]) course;
		MoodleCourseContent topic = getTopic(topicId, courseTopics);

		return createTopics(topic, courseName, courseId, topicId);
	}

	/**
	 * @param topicId
	 * @param courseContent
	 * @return
	 */
	private MoodleCourseContent getTopic(Long topicId,
			MoodleCourseContent[] courseContent) {
		for (int j = 0; j < courseContent.length; j++) {
			if (courseContent[j].getId() == topicId) {
				return courseContent[j];
			}

		}
		return null;
	}

	private View createTopics(MoodleCourseContent topic, String courseName,
			String courseId, Long topicId) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		String topicName = topic.getName();

		View inflateHeader = createTopicsHeader(courseName, topicName, inflater);

		insertPoint.addView(inflateHeader);

		createTopicsContent(topic, inflater, insertPoint, topicId);

		return insertPoint;
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

	protected void createTopicsContent(MoodleCourseContent topic,
			LayoutInflater inflater, LinearLayout insertPoint, Long topicId) {

		MoodleModule[] modulesArray = topic.getMoodleModules();

		if (modulesArray != null) {

			for (int i = 0; i < modulesArray.length; i++) {
				MoodleModule singleModule = modulesArray[i];
				LinearLayout row = new LinearLayout(getActivity());
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View topicsContent = inflater.inflate(R.layout.topic, null);

				String moduleName = singleModule.getName();
				TextView topicLabel = (TextView) topicsContent
						.findViewById(R.id.topic_label);
				topicLabel.setText(moduleName);

				TextView topicContent = (TextView) topicsContent
						.findViewById(R.id.topic_content);
				if (!singleModule.getDescription().isEmpty()) {
					String moduleDescription = singleModule.getDescription();

					topicContent.setText(Html.fromHtml(moduleDescription));
				}

				if (singleModule.getContent() != null) {
					MoodleModuleContent[] moduleContents = singleModule
							.getContent();

					for (int j = 0; j < moduleContents.length; j++) {
						TextView moduleFile = (TextView) topicsContent
								.findViewById(R.id.topic_file);

						String url = moduleContents[j].getFileURL()
								+ "&token="
								+ session.getValues(MoodyConstants.KEY_TOKEN,
										null);

						if (moduleContents[j].getFilename().equalsIgnoreCase(
								"index.html")) {
							String indexURL = new ExternalFiles().getParseFile(
									url, moduleContents[j].getFilename() + j);
							url = indexURL;
							moduleFile.setText(Html.fromHtml(url));
							Linkify.addLinks(moduleFile, Linkify.ALL);
							// moduleFile.setMovementMethod(LinkMovementMethod
							// .getInstance());
						} else {

							moduleFile.setText(Html.fromHtml("<a href=" + url
									+ ">" + moduleContents[j].getFilename()
									+ "</a>"));

							moduleFile.setMovementMethod(LinkMovementMethod
									.getInstance());
						}
					}

				}

				row.addView(topicsContent);
				insertPoint.addView(row);
			}

		}

	}
}
