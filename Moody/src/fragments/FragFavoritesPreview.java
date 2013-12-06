/**
 * 
 */
package fragments;

import java.util.ArrayList;

import managers.ManContents;
import managers.ManFavorites;
import managers.ManSession;
import model.ModConstants;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import ui.CheckableLinearLayout;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.moody.R;

/**
 * @author SérgioFilipe
 * 
 */
public class FragFavoritesPreview extends Fragment {

	// ManSession Manager Class
	ManSession session;

	LayoutInflater myInflater;

	ActionMode.Callback mCallback;
	ActionMode mMode;
	ArrayList<Long> selectedIds;

	private LinearLayout mainLayout;

	private ScrollView contentScrollable;

	private LinearLayout contentsLayout;

	/**
	 * 
	 */
	public FragFavoritesPreview() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		selectedIds = new ArrayList<Long>();
		session = new ManSession(getActivity().getApplicationContext());

		mCallback = onActionModeCallback();

		return createContent(new ManFavorites(getActivity()
				.getApplicationContext()).getFavorites());
	}

	
	/**
	 * @return Callback
	 */
	private Callback onActionModeCallback() {
		return new ActionMode.Callback() {

			// This is called when the action mode is created. This is called by
			// startActionMode()
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {

				getActivity().getMenuInflater().inflate(R.menu.favorites, menu);
				return true;
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				FragmentTransaction fr = getFragmentManager()
						.beginTransaction();
				FragFavoritesPreview fragment = new FragFavoritesPreview();
				fr.disallowAddToBackStack();
				fr.replace(R.id.mainFragment, fragment);
				fr.commit();
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				// TODO Auto-generated method stub
				return false;
			}

			/** This is called when an item in the context menu is selected */
			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId()) {
				case R.id.action_delete:

					new ManFavorites(getActivity().getApplicationContext())
							.removeFavorite(selectedIds);

					// Automatically exists the action mode, when the user
					// selects this action

					mode.finish();
					break;

				}
				return false;
			}
		};
	}

	/**
	 * 
	 * If favorites is empty inflate empty layout else create the rows with the
	 * favorites
	 * 
	 * @param CourseName
	 * @param courseId
	 * @return View
	 */
	protected LinearLayout createContent(ArrayList<Long> favorites) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		initLayouts();
		if (favorites.isEmpty()) {
			contentsLayout.addView(inflater.inflate(
					R.layout.frag_empty_favorites, null));
			return contentsLayout;
		} else
			mainLayout.addView(createTopicsHeader(inflater));
		contentScrollable.addView(createContentRows(contentsLayout, favorites,
				inflater));
		mainLayout.addView(contentScrollable);
		return mainLayout;
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
	 * Method to create the header of the topics preview with the course path
	 * and the "add favorites" button
	 * 
	 * @param CourseName
	 * @param courseId
	 * @param inflater
	 * @return View
	 */
	protected View createTopicsHeader(LayoutInflater inflater) {
		View topicsHeaderView = inflater.inflate(R.layout.frag_header, null);

		TextView header = (TextView) topicsHeaderView
				.findViewById(R.id.course_path_textView);
		ImageButton addFavorite = (ImageButton) topicsHeaderView
				.findViewById(R.id.add_favorites_button);
		addFavorite.setVisibility(View.GONE);

		header.setText(Html.fromHtml(session.getValues(
				ModConstants.KEY_FULL_NAME, null)
				+ " > <font color=#BE245A>"
				+ getResources().getString(R.string.favorites_header)
				+ "</font>"));

		return topicsHeaderView;
	}

	/**
	 * Method to create the topics preview with the courses content and add this
	 * content to the "row"
	 * 
	 * @param rows
	 * @param inflater
	 * @param contentsLayout
	 * @param courseId
	 */
	protected LinearLayout createContentRows(LinearLayout contentsLayout,
			ArrayList<Long> favorites, LayoutInflater inflater) {
		MoodleCourse[] userCourses = new ManContents(getActivity()
				.getApplicationContext()).getCourses();

		for (int i = 0; i < favorites.size(); i++) {

			final MoodleCourse courseInfo = getCourse(favorites.get(i),
					userCourses);

			String courseId = Long.toString(courseInfo.getId());

			MoodleCourseContent[] contents = new ManContents(getActivity()
					.getApplicationContext()).getContent(courseId);

			MoodleModule[] modules = contents[0].getMoodleModules();

			LinearLayout row = new LinearLayout(getActivity());

			row.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			View view = inflater.inflate(
					R.layout.frag_favorites_preview_context, null);

			((TextView) view.findViewById(R.id.favorites_title))
					.setText(courseInfo.getFullname());

			TextView description = (TextView) view
					.findViewById(R.id.favorites_description);

			description.setText((modules != null) ? getModuleInfo(modules)
					: "No info");

			CheckableLinearLayout favoritesCard = (CheckableLinearLayout) view
					.findViewById(R.id.favorites_layout);
			favoritesCard
					.setId(Integer.parseInt(courseInfo.getId().toString()));

			setOnFavorite(courseInfo, favoritesCard);

			row.addView(view);
			contentsLayout.addView(row);

		}

		return contentsLayout;
	}

	/**
	 * @param courseInfo
	 * @param favoritesLayout
	 */
	private void setOnFavorite(final MoodleCourse courseInfo,
			final CheckableLinearLayout favoritesLayout) {
		favoritesLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if (mMode == null) {
					// TODO Auto-generated method stub
					String courseId = Integer.toString(v.getId());
					String courseName = courseInfo.getFullname();

					Bundle bundle = new Bundle();
					bundle.putString("courseId", courseId);
					bundle.putString("courseName", courseName);

					FragmentManager fragmentManager = getFragmentManager();
					FragmentTransaction fragmentTransaction = fragmentManager
							.beginTransaction();

					FragTopicsPreview insideTopicsFrag = new FragTopicsPreview();
					insideTopicsFrag.setArguments(bundle);
//					fragmentTransaction.add(insideTopicsFrag, "topic_frag");
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.replace(R.id.mainFragment,
							insideTopicsFrag);
					fragmentTransaction.commit();
				} else {
					favoritesLayout.performLongClick();

				}

			}
		});

		favoritesLayout.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				if (mMode == null)
					mMode = getActivity().startActionMode(mCallback);

				Integer id = favoritesLayout.getId();

				if (favoritesLayout.isChecked())
					selectedIds.add(id.longValue());
				else
					selectedIds.remove(id.longValue());

				return true;

			}
		});

	}

	private MoodleCourse getCourse(Long id, MoodleCourse[] courses) {

		for (MoodleCourse course : courses) {
			if (Long.valueOf(course.getId()) == Long.valueOf(id))
				return course;
		}

		return null;
	}

	private String getModuleInfo(MoodleModule[] modules) {
		String tVContent = "";

		for (MoodleModule module : modules) {
			if (module.getName() != null) {

				String getNameDirty = module.getName();
				String getNamePure = "";

				for (int n = 0; n < getNameDirty.split("\\s+").length; n++) {
					if (n == 5)
						break;

					getNamePure += getNameDirty.split("\\s+")[n] + " ";
				}

				tVContent += "-" + getNamePure + "\n";

			}

		}

		return tVContent;
	}

}
