package fragments;

import interfaces.FragmentUpdater;
import interfaces.InterDialogFrag;

import java.net.URL;

import managers.ManContents;
import managers.ManSession;
import model.ModConstants;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.moody.R;

/**
 * @author firetrap
 * 
 */
public class FragTopics extends Fragment {

	Object course;
	ManSession session;
	String courseId;
	Long topicId;
	String courseName;
	private LinearLayout mainLayout;
	private ScrollView contentScrollable;
	private LinearLayout contentsLayout;
	private View myView;
	private View myTempView;
	private Fragment myFragment;

	public FragTopics() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		session = new ManSession(getActivity().getApplicationContext());

		courseId = getArguments().getString("courseId");
		topicId = Long.parseLong(getArguments().getString("topicId"));
		courseName = getArguments().getString("courseName");

		// MoodleCourseContent[] courseTopics = new ManContents(getActivity()
		// .getApplicationContext()).getContent(courseId);
		//
		// MoodleCourseContent singleTopic = new ManContents(getActivity()
		// .getApplicationContext()).getTopic(topicId, courseTopics);

		new HeavyWork().execute();

		// return createTopics(singleTopic, courseName, courseId,
		// topicId);
		myView = new View(getActivity());

		myView.setId(78);
		return myView;
	}

	private View createTopics(MoodleCourseContent singleTopic,
			String courseName, String courseId, Long topicId) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		initLayouts();

		View topicsHeaderView = onCreateHeader(courseName,
				singleTopic.getName(), inflater);

		mainLayout.addView(topicsHeaderView);

		createTopicsContent(singleTopic, inflater, contentsLayout);

		contentScrollable.addView(contentsLayout);
		mainLayout.addView(contentScrollable);

		return mainLayout;
	}

	/**
	 * 
	 * This method is responsible to initialize the required layouts
	 * 
	 */
	private void initLayouts() {

		// The mainLayout is a linearLayout witch will wrap another linearLayout
		// with the static header and the scrollable content
		mainLayout = new LinearLayout(getActivity());
		mainLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		// The scrollView responsible to scroll the contents layout
		contentScrollable = new ScrollView(getActivity());
		contentScrollable.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		contentScrollable.setVerticalScrollBarEnabled(false);
		contentScrollable.setHorizontalScrollBarEnabled(false);
		LinearLayout.LayoutParams scroll_params = (LinearLayout.LayoutParams) contentScrollable
				.getLayoutParams();
		scroll_params.setMargins(0, 10, 0, 0);
		contentScrollable.setLayoutParams(scroll_params);

		// The linearLayout which wrap all the topics, this linearLayout is
		// inside the scrollView
		contentsLayout = new LinearLayout(getActivity());
		contentsLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		contentsLayout.setOrientation(LinearLayout.VERTICAL);
	}

	private View onCreateHeader(String courseName, String topicName,
			LayoutInflater inflater) {

		final View topicsHeaderView = inflater.inflate(R.layout.frag_header,
				null);

		final TextView path = (TextView) topicsHeaderView
				.findViewById(R.id.course_path_textView);

		path.setText(Html.fromHtml("Courses > " + courseName + " > "
				+ "<font color=#52c2ea>" + topicName + "</font>"));

		final ImageButton addFavorites = (ImageButton) topicsHeaderView
				.findViewById(R.id.add_favorites_button);
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

			View topicsContent = inflater.inflate(R.layout.frag_topic, null);

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
	 * 
	 * One of the most important method in the project, is responsible to get
	 * the contents from the modules and display it in view according with which
	 * type of data
	 * 
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

						String indexURL = new ManContents(getActivity()
								.getApplicationContext()).parseFile(url,
								moduleContents[j].getFilename() + courseId
										+ topicId + singleModule.getId());

						moduleFile.setText(indexURL);
						moduleFile.setCompoundDrawablesWithIntrinsicBounds(
								getCorrectDrawable(indexURL), 0, 0, 0);

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

	/**
	 * 
	 * Return the correct draw to the view
	 * 
	 * @param url
	 * @return drawable id
	 */
	private int getCorrectDrawable(String url) {

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
		}
		return 0;

	}

	@Override
	public void onResume() {

		super.onResume();
	}

	private class HeavyWork extends AsyncTask<Void, Void, Void> {
		private ProgressDialog dialog;
		final FragmentUpdater activity = (FragmentUpdater) getActivity();

		// Do the long-running work in here
		@Override
		protected Void doInBackground(Void... params) {

			MoodleCourseContent[] courseTopics = new ManContents(getActivity()
					.getApplicationContext()).getContent(courseId);

			MoodleCourseContent singleTopic = new ManContents(getActivity()
					.getApplicationContext()).getTopic(topicId, courseTopics);

			myTempView = createTopics(singleTopic, courseName, courseId,
					topicId);
			return null;

		}

		protected void onPreExecute() {
			dialog = new ProgressDialog(getActivity());
			dialog.show();
		}

		// This is called each time you call publishProgress()
		// protected void onProgressUpdate(Integer... progress) {
		// }

		// This is called when doInBackground() is finished
		@Override
		protected void onPostExecute(Void ignore) {
			myView = myTempView;
			activity.atualizaFragmentComResposta(myTempView);
			dialog.dismiss();
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
