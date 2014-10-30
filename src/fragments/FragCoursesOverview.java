package fragments;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import managers.ManDataStore;
import managers.ManFavorites;
import managers.ManSession;
import model.ModCheckConnection;
import model.ModConstants;
import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleRestCourse;
import restPackage.MoodleServices;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.firetrap.moody.R;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

/**
 * @author FBARREI1 Fragment to display user courses in the mainActivity when
 *         its initialised, in another words a all courses overview
 *
 */
public class FragCoursesOverview extends Fragment {
	MoodleCourse[] courses;

	// Get from resource the number of cards per line
	int cardsPerLine;

	private LinearLayout mainLayout;

	private ScrollView contentScrollable;

	private LinearLayout contentsLayout;

	private HashMap<String, String> organizedCourses;

	private ArrayList<String> ids;

	private ArrayList<String> names;

	Context context;

	private ManSession session;

	private String userId;

	private ManDataStore data;

	int countOrganizedCourses = 0;

	int mAlreadyLoaded = 0;

	boolean fromBackStack = false;

	private View myView;

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		mAlreadyLoaded++;

		if (mAlreadyLoaded >= 2)
			fromBackStack = true;

		context = getActivity();

		// Cache data store
		data = new ManDataStore(context);

		// shared pref
		session = new ManSession(context);

		// Logged user id
		userId = session.getValues(ModConstants.KEY_ID, null);

		Bundle bundle = this.getArguments();
		// Courses organized by Id->Name

		organizedCourses = (HashMap<String, String>) bundle.getSerializable("organizedCourses");

		initLayouts();

		cardsPerLine = getResources().getInteger(R.integer.latest_item_per_line);

		mainLayout.addView(setLayoutTitle());

		// Get all the courses from current user
		initCoursesList();

		new FragCoursesOverviewDataAsyncTask().execute();

		// initCoursesContents();

