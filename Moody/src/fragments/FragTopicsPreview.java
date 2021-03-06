package fragments;

import managers.ManDataStore;
import managers.ManFavorites;
import managers.ManSession;
import model.ModConstants;
import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleRestCourse;
import restPackage.MoodleServices;
import ads.MoodyAds;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firetrap.moody.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

/**
 * @author firetrap
 *
 */
public class FragTopicsPreview extends Fragment {
	// ManSession Manager Class
	ManSession session;
	private LinearLayout mainLayout;
	private ScrollView contentScrollable;
	private LinearLayout contentsLayout;
	private String courseId;
	private String courseName;
	private String topicId;
	private Context context;
	private String userId;
	private ManDataStore data;
	private AdView adView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.context = getActivity().getApplicationContext();

		session = new ManSession(context);

		userId = session.getValues(ModConstants.KEY_ID, null);

		data = new ManDataStore(context);

		courseName = getArguments().getString("courseName");

		courseId = getArguments().getString("courseId");

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
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		initLayouts();

		View topicsHeaderView = createTopicsHeader(CourseName, courseId, inflater);
		// Add the header to the main Layout
		mainLayout.addView(topicsHeaderView, 0);

		createAdView();

		createTopicsContent(inflater, contentsLayout, courseId);

		// add the linearLayout with the contents to scrollView
		contentScrollable.addView(contentsLayout);

		// add the scrollView with the contents to mainLayout
		mainLayout.addView(contentScrollable);

		return mainLayout;

	}

	private void createAdView() {
		// Add adMobView to the mainLayout
		mainLayout.addView(new MoodyAds(getActivity()).createAdmobSmartBanner(), 1);
	}

	@Override
	public void onResume() {
		if (adView != null)
			adView.resume();
		super.onResume();
	}

	@Override
	public void onPause() {
		if (adView != null)
			adView.pause();
		super.onPause();
	}

	@Override
	public void onDestroy() {
		if (adView != null)
			adView.destroy();
		super.onDestroy();
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
		mainLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		mainLayout.setOrientation(LinearLayout.VERTICAL);

		// The scrollView responsible to scroll the contents layout
		contentScrollable = new ScrollView(getActivity());
		contentScrollable.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		contentScrollable.setVerticalScrollBarEnabled(false);
		contentScrollable.setHorizontalScrollBarEnabled(false);
		LinearLayout.LayoutParams scroll_params = (LinearLayout.LayoutParams) contentScrollable.getLayoutParams();
		scroll_params.setMargins(0, 10, 0, 0);
		contentScrollable.setLayoutParams(scroll_params);

		// The linearLayout which wrap all the topics, this linearLayout is
		// inside the scrollView
		contentsLayout = new LinearLayout(getActivity());
		contentsLayout.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		contentsLayout.setOrientation(LinearLayout.VERTICAL);
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
	protected View createTopicsHeader(String CourseName, String courseId, LayoutInflater inflater) {
		View topicsHeaderView = inflater.inflate(R.layout.frag_header, null);
		int id = Integer.parseInt(courseId);

		TextView courseName = (TextView) topicsHeaderView.findViewById(R.id.course_path_textView);

		courseName.setText(Html.fromHtml("Courses ><font color=#52c2ea>" + CourseName + "</font>"));

		// If the course is already cached as favorite, hides the button,
		// otherwise configs it's ID
		if (!new ManFavorites(getActivity().getApplicationContext()).isFavorite(id)) {
			ImageButton addFavorites = (ImageButton) topicsHeaderView.findViewById(R.id.add_favorites_button);

			addFavorites.setId(id);
			addFavorites.setTag("add_favorites_button_" + courseId);
		} else
			((ImageButton) topicsHeaderView.findViewById(R.id.add_favorites_button)).setVisibility(View.VISIBLE);

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
	protected void createTopicsContent(LayoutInflater inflater, LinearLayout insertPoint, String courseId) {
		String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + courseId + userId;

		MoodleCourseContent[] courseContent = (MoodleCourseContent[]) data.getData(fileName);

		for (int j = 0; j < courseContent.length; j++) {

			MoodleModule[] modules = courseContent[j].getMoodleModules();
			if (modules != null) {
				LinearLayout row = new LinearLayout(getActivity());
				row.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View topicsView = inflater.inflate(R.layout.frag_topics_preview_context, null);

				TextView topicName = (TextView) topicsView.findViewById(R.id.topics_preview_title);
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
				TextView topicModule = (TextView) topicsView.findViewById(R.id.topics_preview_description);
				topicModule.setText(moduleName);
				// Where the LinearLayout from FragTopicsPreview id will be
				// course
				// id and the LinearLayout
				// tag will be the topic id
				LinearLayout layout = (LinearLayout) topicsView.findViewById(R.id.topics_preview_layout);
				layout.setId(Integer.parseInt(courseId));
				layout.setTag(Long.toString(courseContent[j].getId()));
				onClick(courseContent, layout, j);
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
	private void onClick(final MoodleCourseContent[] courseContent, View layout, final int contentIdx) {

		layout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				// TODO Auto-generated method stub
				courseId = Integer.toString(v.getId());
				topicId = (String) v.getTag();

				String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + courseId + userId;

				if (!data.isInCache(fileName))
					new FragTopicsPreviewDataAsyncTask().execute(MoodleServices.CORE_COURSE_GET_CONTENTS, fileName, courseId);
				else
					getCourseContentsAsyncResult();

			}
		});
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	private class FragTopicsPreviewDataAsyncTask extends AsyncTask<Object, Void, Object> {
		private ProgressDialog dialog;
		private CountDownTimer cvt = createCountDownTimer();
		private MoodleServices webService;
		private String fileName2Store;

		public FragTopicsPreviewDataAsyncTask() {
			dialog = new ProgressDialog(context);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cvt.start();
		}

		@Override
		protected Object doInBackground(Object... params) {
			String serverUrl = session.getValues(ModConstants.KEY_URL, null);
			String token = session.getValues(ModConstants.KEY_TOKEN, null);
			webService = (MoodleServices) params[0];
			fileName2Store = (String) params[1];
			String webServiceParams = (String) params[2];

			MoodleCallRestWebService.init(serverUrl + "/webservice/rest/server.php", token);
			long courseId;
			try {
				switch (webService) {
				case CORE_COURSE_GET_CONTENTS:
					courseId = Long.parseLong(webServiceParams);
					MoodleCourseContent[] courseContent = MoodleRestCourse.getCourseContent(courseId, null);
					return courseContent;

				default:
					return null;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object asyncTaskObj) {
			// Store all objects in cache for future faster access
			if (asyncTaskObj != null)
				data.storeData(asyncTaskObj, fileName2Store);

			cvt.cancel();

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (webService) {

			case CORE_COURSE_GET_CONTENTS:
				getCourseContentsAsyncResult();
				break;

			default:
				break;
			}
		}

		private CountDownTimer createCountDownTimer() {
			return new CountDownTimer(250, 10) {
				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					dialog = new ProgressDialog(context);
					dialog.setMessage("Loading...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			};
		}
	}

	private void getCourseContentsAsyncResult() {
		Bundle bundle = new Bundle();
		bundle.putString("courseId", courseId);
		bundle.putString("courseName", courseName);
		bundle.putString("topicId", topicId);

		FragmentManager fragmentManager = getActivity().getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

		FragTopics insideTopicsFrag = new FragTopics();
		insideTopicsFrag.setArguments(bundle);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.mainFragment, insideTopicsFrag, courseId + topicId);
		fragmentTransaction.commit();
	}

}
