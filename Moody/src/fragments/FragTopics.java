package fragments;

import interfaces.FragmentUpdater;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.examples.HtmlToPlainText;

import managers.ManContents;
import managers.ManSession;
import model.ModConstants;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firetrap.moody.R;

/**
 * @author firetrap
 * 
 */
public class FragTopics extends Fragment {

	Object course;
	ManSession session;
	String courseId;
	String topicId;
	String courseName;
	private LinearLayout mainLayout;
	private ScrollView contentScrollable;
	private LinearLayout contentsLayout;
	private View myView;
	private boolean asyncTaskRunned = false;
	static Context ctx;

	public FragTopics() {
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = getActivity().getApplicationContext();

		session = new ManSession(getActivity().getApplicationContext());

		courseId = getArguments().getString("courseId");
		topicId = getArguments().getString("topicId");
		courseName = getArguments().getString("courseName");
		if (!asyncTaskRunned) {
			if (Build.VERSION.SDK_INT >= 11)
				new HeavyWork()
						.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new HeavyWork().execute();

		}
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

		String summaryContent = singleTopic.getSummary();
		if (!summaryContent.isEmpty()) {

			View topicSummary = inflater.inflate(R.layout.topic_summary, null);

			TextView summary = (TextView) topicSummary
					.findViewById(R.id.summary_text);
			summary.setText(Html.fromHtml(summaryContent));
			insertPoint.addView(topicSummary);
		}
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

				if (!Html.fromHtml(singleModule.getDescription()).toString()
						.isEmpty()) {

					String moduleDescription = singleModule.getDescription();

					String parsed = new ManContents(getActivity()
							.getApplicationContext())
							.parseFile(moduleDescription);
					String cleared = clearSource(parsed);

					/**
					 * 
					 * Added a link to image because the impossibility of access
					 * to images inside description, the function is prepared to
					 * receive an image and display it. ISSUE reported in
					 * https://tracker.moodle.org/browse/MDL-43513
					 * 
					 */
					if (cleared.contains("youtube")) {
						TextView bla = new TextView(getActivity());

						bla.setText(cleared);
						bla.setCompoundDrawablesWithIntrinsicBounds(
								getCorrectDrawable(cleared), 0, 0, 0);
						Linkify.addLinks(bla, Linkify.ALL);

						LinearLayout recebe = (LinearLayout) topicsContent
								.findViewById(R.id.test_ll);
						recebe.addView(bla);

						// String cleared = clearSource(parsed);
						// topicContent.setText(cleared);
						// topicContent.setCompoundDrawablesWithIntrinsicBounds(
						// getCorrectDrawable(cleared), 0, 0, 0);
						// Linkify.addLinks(topicContent, Linkify.ALL);
					}

					if (cleared.contains("image")) {
						// topicContent.setVisibility(View.GONE);
						// moduleImage.setVisibility(View.VISIBLE);
						// moduleImage
						// .setImageDrawable(LoadImageFromWebOperations(parsed));

						topicContent.setText(Html.fromHtml("<a href=" + parsed
								+ ">" + "Image - accessible only from broswer"
								+ "</a>"));
						topicContent.setCompoundDrawablesWithIntrinsicBounds(
								getCorrectDrawable(parsed), 0, 0, 0);
						topicContent.setMovementMethod(LinkMovementMethod
								.getInstance());

					} else {

						topicContent.setText(Html.fromHtml(moduleDescription));
						topicContent.setMovementMethod(LinkMovementMethod
								.getInstance());
					}

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

		// ImageView moduleImage = (ImageView) topicsContent
		// .findViewById(R.id.module_image);

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
						if (clearSource(indexURL).contains("image")) {
							// moduleFile.setVisibility(View.GONE);
							// moduleImage.setVisibility(View.VISIBLE);
							// moduleImage
							// .setImageDrawable(LoadImageFromWebOperations(indexURL));

						} else {
							moduleFile.setText(indexURL);
							moduleFile.setCompoundDrawablesWithIntrinsicBounds(
									getCorrectDrawable(indexURL), 0, 0, 0);

							Linkify.addLinks(moduleFile, Linkify.WEB_URLS);

						}

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

	public static Drawable LoadImageFromWebOperations(String url) {
		try {
			InputStream is = (InputStream) new URL(url).getContent();
			Drawable d = Drawable.createFromStream(is, null);
			return d;
		} catch (Exception e) {
			return null;
		}
	}

	public String clearSource(String src) {
		if (src.contains("youtube")) {
			String cleaned = src.split("\\&")[0];
			// return src.split("\\?")[0].replace("v/", "watch?v=");
			return cleaned.replace("v/", "watch?v=");
		}
		String mimeType = getMimeType(src);
		if (mimeType != null) {
			if (mimeType.contains("image")) {
				return mimeType;
			}
		}

		return src;
	}

	/**
	 * 
	 * Return the correct draw to the view
	 * 
	 * @param url
	 * @return drawable id
	 */
	private int getCorrectDrawable(String url) {

		if (url.contains(".youtube."))
			return R.drawable.youtube;

		if (getMimeType(url) != null) {
			if (getMimeType(url).equalsIgnoreCase("application/pdf")) {
				return R.drawable.pdf;
			}
			if (getMimeType(url).equalsIgnoreCase("application/msword")
					|| getMimeType(url)
							.equalsIgnoreCase(
									"application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
				return R.drawable.docs;
			}
			if (getMimeType(url).equalsIgnoreCase(
					"application/vnd.ms-powerpoint")
					|| getMimeType(url)
							.equalsIgnoreCase(
									"application/vnd.openxmlformats-officedocument.presentationml.presentation")) {
				return R.drawable.ppt;
			}
			if (getMimeType(url).equalsIgnoreCase("application/vnd.ms-excel")
					|| getMimeType(url)
							.equalsIgnoreCase(
									"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")) {
				return R.drawable.xls;
			}

		}

		return R.drawable.generic;

	}

	public static String getMimeType(String url) {
		String type = null;
		String extension = MimeTypeMap.getFileExtensionFromUrl(url);

		if (extension != null) {
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			type = mime.getMimeTypeFromExtension(extension);
			// writeStringAsFile(type + "\n", "mime.txt");

		}
		return type;

	}

	public static void writeStringAsFile(final String fileContents,
			String fileName) {

		try {
			FileWriter out = new FileWriter(new File(ctx.getFilesDir(),
					fileName), true);
			out.write(fileContents);
			out.close();
		} catch (IOException e) {

		}
	}

	@Override
	public void onResume() {

		super.onResume();
	}

	private class HeavyWork extends AsyncTask<Void, Void, Void> {

		private ProgressDialog dialog;

		final FragmentUpdater activity = (FragmentUpdater) getActivity();
		private CountDownTimer cvt = createCountDownTimer();

		// Do the long-running work in here
		@Override
		protected Void doInBackground(Void... params) {
			MoodleCourseContent[] courseTopics = new ManContents(getActivity()
					.getApplicationContext()).getContent(courseId);

			MoodleCourseContent singleTopic = new ManContents(getActivity()
					.getApplicationContext()).getTopic(Long.parseLong(topicId),
					courseTopics);
			// This createTopics call another methods from the fragment class to
			// get the data and create views
			myView = createTopics(singleTopic, courseName, courseId,
					Long.parseLong(topicId));
			if (dialog != null)
				dialog.dismiss();
			return null;

		}

		protected void onPreExecute() {
			asyncTaskRunned = true;
			cvt.start();
		}

		// This is called when doInBackground() is finished
		@Override
		protected void onPostExecute(Void ignore) {
			cvt.cancel();
			activity.updater(myView, courseId, topicId);
			if (dialog != null)
				dialog.dismiss();
		}

		private CountDownTimer createCountDownTimer() {
			return new CountDownTimer(250, 10) {
				@Override
				public void onTick(long millisUntilFinished) {

				}

				@Override
				public void onFinish() {
					dialog = new ProgressDialog(getActivity());
					dialog.setMessage("Loading...");
					dialog.show();
				}
			};
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

}
