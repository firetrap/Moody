package ui;

import model.ObjectSearch;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.moody.R;

import fragments.FragSearch;
import fragments.FragTopics;

public class CardTextView extends TextView {

	private Activity activity;
	private int action;
	private String courseName;
	private String topicName;
	private ObjectSearch params;
	private String searchQuery;

	public CardTextView(Activity activity, int action, String courseName,
			String topicName, ObjectSearch params, String searchQuery) {
		super(activity);
		this.activity = activity;
		this.action = action;
		this.courseName = courseName;
		this.topicName = topicName;
		this.params = params;
		this.searchQuery = searchQuery;

		setCompoundDrawablesWithIntrinsicBounds(null, null, activity
				.getResources().getDrawable(R.drawable.ic_action_go_into), null);

		setTextColor(activity.getResources().getColor(android.R.color.darker_gray));
		setTextSize(18);
		setPadding(10, 10, 10, 10);
		getEllipsize();
		setEllipsize(TruncateAt.END);
		setLines(1);
		setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

		customActions();

	}

	private void customActions() {
		final DrawerLayout myDrawerLayout = (DrawerLayout) activity
				.findViewById(R.id.drawer_layout);
		switch (action) {
		case R.id.MOODY_SEARCH_TOPIC_ACTION_MODULE:
			// TOPICS
			setText(courseName + " > " + topicName);
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

		case R.id.MOODY_SEARCH_ALL_RESULTS_ACTION_MODULE:
			// View all results fragment
			setText(activity.getString(R.string.all_results));
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

		case R.id.MOODY_SEARCH_WEB_SEARCH_ACTION_MODULE:
			// Search on Web
			setText(activity.getString(R.string.search_on_web));
			setTextColor(activity.getResources().getColor(R.color.C_Blue_Light));
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
