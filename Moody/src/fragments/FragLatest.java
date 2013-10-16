package fragments;

import java.util.LinkedList;

import managers.ManContents;
import managers.ManDataStore;
import managers.ManLatest;
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
import android.widget.TextView;

import com.android.moody.R;

/**
 * @author firetrap
 * 
 */
public class FragLatest extends Fragment {
	LinkedList<ManLatest> latestList;
	// Get from resource the number of cards per line
	int cardsPerLine;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (new ManDataStore(getActivity()).isInCache("Latest")) {
			initResources();
			// The outer Vertical LinearLayout
			LinearLayout outerLayout = createVerticalLinearLayout();
			outerLayout.addView(setLayoutTitle());

			// Calculate the number of rows to build
			int lines = setHowManyRows(latestList, cardsPerLine);

			for (int i = 0; i < lines; i++) {
				// The inner Horizontal Linear Layout
				LinearLayout innerLayout = createHorizontalLinearLayout();

				for (int j = 0; j < cardsPerLine; j++) {
					if (!latestList.isEmpty()) {
						final ManLatest latest = latestList.getFirst();
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
				outerLayout.addView(innerLayout);

			}

			return outerLayout;
		} else {
			return inflater.inflate(R.layout.frag_empty_latest, null);
		}

	}

	/**
	 * @param innerLayout
	 * @param latest
	 */
	private void onClick(LinearLayout innerLayout, final ManLatest latest) {
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
		latestList = (LinkedList<ManLatest>) new ManDataStore(getActivity())
				.getData("Latest");
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
	private int setHowManyRows(LinkedList<ManLatest> latest, int cardsPerLine) {
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
	 * @return
	 */
	private LinearLayout createVerticalLinearLayout() {
		LinearLayout outerLayout = new LinearLayout(getActivity());
		outerLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		outerLayout.setOrientation(LinearLayout.VERTICAL);
		return outerLayout;
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
	 * @param latest
	 * @param view
	 */
	private void setCourseTitle(ManLatest latest, View view) {
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
	private void setContentDescription(ManLatest latest, View view) {
		TextView contentDescription = (TextView) view
				.findViewById(R.id.latest_preview_description);
		contentDescription.setText(latest.getNewContent());
	}

}