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
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
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

		return createContent(new ManFavorites().getFavorites(getActivity()
				.getApplicationContext(), getResources()));
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
				fr.addToBackStack(null);
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

					new ManFavorites().removeFavorite(selectedIds,
							getActivity().getApplicationContext(),
							getResources());

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
	 * Create the "row" with the header and the content
	 * 
	 * @param CourseName
	 * @param courseId
	 * @return View
	 */
	protected LinearLayout createContent(ArrayList<Long> favorites) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout insertPoint = new LinearLayout(getActivity());

		insertPoint.setLayoutParams(new LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
		insertPoint.setOrientation(LinearLayout.VERTICAL);
		// insertPoint.addView(inflater.inflate(
		// R.layout.favorites_course_preview_header, null));

		if (favorites.isEmpty()) {
			insertPoint.addView(inflater.inflate(R.layout.empty, null));
			return insertPoint;
		} else
			return createContentRows(insertPoint, favorites, inflater);
	}

	/**
	 * Method to create the topics preview with the courses content and add this
	 * content to the "row"
	 * 
	 * @param rows
	 * @param inflater
	 * @param insertPoint
	 * @param courseId
	 */
	protected LinearLayout createContentRows(LinearLayout insertPoint,
			ArrayList<Long> favorites, LayoutInflater inflater) {
		MoodleCourse[] userCourses = new ManContents().getUserCourses(
				getResources(), getActivity().getApplicationContext());

		for (int i = 0; i < favorites.size(); i++) {

			final MoodleCourse courseInfo = getCourse(favorites.get(i),
					userCourses);

			String courseId = Long.toString(courseInfo.getId());

			MoodleCourseContent[] contents = new ManContents()
					.getCourseContent(courseId, getResources(), getActivity()
							.getApplicationContext());

			MoodleModule[] modules = contents[0].getMoodleModules();

			LinearLayout row = new LinearLayout(getActivity());

			row.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			View view = inflater.inflate(R.layout.favorites_preview_context,
					null);

			if (i > 0) {
				((TextView) view.findViewById(R.id.favorites_header_title))
						.setVisibility(View.GONE);
			} else {
				((TextView) view.findViewById(R.id.favorites_header_title))
						.setText(Html.fromHtml(session.getValues(
								ModConstants.KEY_FULL_NAME, null)
								+ " > <font color=#BE245A>"
								+ getResources().getString(
										R.string.favorites_header) + "</font>"));
			}

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
			insertPoint.addView(row);

		}

		return insertPoint;
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
