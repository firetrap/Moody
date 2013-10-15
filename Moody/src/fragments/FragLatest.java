package fragments;

import java.util.LinkedList;

import managers.ManContents;
import managers.ManDataStore;
import managers.ManLatest;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.moody.R;

public class FragLatest extends Fragment {
	LinkedList<ManLatest> latestList;
	// Get from resource the number of cards per line
	int cardsPerLine;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		initResources();
		// The outer Vertical LinearLayout
		LinearLayout outerLayout = createVerticalLinearLayout();
		outerLayout.addView(setLayoutTitle());
		// Calculate the number of rows to build
		int lines = setRows(latestList, cardsPerLine);
		for (int i = 0; i < lines; i++) {

			// The inner Horizontal Linear Layout
			LinearLayout innerLayout = createHorizontalLinearLayout();

			for (int j = 0; j < cardsPerLine; j++) {
				if (!latestList.isEmpty()) {
					ManLatest latest = latestList.getFirst();
					latestList.removeFirst();

					MoodleCourseContent[] course = new ManContents(
							getActivity()).getContent(latest.getCourseId());

					MoodleCourseContent topic = new ManContents(getActivity())
							.getTopic(Long.parseLong(latest.getTopicId()),
									course);

					MoodleModule[] modules = topic.getMoodleModules();

					View view = inflater.inflate(R.layout.frag_latest, null);
					view.setLayoutParams(new LayoutParams(
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							android.view.ViewGroup.LayoutParams.MATCH_PARENT,
							1f));

					TextView title = (TextView) view
							.findViewById(R.id.latest_preview_title);
					title.setText(topic.getName());

					String moduleName = "";
					// Loop for the modules array
					for (int k = 0; k < topic.getMoodleModules().length; k++) {

						String getNameDirty = modules[k].getName();
						String getNamePure = "";

						for (int n = 0; n < getNameDirty.split("\\s+").length; n++) {
							if (n == 5) {
								break;
							}

							getNamePure += getNameDirty.split("\\s+")[n] + " ";
						}

						moduleName += "-" + getNamePure + "\n";

					}

					TextView description = (TextView) view
							.findViewById(R.id.latest_preview_description);
					description.setText(moduleName);
					innerLayout.addView(view);
				}

			}
			outerLayout.addView(innerLayout);

		}

		return outerLayout;

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
	private int setRows(LinkedList<ManLatest> latest, int cardsPerLine) {
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

}