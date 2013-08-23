package fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.moody.R;

public class MainContentFragment extends Fragment {

	public MainContentFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		LinearLayout insertPoint = new LinearLayout(getActivity());
		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);

		TextView coursePath = new TextView(getActivity());
		coursePath.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		coursePath.setBackgroundColor(getResources().getColor(R.color.C_White));
		coursePath.setPadding(5, 10, 0, 10);
		coursePath.setText(R.string.course_path);
		coursePath.setTextColor(getResources().getColor(R.color.C_Blue_Light));
		insertPoint.addView(coursePath);

		for (int j = 0; j < 5; j++) {
			LinearLayout row = new LinearLayout(getActivity());
			row.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			RelativeLayout topics = new RelativeLayout(getActivity());
			ImageButton arrow = new ImageButton(getActivity());
			ImageButton addFavorites = new ImageButton(getActivity());
			TextView topicName = new TextView(getActivity());
			TextView contentPreview = new TextView(getActivity());
			View viewBottom = new View(getActivity());
			View viewTop = new View(getActivity());

			// RelativeLayout params
			topics.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			topics.setBackgroundResource(R.drawable.fill_light_grey);

			// Arrow params
			arrow.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			arrow.setBackgroundResource(R.color.transparent);
			arrow.setContentDescription(getString(R.string.arrow_description));
			arrow.setPadding(0, 0, 5, 0);
			arrow.setImageResource(R.drawable.right_arrow);
			arrow.setId(1);

			RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(
					arrow.getLayoutParams());
			arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);
			arrow.setLayoutParams(arrowParams);

			// Add favorits params
			addFavorites.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			addFavorites.setBackgroundResource(R.color.transparent);
			addFavorites
					.setContentDescription(getString(R.string.add_favorite_description));
			addFavorites.setPadding(0, 0, 10, 0);
			addFavorites.setImageResource(R.drawable.add_favorites);
			addFavorites.setId(2);

			RelativeLayout.LayoutParams addFavoritesParams = new RelativeLayout.LayoutParams(
					addFavorites.getLayoutParams());
			addFavoritesParams.addRule(RelativeLayout.CENTER_VERTICAL);
			addFavoritesParams.addRule(RelativeLayout.LEFT_OF, arrow.getId());
			addFavorites.setLayoutParams(addFavoritesParams);

			// Add textview topics
			topicName.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			topicName.setBackgroundResource(R.drawable.border_course_context);

			topicName.setPadding(20, 0, 20, 0);
			topicName.setText(getString(R.string.topic_string));
			topicName.setId(3);

			RelativeLayout.LayoutParams topicsNameParams = new RelativeLayout.LayoutParams(
					topicName.getLayoutParams());
			topicsNameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			topicsNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			topicsNameParams.setMargins(0, 10, 0, 0);
			topicName.setLayoutParams(topicsNameParams);

			// Add textview content preview
			contentPreview.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			contentPreview.setText(getString(R.string.content_preview));

			RelativeLayout.LayoutParams contentPreviewParams = new RelativeLayout.LayoutParams(
					contentPreview.getLayoutParams());
			contentPreviewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			contentPreviewParams.addRule(RelativeLayout.LEFT_OF,
					addFavorites.getId());
			contentPreviewParams.addRule(RelativeLayout.BELOW,
					topicName.getId());
			contentPreviewParams.setMargins(0, 10, 20, 10);
			contentPreview.setLayoutParams(contentPreviewParams);
			contentPreview.setId(4);

			// Add view to fill space bottom of topics
			viewBottom.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT, 10));
			viewBottom.setBackgroundResource(R.drawable.border_inside);
			RelativeLayout.LayoutParams viewBottomParams = new RelativeLayout.LayoutParams(
					viewBottom.getLayoutParams());
			viewBottomParams.addRule(RelativeLayout.ALIGN_BOTTOM,
					contentPreview.getId());
			viewBottomParams.setMargins(0, 0, 0, -10);
			viewBottom.setLayoutParams(viewBottomParams);

			// Add view to fill space in the top of topics
			viewTop.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT, 10));
			viewTop.setBackgroundColor(getResources().getColor(R.color.C_White));
			RelativeLayout.LayoutParams viewTopParams = new RelativeLayout.LayoutParams(
					viewTop.getLayoutParams());
			viewTopParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			viewTopParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			viewTop.setLayoutParams(viewTopParams);

			topics.addView(arrow);
			topics.addView(addFavorites);
			topics.addView(topicName);
			topics.addView(contentPreview);
			topics.addView(viewBottom);
			topics.addView(viewTop);

			row.addView(topics);
			insertPoint.addView(row);
		}

		return insertPoint;
	}

	public void setText(String item, View view) {
		TextView textPath = (TextView) view
				.findViewById(R.id.course_path_textView);
		textPath.setText(item);
	}

	public void numberOfRows(View view) {

		// LinearLayout insertPoint = (LinearLayout)
		// view.findViewById(R.id.linear_layout_inside_left);
		//
		// for (int j = 5; j >= 0; j--) {
		//
		// LinearLayout row = new LinearLayout(this.getActivity());
		// row.setLayoutParams(new LayoutParams(
		// android.view.ViewGroup.LayoutParams.MATCH_PARENT,
		// android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		//
		//
		//
		//
		//
		//
		//
		//
		//
		//
		//
		//
		// Button btnTag = new Button(this.getActivity());
		// btnTag.setLayoutParams(new LayoutParams(
		// android.view.ViewGroup.LayoutParams.MATCH_PARENT,
		// android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		// btnTag.setText("Your Course " + (j + 1));
		// btnTag.setId(j);
		// btnTag.setBackgroundColor(attr.selectableItemBackground);
		//
		// if (j < 5) {
		// btnTag.setBackgroundResource(R.drawable.border_inside);
		// }
		//
		// btnTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.books, 0,
		// 0, 0);
		// btnTag.setCompoundDrawablePadding(10);
		// btnTag.setPadding(10, 0, 0, 0);
		// btnTag.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
		// btnTag.setClickable(false);
		//
		// row.addView(btnTag);
		//
		// layout.addView(row, 3);
		// }

		LinearLayout insertPoint = (LinearLayout) view
				.findViewById(R.id.course_topics_linear_layout);

		LinearLayout row = new LinearLayout(getActivity());
		row.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		for (int j = 0; j < 5; j++) {

			RelativeLayout topics = new RelativeLayout(getActivity());
			ImageButton arrow = new ImageButton(getActivity());
			ImageButton addFavorites = new ImageButton(getActivity());
			TextView topicName = new TextView(getActivity());
			TextView contentPreview = new TextView(getActivity());

			// RelativeLayout params
			topics.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.FILL_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			topics.setBackgroundResource(R.drawable.fill_light_grey);

			// Arrow params
			arrow.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			arrow.setBackgroundResource(R.color.transparent);
			arrow.setContentDescription(getString(R.string.arrow_description));
			arrow.setPadding(0, 0, 5, 0);
			arrow.setImageResource(R.drawable.right_arrow);
			arrow.setId(j + 1);

			RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(
					arrow.getLayoutParams());
			arrowParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			arrowParams.addRule(RelativeLayout.CENTER_VERTICAL);

			// Add favorits params
			addFavorites.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			addFavorites.setBackgroundResource(R.color.transparent);
			addFavorites
					.setContentDescription(getString(R.string.add_favorite_description));
			addFavorites.setPadding(0, 0, 10, 0);
			addFavorites.setImageResource(R.drawable.add_favorites);
			addFavorites.setId(j + 2);

			RelativeLayout.LayoutParams addFavoritesParams = new RelativeLayout.LayoutParams(
					addFavorites.getLayoutParams());
			addFavoritesParams.addRule(RelativeLayout.CENTER_VERTICAL);
			addFavoritesParams.addRule(RelativeLayout.LEFT_OF, arrow.getId());

			// Add textview topics
			topicName.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			topicName.setBackgroundResource(R.drawable.border_course_context);
			topicName.setPadding(20, 0, 20, 0);
			topicName.setText(getString(R.string.topic_string));
			topicName.setId(j + 3);

			RelativeLayout.LayoutParams topicsNameParams = new RelativeLayout.LayoutParams(
					topicName.getLayoutParams());
			topicsNameParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			topicsNameParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

			// Add textview content preview
			contentPreview.setLayoutParams(new LayoutParams(250, 100));
			contentPreview
					.setBackgroundResource(R.drawable.border_course_context);
			contentPreview.setPadding(0, 10, 10, 0);
			contentPreview.setText(getString(R.string.content_preview));

			RelativeLayout.LayoutParams contentPreviewParams = new RelativeLayout.LayoutParams(
					contentPreview.getLayoutParams());
			contentPreviewParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			contentPreviewParams.addRule(RelativeLayout.LEFT_OF,
					addFavorites.getId());
			contentPreviewParams.addRule(RelativeLayout.BELOW,
					topicName.getId());

			topics.addView(arrow);
			topics.addView(addFavorites);
			topics.addView(topicName);
			topics.addView(contentPreview);

			row.addView(topics);
			insertPoint.addView(row, 0);

		}
	}

}
