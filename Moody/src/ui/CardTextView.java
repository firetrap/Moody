package ui;

import model.ObjectSearch;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.moody.R;

import fragments.FragSearch;
import fragments.FragTopics;

public class CardTextView extends TextView {

	public CardTextView(final Activity activity, int id, String text,
			int visibility, boolean clickAble, int color,
			final ObjectSearch params, final String searchQuery) {
		super(activity);

		setText(text);
		setVisibility(visibility);
		// setTextAppearance(context, R.style.CardLightText);
		setClickable(clickAble);
		if (color != 0)
			setTextColor(color);
		setTextSize(18);
		setPadding(10, 10, 10, 10);
		// setBackgroundDrawable(getResources().getDrawable(
		// R.drawable.card_background));
		setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		final DrawerLayout myDrawerLayout = (DrawerLayout) activity
				.findViewById(R.id.drawer_layout);

		switch (id) {
		case 0:
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putString("courseId", params.getCourseId());
					bundle.putString("courseName", params.getCourseName());
					bundle.putString("topicId", params.getTopicId());

					FragmentManager fragmentManager = activity
							.getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager
							.beginTransaction();

					FragTopics insideTopicsFrag = new FragTopics();
					insideTopicsFrag.setArguments(bundle);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.replace(R.id.mainFragment,
							insideTopicsFrag);
					fragmentTransaction.commit();
					if (myDrawerLayout != null) {
						myDrawerLayout.closeDrawer(Gravity.RIGHT);

					}
				}
			});

			break;

		case 1:
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					bundle.putString("query", searchQuery);

					FragmentManager fragmentManager = activity
							.getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager
							.beginTransaction();

					FragSearch search = new FragSearch();
					search.setArguments(bundle);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.replace(R.id.mainFragment, search);
					fragmentTransaction.commit();
					if (myDrawerLayout != null) {
						myDrawerLayout.closeDrawer(Gravity.RIGHT);

					}
				}
			});
			break;

		case 2:
			setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
					intent.putExtra(SearchManager.QUERY, searchQuery);
					activity.startActivity(intent);
				}
			});
			break;

		default:
			break;
		}

	}
}