		return myView;
	}

	private void initCoursesList() {
		ids = new ArrayList<String>();
		names = new ArrayList<String>();

		for (Entry<String, String> entry : organizedCourses.entrySet()) {
			ids.add(entry.getKey());
			names.add(entry.getValue());
		}

	}

	private void initCoursesContents() {
		// for (Entry<String, String> entry : organizedCourses.entrySet()) {
		// String courseId = entry.getKey();
		// String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() +
		// courseId + userId;
		//
		// if (!data.isInCache(fileName)) {
		// if (!new ModCheckConnection(context).hasConnection())
		// warningMessage(new ModCheckConnection(context).hasConnection(),
		// Toast.LENGTH_LONG, null,
		// getString(R.string.no_internet));
		// else {
		// MoodleCallRestWebService.init(serverUrl +
		// "/webservice/rest/server.php", token);
		// long courseId;
		// try {
		//
		// courseId = Long.parseLong((String) webServiceParams);
		// MoodleCourseContent[] courseContent =
		// MoodleRestCourse.getCourseContent(courseId, null);
		// return courseContent;
		//
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }
		//
		// }
		// }

	}

	private void setCardTitle(View v, String text) {
		TextView courseTitleField = (TextView) v.findViewById(R.id.course_title);
		courseTitleField.setText(text);
	}

	private void setContent(View v, String text) {
		TextView contentDescription = (TextView) v.findViewById(R.id.courses_preview_description);
		contentDescription.setText(text);
	}

	/**
	 * @return
	 */
	private LinearLayout createHorizontalLinearLayout() {
		LinearLayout innerLayout = new LinearLayout(getActivity());
		LinearLayout.LayoutParams layoutParams = new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		innerLayout.setLayoutParams(layoutParams);
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);

		return innerLayout;
	}

	/**
	 *
	 * This method is responsible to initialise the required layouts
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
	 * @param latestView
	 * @param courses
	 */
	private void onClick(View latestView, final String courseId, final String courseName) {
		latestView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String courseName2 = courseName;
				String courseId2 = courseId;

				Bundle bundle = new Bundle();
				bundle.putString("courseName", courseName2);
				bundle.putString("courseId", courseId2);

				FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
				FragTopicsPreview fragment = new FragTopicsPreview();

				fragment.setArguments(bundle);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.replace(R.id.mainFragment, fragment);
				fragmentTransaction.commit();
			}
		});
	}

	/**
	 * @param inflater
	 * @return
	 */
	private View inflateLatestView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.frag_courses_overview, null);
		view.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1f));
		return view;
	}

	/**
	 * @param context
	 * @return
	 */
	private TextView setLayoutTitle() {
		TextView large_title = new TextView(getActivity());
		large_title.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		large_title.setText("Courses");
		large_title.setTextAppearance(getActivity(), R.style.CardsLayoutTitle);
		// large_title.setTypeface(null, Typeface.ITALIC);
		large_title.setPadding(30, 10, 0, 10);
		return large_title;
	}

	/**
	 * @param organizedCourses2
	 * @param cardsPerLine
	 * @return
	 */
	private int setHowManyRows(HashMap<String, String> organizedCourses2, int cardsPerLine) {
		int colums;
		if (organizedCourses2.size() > cardsPerLine) {
			Double count = Math.ceil(organizedCourses2.size() / 2.0);
			colums = count.intValue();
		} else {
			colums = 1;
		}
		return colums;
	}

	private class FragCoursesOverviewDataAsyncTask extends AsyncTask<Object, Void, Object> {
		private ProgressDialog dialog;
		private CountDownTimer cvt = createCountDownTimer();
		private String fileName;

		public FragCoursesOverviewDataAsyncTask() {
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

			for (Entry<String, String> entry : organizedCourses.entrySet()) {
				String courseId = entry.getKey();
				fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + courseId + userId;

				if (!data.isInCache(fileName)) {
					if (!new ModCheckConnection(context).hasConnection())
						warningMessage(new ModCheckConnection(context).hasConnection(), Toast.LENGTH_LONG, null,
								getString(R.string.no_internet));
					else {
						MoodleCallRestWebService.init(serverUrl + "/webservice/rest/server.php", token);
						long lCourseId;
						try {

							lCourseId = Long.parseLong((String) courseId);
							MoodleCourseContent[] courseContent = MoodleRestCourse.getCourseContent(lCourseId, null);
							// Store all objects in cache for future faster
							// access
							if (courseContent != null)
								data.storeData(courseContent, fileName);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}
				countOrganizedCourses++;
			}

			return null;
		}

		@Override
		protected void onPostExecute(Object asyncTaskObj) {
			cvt.cancel();

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			if (countOrganizedCourses == organizedCourses.size())
				buildView();

			if (fromBackStack) {
				buildView();
				fromBackStack = false;
				countOrganizedCourses = 0;
				mAlreadyLoaded = 0;
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

	private void buildView() {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		int plus = 0;

		// Calculate the number of rows to build
		int lines = setHowManyRows(organizedCourses, cardsPerLine);
		for (int i = 0; i < lines; i++) {
			// The inner Horizontal Linear Layout
			LinearLayout innerLayout = createHorizontalLinearLayout();

			for (int j = 0; j < cardsPerLine; j++) {
				plus++;
				if (plus > ids.size())
					break;
				String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + ids.get(plus - 1) + userId;

				MoodleCourseContent[] courseContent = (MoodleCourseContent[]) data.getData(fileName);

				// MoodleCourseContent[] courseContent = new
				// ManContents(getActivity()).getContent(ids.get(plus - 1));

				View latestView = inflateLatestView(inflater);

				setCardTitle(latestView, names.get(plus - 1));

				// If the course is already cached as favorite, hides the
				// button,
				// otherwise configs it's ID
				if (!new ManFavorites(getActivity().getApplicationContext()).isFavorite(Long.parseLong(ids.get(plus - 1)))) {
					ImageButton addFavorites = (ImageButton) latestView.findViewById(R.id.courses_list_favorites);

					addFavorites.setId(Integer.parseInt(ids.get(plus - 1)));
					addFavorites.setTag("add_favorites_button_" + ids.get(plus - 1));
				} else
					((ImageButton) latestView.findViewById(R.id.courses_list_favorites)).setVisibility(View.GONE);

				StringBuilder topics = new StringBuilder();
				for (int k = 0; k < courseContent.length; k++) {
					topics.append(courseContent[k].getName() + "\n");
				}
				setContent(latestView, topics.toString());
				onClick(latestView, ids.get(plus - 1), names.get(plus - 1));
				innerLayout.addView(latestView);
			}
			contentsLayout.addView(innerLayout);
		}
		contentScrollable.addView(contentsLayout);
		mainLayout.addView(contentScrollable);

		myView = mainLayout;

		relaunchFragment();
	}

	public void relaunchFragment() {
		getFragmentManager().beginTransaction().detach(this).attach(this).commit();
		getFragmentManager().executePendingTransactions();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}

	/**
	 * Send a toast with a warning
	 */
	private void warningMessage(boolean statement, int duration, String sucessMessage, String errorMessage) {
		if (statement) {
			if (sucessMessage != null)
				Toast.makeText(context, sucessMessage, duration).show();
		} else if (errorMessage != null)
			Toast.makeText(context, errorMessage, duration).show();
	}

}
