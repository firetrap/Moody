package fragments;

import managers.ManContents;
import managers.ManSession;
import model.ModConstants;
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

import com.android.moody.R;

/**
 * @author firetrap
 *
 */
public class FragTopics extends Fragment {

	Object course;
	// ManSession Manager Class
	ManSession session;
	String courseId;
	Long topicId;
	String courseName;

	public FragTopics() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		session = new ManSession(getActivity().getApplicationContext());

		courseId = getArguments().getString("courseId");
		topicId = Long.parseLong(getArguments().getString("topicId"));
		courseName = getArguments().getString("courseName");

		MoodleCourseContent[] courseTopics = new ManContents()
				.getCourseContent(courseId, getResources(), getActivity()
						.getApplicationContext());

		MoodleCourseContent singleTopic = new ManContents().getTopic(topicId,
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

			View topicsContent = inflater.inflate(R.layout.topic, null);

			TextView moduleName = (TextView) topicsContent
					.findViewById(R.id.module_label);
			TextView topicContent = (TextView) topicsContent
					.findViewById(R.id.module_text_content);

			MoodleModule singleModule = modulesArray[i];

			LinearLayout row = new LinearLayout(getActivity());
			row.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			int b = 0;
			if (!singleModule.getName().isEmpty()) {
				moduleName.setText(singleModule.getName());

				if (!singleModule.getDescription().isEmpty()) {
					String moduleDescription = singleModule.getDescription();
					topicContent.setText(Html.fromHtml(moduleDescription));
					topicContent.setMovementMethod(LinkMovementMethod
							.getInstance());
				} else {
					topicContent.setVisibility(View.GONE);
					b++;

				}
				if (singleModule.getContent() != null) {
					getModuleContents(singleModule, topicsContent);
				} else {
					topicsContent.findViewById(R.id.module_files)
							.setVisibility(View.GONE);
					b++;

				}

				if (b < 2) {
					row.addView(topicsContent);
					insertPoint.addView(row);
				}
			}
		}

	}

	/**
	 * @param singleModule
	 * @param topicsContent
	 */
	private void getModuleContents(MoodleModule singleModule, View topicsContent) {
		MoodleModuleContent[] moduleContents = singleModule.getContent();

		TextView moduleFile = (TextView) topicsContent
				.findViewById(R.id.module_files);
		for (int j = 0; j < moduleContents.length; j++) {

			if (!moduleContents[j].getFileURL().isEmpty()) {

				String url = moduleContents[j].getFileURL();

				if (moduleContents[j].getType().equals("file")) {
					url += "&token="
							+ session.getValues(ModConstants.KEY_TOKEN, null);

					if (!moduleContents[j].getFilename().equalsIgnoreCase(
							"index.html")) {

						moduleFile.setText(Html.fromHtml("<a href=" + url + ">"
								+ moduleContents[j].getFilename() + "</a>"));
						moduleFile.setCompoundDrawablesWithIntrinsicBounds(
								getCorrectDrawable(url), 0, 0, 0);

						moduleFile.setMovementMethod(LinkMovementMethod
								.getInstance());

					} else if (moduleContents[j].getFilename()
							.equalsIgnoreCase("index.html")) {

						// the fileName is
						// contentFileName+CourseId+TopicId+ModuleId+content
						// number
						String indexURL = new ManContents().parseFile(
								getActivity().getApplicationContext(), url,
								moduleContents[j].getFilename() + courseId
										+ topicId + singleModule.getId());

						// if (indexURL.contains("youtube")) {

						moduleFile.setText(indexURL);
						moduleFile.setCompoundDrawablesWithIntrinsicBounds(
								getCorrectDrawable(indexURL), 0, 0, 0);
						// }
						// else {
						// moduleFile.setText(Html.fromHtml(indexURL));
						//
						// }

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

	// Because android doesn't support string switch i've to do with if else
	// statements
	public int getCorrectDrawable(String url) {

		if (url.contains(".youtube.")) {
			return R.drawable.youtube;
		} else if (url.contains(".pdf")) {
			return R.drawable.pdf;
		} else if (url.contains(".doc")) {
			return R.drawable.docs;
		} else if (url.contains(".ppt")) {
			return R.drawable.ppt;
		} else if (url.contains(".xls")) {
			return R.drawable.xls;
			// } else if (url.contains(".zip") || url.contains(".rar")) {
			// return R.drawable.zip;
			// } else if (url.contains(".mp3")) {
			// return R.drawable.mp3;
		}
		return 0;

	}
}
