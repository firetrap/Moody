package fragments;

import java.util.LinkedList;

import restPackage.MoodleCourseContent;

import managers.ManContents;
import managers.ManDataStore;
import managers.ManLatest;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.moody.R;

public class FragLatest extends Fragment {

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Context context = getActivity().getApplicationContext();
		ManContents content = new ManContents(context);

		LinkedList<ManLatest> latestList = (LinkedList<ManLatest>) new ManDataStore(
				context).getData("Latest");

		LinearLayout outerLayout = new LinearLayout(context);
		outerLayout.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT));
		outerLayout.setOrientation(LinearLayout.VERTICAL);

		outerLayout.addView(setLayoutTitle(context));

		int cardsPerLine = getResources().getInteger(
				R.integer.latest_item_per_line);
		int colums = setRows(latestList, cardsPerLine);

		for (int i = 0; i < colums; i++) {
			ManLatest latest = latestList.get(i);

			LinearLayout innerLayout = new LinearLayout(context);
			innerLayout.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.MATCH_PARENT));
			innerLayout.setOrientation(LinearLayout.HORIZONTAL);

			for (int j = 0; j < cardsPerLine; j++) {

				Long topicId = Long.parseLong(latest.getTopicId());
				String courseId = latest.getCourseId();
				MoodleCourseContent[] courseContent = content
						.getContent(courseId);
				MoodleCourseContent topic = content.getTopic(topicId,
						courseContent);

				View view = inflater.inflate(R.layout.frag_latest, null);
				view.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.MATCH_PARENT, 1f));
				TextView title = (TextView) view
						.findViewById(R.id.latest_preview_title);
				title.setText(topic.getName());
				TextView description = (TextView) view
						.findViewById(R.id.latest_preview_description);
				description.setText(topic.getSummary());
				innerLayout.addView(view);
			}
			outerLayout.addView(innerLayout);

		}

		return outerLayout;

	}

	/**
	 * @param latest
	 * @param cardsPerLine
	 * @return
	 */
	private int setRows(LinkedList<ManLatest> latest, int cardsPerLine) {
		int colums;
		if (latest.size() > cardsPerLine) {
			Double count = Math.ceil(latest.size() / 2.0);
			colums = count.intValue();
		} else {
			colums = latest.size();
		}
		return colums;
	}

	/**
	 * @param context
	 * @return
	 */
	private TextView setLayoutTitle(Context context) {
		TextView large_title = new TextView(context);
		large_title.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		large_title.setText("Latest Contents");
		large_title.setTextAppearance(context, R.style.CardsLayoutTitle);
		// large_title.setTypeface(null, Typeface.ITALIC);
		large_title.setPadding(30, 10, 0, 10);
		return large_title;
	}

}