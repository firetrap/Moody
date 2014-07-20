package fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.firetrap.moody.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import managers.ManContents;
import managers.ManFavorites;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;

public class FragCoursesList extends Fragment {
	// LinkedList<ObjectLatest> latestList;
	MoodleCourse[] courses;
	// Get from resource the number of cards per line
	int cardsPerLine;
	private LinearLayout mainLayout;
	private ScrollView contentScrollable;
	private LinearLayout contentsLayout;
	private HashMap<String, String> organizedCourses;
	private ArrayList<String> ids;
	private ArrayList<String> names;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// Courses organized by Id->Name
		Bundle bundle = this.getArguments();
		organizedCourses = (HashMap<String, String>) bundle
				.getSerializable("organizedCourses");

		initLayouts();
		cardsPerLine = getResources()
				.getInteger(R.integer.latest_item_per_line);
		mainLayout.addView(setLayoutTitle());
		// Get all the courses from current user
		initCoursesList();

		if (organizedCourses == null || organizedCourses.size() == 0)
			return new View(getActivity());
		else {

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

					MoodleCourseContent[] courseContent = new ManContents(
							getActivity()).getContent(ids.get(plus - 1));
					View latestView = inflateLatestView(inflater);

					setCardTitle(latestView, names.get(plus - 1));

					// If the course is already cached as favorite, hides the
					// button,
					// otherwise configs it's ID
					if (!new ManFavorites(getActivity().getApplicationContext())
							.isFavorite(Long.parseLong(ids.get(plus - 1)))) {
						ImageButton addFavorites = (ImageButton) latestView
								.findViewById(R.id.courses_list_favorites);

						addFavorites.setId(Integer.parseInt(ids.get(plus - 1)));
						addFavorites.setTag("add_favorites_button_"
								+ ids.get(plus - 1));
					} else
						((ImageButton) latestView
								.findViewById(R.id.courses_list_favorites))
								.setVisibility(View.GONE);

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
			return mainLayout;
		}
	}

	private void setCardTitle(View v, String text) {
		TextView courseTitleField = (TextView) v
				.findViewById(R.id.course_title);
		courseTitleField.setText(text);
	}

	private void setContent(View v, String text) {
		TextView contentDescription = (TextView) v
				.findViewById(R.id.courses_preview_description);
		contentDescription.setText(text);
	}

	private void initCoursesList() {
		ids = new ArrayList<String>();
		names = new ArrayList<String>();

		for (Entry<String, String> entry : organizedCourses.entrySet()) {
			ids.add(entry.getKey());
			names.add(entry.getValue());
		}
	}

	/**
	 * @return
	 */
	private LinearLayout createHorizontalLinearLayout() {
		LinearLayout innerLayout = new LinearLayout(getActivity());
		innerLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		innerLayout.setOrientation(LinearLayout.HORIZONTAL);

		return innerLayout;
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

	/**
	 * @param latestView
	 * @param courses
	 */
	private void onClick(View latestView, final String courseId,
			final String courseName) {
		latestView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String courseName2 = courseName;
				String courseId2 = courseId;

				Bundle bundle = new Bundle();
				bundle.putString("courseName", courseName2);
				bundle.putString("courseId", courseId2);

				FragmentTransaction fragmentTransaction = getFragmentManager()
						.beginTransaction();
				FragTopicsPreview fragment = new FragTopicsPreview();

				fragment.setArguments(bundle);
				// fragmentTransaction.add(fragment, "courses");
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.replace(R.id.mainFragment, fragment);
				fragmentTransaction.commit();
				// moodydrawerLayout.closeDrawer(Gravity.LEFT);
			}
		});
	}

	/**
	 * @param inflater
	 * @return
	 */
	private View inflateLatestView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.frag_courses_list, null);
		view.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1f));
		return view;
	}

	/**
	 * @param context
	 * @return
	 */
	private TextView setLayoutTitle() {
		TextView large_title = new TextView(getActivity());
		large_title.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
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
	private int setHowManyRows(HashMap<String, String> organizedCourses2,
			int cardsPerLine) {
		int colums;
		if (organizedCourses2.size() > cardsPerLine) {
			Double count = Math.ceil(organizedCourses2.size() / 2.0);
			colums = count.intValue();
		} else {
			colums = 1;
		}
		return colums;
	}

}
