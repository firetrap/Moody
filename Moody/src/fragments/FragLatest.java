package fragments;

import java.util.LinkedList;

import managers.ManContents;
import managers.ManDataStore;
import model.ObjectLatest;
import restPackage.MoodleCourseContent;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.moody.R;

/**
 * @author firetrap
 * 
 */
public class FragLatest extends Fragment {
	LinkedList<ObjectLatest> latestList;
	// Get from resource the number of cards per line
	int cardsPerLine;
	private LinearLayout mainLayout;
	private ScrollView contentScrollable;
	private LinearLayout contentsLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (new ManDataStore(getActivity()).isInCache("Latest")) {
			initLayouts();
			initResources();
			mainLayout.addView(setLayoutTitle());

			// Calculate the number of rows to build
			int lines = setHowManyRows(latestList, cardsPerLine);

			for (int i = 0; i < lines; i++) {
				// The inner Horizontal Linear Layout
				LinearLayout innerLayout = createHorizontalLinearLayout();

				for (int j = 0; j < cardsPerLine; j++) {
					if (!latestList.isEmpty()) {
						final ObjectLatest latest = latestList.getFirst();
						latestList.removeFirst();

						MoodleCourseContent[] course = new ManContents(
								getActivity()).getContent(latest.getCourseId());
						MoodleCourseContent topic = new ManContents(
								getActivity()).getTopic(
								Long.parseLong(latest.getTopicId()), course);

						View latestView = inflateLatestView(inflater);
						setCourseTitle(latest, latestView);
						setTopicTitle(topic, latestView);
						setContentDescription(latest, latestView);
						onClick(innerLayout, latest);
						innerLayout.addView(latestView);
					}

				}
				contentsLayout.addView(innerLayout);

			}

			contentScrollable.addView(contentsLayout);
			mainLayout.addView(contentScrollable);
			return mainLayout;
		} else {
			return inflater.inflate(R.layout.frag_empty_latest, null);
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
	 * @param innerLayout
	 * @param latest
	 */
	private void onClick(LinearLayout innerLayout, final ObjectLatest latest) {
		innerLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Bundle bundle = new Bundle();
				bundle.putString("courseId", latest.getCourseId());
				bundle.putString("courseName", latest.getCourseName());
				bundle.putString("topicId", latest.getTopicId());

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

	/**
	 * @param inflater
	 * @return
	 */
	private View inflateLatestView(LayoutInflater inflater) {
		View view = inflater.inflate(R.layout.frag_latest, null);
		view.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1f));
		return view;
	}

	/**
	 * This method is necessary because the fragment life cycle, the context
	 * needed it's only available after the onCreate it's triggered
	 */
	@SuppressWarnings("unchecked")
	private void initResources() {
		latestList = (LinkedList<ObjectLatest>) new ManDataStore(getActivity())
				.getData("Latest");
		if (latestList.size() > 20) {
			latestList.subList(
					0,
					latestList.size()
							- getResources().getInteger(
									R.integer.latest_max_limit)).clear();
		}
		// Get from resource the number of cards per line
		cardsPerLine = getResources()
				.getInteger(R.integer.latest_item_per_line);
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
		large_title.setText("Latest Contents");
		large_title.setTextAppearance(getActivity(), R.style.CardsLayoutTitle);
		// large_title.setTypeface(null, Typeface.ITALIC);
		large_title.setPadding(30, 10, 0, 10);
		return large_title;
	}

	/**
	 * @param latest
	 * @param cardsPerLine
	 * @return
	 */
	private int setHowManyRows(LinkedList<ObjectLatest> latest, int cardsPerLine) {
		int colums;
		if (latest.size() > cardsPerLine) {
			Double count = Math.ceil(latest.size() / 2.0);
			colums = count.intValue();
		} else {
			colums = 1;
		}
		return colums;
	}

	/**
	 * @param latest
	 * @param view
	 */
	private void setCourseTitle(ObjectLatest latest, View view) {
		TextView courseTitle = (TextView) view
				.findViewById(R.id.latest_preview_course_title);
		courseTitle.setText(latest.getCourseName());
	}

	/**
	 * @param topic
	 * @param view
	 */
	private void setTopicTitle(MoodleCourseContent topic, View view) {
		TextView topicTitle = (TextView) view
				.findViewById(R.id.latest_preview_topic_title);
		topicTitle.setText(topic.getName());
	}

	/**
	 * @param latest
	 * @param view
	 */
	private void setContentDescription(ObjectLatest latest, View view) {
		TextView contentDescription = (TextView) view
				.findViewById(R.id.latest_preview_description);
		contentDescription.setText(latest.getNewContent());
	}

}