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

		return numberOfRows(5);
	}

	public void setText(String item, View view) {
		// TextView textPath = (TextView) view
		// .findViewById(R.id.course_path_textView);
		// textPath.setText(item);
	}

	public View numberOfRows(int rows) {
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
		coursePath.setId(0);
		coursePath.setTag("course_path_tag");
		insertPoint.addView(coursePath);

		for (int j = 0; j < rows; j++) {
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
			topics.setId(j+1);
			topics.setTag("topic_tag_" + j);
			
			
			// Arrow params
			arrow.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			arrow.setBackgroundResource(R.color.transparent);
			arrow.setContentDescription(getString(R.string.arrow_description));
			arrow.setPadding(0, 0, 5, 0);
			arrow.setImageResource(R.drawable.right_arrow);
			arrow.setId(j+1);
			arrow.setTag("arrow_tag");

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
			addFavorites.setId(j+2);
			addFavorites.setTag("add_favorites_tag");

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
			topicName.setId(j+3);
			topicName.setTag("topic_name_tag");

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
			contentPreview.setId(j+4);
			contentPreview.setTag("content_preview_tag");
			contentPreview.setLayoutParams(contentPreviewParams);

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

}
