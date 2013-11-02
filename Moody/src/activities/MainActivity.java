package activities;

import fragments.FragFavoritesPreview;
import fragments.FragLatest;
import fragments.FragTopics;
import fragments.FragTopicsPreview;
import fragments.FragUserCloud;
import fragments.FragUserContactMessage;
import fragments.FragUserContacts;
import fragments.FragUserPicture;
import interfaces.InterDialogFrag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import managers.ManAlertDialog;
import managers.ManContents;
import managers.ManDataStore;
import managers.ManFavorites;
import managers.ManSearch;
import managers.ManSession;
import managers.ManUserContacts;
import model.ModConstants;
import model.ModMessage;
import model.ObjectSearch;
import restPackage.MoodleContact;
import restPackage.MoodleCourse;
import restPackage.MoodleUser;
import service.ServiceBackground;
import ui.CardTextView;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import bitmap.BitmapResizer;

import com.android.moody.R;
import com.espian.showcaseview.ShowcaseView;
import com.espian.showcaseview.ShowcaseViews;
import com.espian.showcaseview.ShowcaseViews.ItemViewProperties;

import connections.DataAsyncTask;

/**
 * @author firetrap
 * 
 */
public class MainActivity extends Activity implements OnClickListener,
		InterDialogFrag {

	private DrawerLayout myDrawerLayout;

	private HashMap<String, String> organizedCourses = new HashMap<String, String>();

	// ManSession Manager Class
	ManSession session;

	public long startTime;
	public long endTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		startTime = System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		// startService(new Intent(getApplicationContext(),
		// ServiceBackground.class));
		setContentView(R.layout.activity_main);
		// shared pref
		session = new ManSession(getApplicationContext());
		myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		populateLeft();
		populateRight();
		receiveNotification();
		initDemoOverlay();
		drawerLayoutListener();

	}

	/**
	 * 
	 */
	private void initDemoOverlay() {
		ShowcaseView.ConfigOptions configOptions1 = new ShowcaseView.ConfigOptions();
		configOptions1.shotType = ShowcaseView.TYPE_ONE_SHOT;
		configOptions1.hideOnClickOutside = false;
		configOptions1.block = true;
		configOptions1.showcaseId = R.id.DEMO_OPEN_LEFT;
		ShowcaseViews views1 = new ShowcaseViews(MainActivity.this,
				R.layout.activity_main);

		views1.addView(new ItemViewProperties(R.id.scrollView_main_content,
				R.string.demo_open_left_title, R.string.demo_open_left_message,
				0f, new float[] { 0, 600, 300, 600 }, configOptions1));

		views1.show();
	}

	/**
	 * 
	 */
	private void drawerLayoutListener() {
		myDrawerLayout.setDrawerListener(new DrawerListener() {
			@Override
			public void onDrawerStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDrawerSlide(View arg0, float arg1) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onDrawerOpened(View arg0) {

				switch (arg0.getId()) {

				case R.id.left_drawer:
					ShowcaseView.ConfigOptions configOptions2 = new ShowcaseView.ConfigOptions();
					configOptions2.showcaseId = R.id.DEMO_USER_SECTION;
					configOptions2.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions2.hideOnClickOutside = false;
					configOptions2.block = true;

					ShowcaseView.ConfigOptions configOptions3 = new ShowcaseView.ConfigOptions();
					configOptions3.showcaseId = R.id.DEMO_COURSES_SECTION;
					configOptions3.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions3.hideOnClickOutside = false;
					configOptions3.block = true;

					ShowcaseView.ConfigOptions configOptions4 = new ShowcaseView.ConfigOptions();
					configOptions4.showcaseId = R.id.DEMO_NAVIGATION_SECTION;
					configOptions4.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions4.hideOnClickOutside = false;
					configOptions4.block = true;

					ShowcaseViews views2 = new ShowcaseViews(MainActivity.this,
							R.layout.activity_main);

					views2.addView(new ItemViewProperties(
							R.id.login_image_button, R.string.demo_user_title,
							R.string.demo_user_message, 0f, new float[] { 350,
									250, 350, 250 }, configOptions2));

					views2.addView(new ItemViewProperties(
							R.id.begin_of_courses, R.string.demo_courses_title,
							R.string.demo_courses_message, 0f, new float[] {
									350, 600, 350, 600 }, configOptions3));

					views2.addView(new ItemViewProperties(
							R.id.favorites_button,
							R.string.demo_navigation_title,
							R.string.demo_navigation_message, 0f, new float[] {
									350, 950, 350, 950 }, configOptions4));
					views2.show();
					break;

				case R.id.right_drawer:
					ShowcaseView.ConfigOptions configOptions5 = new ShowcaseView.ConfigOptions();
					configOptions5.showcaseId = R.id.DEMO_SEARCH_SECTION;
					configOptions5.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions5.hideOnClickOutside = false;
					configOptions5.block = true;
					ShowcaseView.ConfigOptions configOptions6 = new ShowcaseView.ConfigOptions();
					configOptions6.showcaseId = R.id.DEMO_CONTACTS_SECTION;
					configOptions6.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions6.hideOnClickOutside = false;
					configOptions6.block = true;

					ShowcaseViews views3 = new ShowcaseViews(MainActivity.this,
							R.layout.activity_main);

					views3.addView(new ItemViewProperties(R.id.searchView,
							R.string.demo_search_title,
							R.string.demo_search_message, 0f, new float[] {
									380, 500, 380, 200 }, configOptions5));

					views3.addView(new ItemViewProperties(
							R.id.contacts_linear_layout,
							R.string.demo_contacts_title,
							R.string.demo_contacts_message, 0f, new float[] {
									350, 500, 350, 500 }, configOptions6));
					views3.show();
					break;

				}

			}

			@Override
			public void onDrawerClosed(View arg0) {
				switch (arg0.getId()) {
				case R.id.left_drawer:
					ShowcaseView.ConfigOptions configOptions7 = new ShowcaseView.ConfigOptions();
					configOptions7.showcaseId = R.id.DEMO_OPEN_RIGHT;
					configOptions7.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions7.hideOnClickOutside = false;
					configOptions7.block = true;
					ShowcaseViews views4 = new ShowcaseViews(MainActivity.this,
							R.layout.activity_main);

					views4.addView(new ItemViewProperties(
							R.id.scrollView_main_content,
							R.string.demo_open_right_title,
							R.string.demo_open_right_message, 0f, new float[] {
									600, 500, 0, 500 }, configOptions7));
					views4.show();

					break;

				case R.id.right_drawer:
					ShowcaseView.ConfigOptions configOptions8 = new ShowcaseView.ConfigOptions();
					configOptions8.showcaseId = R.id.DEMO_FAVORITES;
					configOptions8.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions8.hideOnClickOutside = false;
					configOptions8.block = true;

					ShowcaseView.ConfigOptions configOptions9 = new ShowcaseView.ConfigOptions();
					configOptions9.showcaseId = R.id.DEMO_END;
					configOptions9.shotType = ShowcaseView.TYPE_ONE_SHOT;
					configOptions9.hideOnClickOutside = false;
					configOptions9.block = true;

					ShowcaseViews views5 = new ShowcaseViews(MainActivity.this,
							R.layout.activity_main);

					views5.addView(new ItemViewProperties(
							R.id.scrollView_main_content,
							R.string.demo_add_favorite_title,
							R.string.demo_add_favorite_message, 0f,
							new float[] { 610, 190, 610, 190 }, configOptions8));

					views5.addView(new ItemViewProperties(
							R.id.scrollView_main_content,
							R.string.demo_end_title, R.string.demo_end_message,
							0f, configOptions9));

					views5.show();

					break;

				}

			}
		});
	}

	@Override
	protected void onResume() {
		endTime = System.currentTimeMillis();
		Log.d("MoodyPerformance",
				Long.toString(performanceMeasure(startTime, endTime)));
		super.onResume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {

		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			search(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// // Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;

	}

	/*
	 * (non-Javadoc) * This method it's responsible to retrieve the bitmap
	 * data/resize/compress The first case of the switch it's not fully
	 * implemented as planned, moodle doesn't support the users to update their
	 * own user details this issue is described and reported here
	 * https://tracker.moodle.org/browse/CONTRIB-4282 The next code it's ready
	 * 
	 * TO DO: implement the user update details in Moody when Moodle.org decide
	 * to add the required web service function.
	 * 
	 * @see interfaces.InterDialogFrag#onFinishEditDialog(java.lang.String, int)
	 */
	@Override
	public void onFinishEditDialog(String inputText, int code) {
		switch (code) {
		case ModConstants.DIALOG_FRAG_USER_PIC:
			session.addPref(inputText);
			ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);

			login_button.setImageBitmap(BitmapResizer
					.decodeSampledBitmapFromResource(inputText,
							R.id.login_image_button, 100, 100));
			break;

		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() == 1) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	private void populateRight() {
		setupSearchView();
		populateContacts();
	}

	private void setupSearchView() {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) findViewById(R.id.searchView);
		SearchableInfo searchableInfo = searchManager
				.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
	}

	private void populateContacts() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout contactsLinearLayout = (LinearLayout) findViewById(R.id.contacts_linear_layout);
		LinearLayout strangersLinearLayout = (LinearLayout) findViewById(R.id.strangers_linear_layout);
		final MoodleContact[] contacts = new ManUserContacts(
				getApplicationContext()).getContacts();
		if (contacts == null) {
			contactsLinearLayout.setVisibility(View.GONE);
			strangersLinearLayout.setVisibility(View.GONE);
		} else {
			for (int i = 0; i < contacts.length; i++) {
				View view = inflater.inflate(R.layout.contact, null);
				final MoodleUser singleContact = contacts[i]
						.getContactProfile();
				TextView contactTxtview = (TextView) view
						.findViewById(R.id.contact_textView);
				contactTxtview.setText(singleContact.getFullname());

				contactSetOnClickListener(singleContact, contactTxtview);

				contactSetOnLongClickListener(singleContact, contactTxtview);

				switch (contacts[i].getState()) {
				case ONLINE:
					contactTxtview.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.contact_online),
							null,
							getResources().getDrawable(
									R.drawable.ic_action_email), null);

					// add the textView to the linearLayout
					contactsLinearLayout.addView(contactTxtview);
					break;

				case OFFLINE:
					contactTxtview.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.contact_offline),
							null,
							getResources().getDrawable(
									R.drawable.ic_action_email), null);
					// add the textView to the linearLayout
					contactsLinearLayout.addView(contactTxtview);
					break;

				case STRANGERS:
					contactTxtview.setCompoundDrawablesWithIntrinsicBounds(
							getResources().getDrawable(
									R.drawable.contact_stranger),
							null,
							getResources().getDrawable(
									R.drawable.ic_action_email), null);
					// add the textView to the linearLayout
					strangersLinearLayout.addView(contactTxtview);
					break;

				}

			}
		}

		ArrayList<MoodleContact> blockedContacts = new ManUserContacts(
				getApplicationContext()).getBlockedContacts();
		LinearLayout blockedLinearLayout = (LinearLayout) findViewById(R.id.blocked_linear_layout);
		if (blockedContacts == null) {
			blockedLinearLayout.setVisibility(View.GONE);

		} else {
			for (int j = 0; j < blockedContacts.size(); j++) {
				View view = inflater.inflate(R.layout.contact, null);
				final MoodleUser singleContact = blockedContacts.get(j)
						.getContactProfile();
				TextView contactTxtview = (TextView) view
						.findViewById(R.id.contact_textView);
				contactTxtview.setText(singleContact.getFullname());
				contactTxtview.setPaintFlags(contactTxtview.getPaintFlags()
						| Paint.STRIKE_THRU_TEXT_FLAG);
				contactTxtview.setCompoundDrawablesWithIntrinsicBounds(
						getResources().getDrawable(R.drawable.contact_blocked),
						null,
						getResources().getDrawable(R.drawable.ic_action_email),
						null);
				contactSetOnClickListener(singleContact, contactTxtview);
				contactSetOnLongClickListener(singleContact, contactTxtview);
				blockedLinearLayout.addView(contactTxtview);

			}
		}

		if (contacts == null && blockedContacts == null)
			findViewById(R.id.contacts_wrapper).setVisibility(View.GONE);

	}

	/**
	 * @param singleContact
	 * @param contactTxtview
	 */
	private void contactSetOnLongClickListener(final MoodleUser singleContact,
			TextView contactTxtview) {
		contactTxtview.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Long id = singleContact.getId();
				if (Long.valueOf(id) != Long.valueOf(-1)) {
					FragmentManager fm = getFragmentManager();
					FragUserContacts userContactContextDialog = null;
					Bundle bund = new Bundle();
					bund.putLong("contact", id);
					bund.putString("name", singleContact.getFullname());
					userContactContextDialog = new FragUserContacts();
					userContactContextDialog.setArguments(bund);
					userContactContextDialog.setRetainInstance(true);
					userContactContextDialog.show(fm, "fragment_name");
				}
				return false;
			}
		});
	}

	/**
	 * @param singleContact
	 * @param contactTxtview
	 */
	private void contactSetOnClickListener(final MoodleUser singleContact,
			TextView contactTxtview) {
		contactTxtview.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentManager fm = getFragmentManager();
				FragUserContactMessage userContactContextDialog = null;
				Bundle bund = new Bundle();
				bund.putLong("contact", singleContact.getId());
				bund.putString("name", singleContact.getFullname());
				userContactContextDialog = new FragUserContactMessage();
				userContactContextDialog.setArguments(bund);
				userContactContextDialog.setRetainInstance(true);
				userContactContextDialog.show(fm, "fragment_name");
			}
		});
	}

	private void populateLeft() {
		populateFullName();
		populateUserCourses();
		populateUserPicture();
	}

	private void receiveNotification() {
		if (getIntent().getFlags() == R.id.MOODY_NOTIFICATION_ACTION_TOPIC) {
			int courseId2 = getIntent().getFlags();
			Button btnTag2 = (Button) findViewById(courseId2);
			btnTag2.performClick();
		} else if (getIntent().getFlags() == R.id.MOODY_NOTIFICATION_ACTION_MODULE) {
			Bundle bundle = getIntent().getExtras();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();

			FragTopics insideTopicsFrag = new FragTopics();
			insideTopicsFrag.setArguments(bundle);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.mainFragment, insideTopicsFrag);
			fragmentTransaction.commit();
		} else {
			// When its created it will get any course to populate the main
			// fragment

			Entry<String, String> course = organizedCourses.entrySet()
					.iterator().next();
			int startUpCourseId = Integer.parseInt(course.getKey());
			Button btnTag = (Button) findViewById(startUpCourseId);
			btnTag.performClick();
		}
	}

	public void populateFullName() {

		TextView view = (TextView) findViewById(R.id.fullname_textview);
		view.setText(session.getValues(ModConstants.KEY_FULL_NAME, null));

	}

	public void populateUserPicture() {
		ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
		if (session.getValues("PIC_PATH", null) == null) {

			Drawable pic = null;
			MoodleUser user = new ManContents(getApplicationContext())
					.getUser();

			user.getProfileImageURL();
			pic = DataAsyncTask
					.createDrawableFromUrl(user.getProfileImageURL());
			login_button.setBackgroundResource(R.drawable.bkgd_imagebutton);
			login_button.setImageDrawable(pic);

		} else {

			login_button.setImageBitmap(BitmapResizer
					.decodeSampledBitmapFromResource(
							session.getValues("PIC_PATH", null),
							R.id.login_image_button, 100, 100));

		}
	}

	private void populateUserCourses() {

		// Get all the courses from current user
		MoodleCourse[] courses = new ManContents(getApplicationContext())
				.getCourses();

		// Start populating the menus and views
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout inserPoint = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		if (courses == null || courses.length == 0) {

			fatalError("Moody Fatal Error - Get Courses",
					"An Error Ocurred Retrieving Data contact your Moodle Administrator");

		} else {
			for (int j = 0; j < courses.length; j++) {

				LinearLayout row = new LinearLayout(this);
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View view = inflater.inflate(
						R.layout.courses_button_left_drawer, null);

				Button btnTag = (Button) view.findViewById(R.id.course_id);
				btnTag.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

				String name = courses[j].getFullname();
				int id = courses[j].getId().intValue();
				btnTag.setText(name);
				btnTag.setId(id);

				organizedCourses.put(Integer.toString(id), name);

				row.addView(view);

				inserPoint.addView(row, 3);

			}

		}

	}

	public void onAddFavoritesClick(View v) {
		// The add to favorites button id its the same of the course so we can
		// send the button id to future development
		final int id = v.getId();
		final View view = v;

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {

				case DialogInterface.BUTTON_POSITIVE:
					new ManFavorites(getApplicationContext())
							.insertFavorite(id);

					dialog.dismiss();

					onCoursesClick(view);
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();

					break;
				}
			}
		};

		ManAlertDialog.showMessageDialog(
				this,
				new ModMessage(getResources().getString(
						R.string.favorites_add_message), String.format(
						getResources().getString(
								R.string.favorites_add_confirm_message),
						organizedCourses.get(Integer.toString(v.getId())))),
				dialogClickListener, dialogClickListener, false);

	}

	// Method to decide what to do from what button was pressed
	@Override
	public void onClick(View v) {

		FragmentManager fm;
		Intent intent;
		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();

		switch (v.getId()) {
		case R.id.login_image_button:

			fm = getFragmentManager();
			FragUserPicture userDetailsDialog = new FragUserPicture();
			userDetailsDialog.setRetainInstance(true);
			userDetailsDialog.show(fm, "fragment_name");

			break;
		case R.id.logout_image_button:
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						session.logoutUser();

						// limpa cache ao fazer logout.
						new ManDataStore(getApplicationContext()).deleteCache();

						Intent intent = new Intent(getApplicationContext(),
								LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);

						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();

						break;
					}
				}
			};

			ManAlertDialog.showMessageDialog(this, new ModMessage("Logout",
					"Are you sure?"), dialogClickListener, dialogClickListener,
					false);

			break;

		case R.id.fullname_textview:
			intent = new Intent(getApplicationContext(),
					UserDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);

			myDrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.latest_button:

			FragLatest fragmentLatest = new FragLatest();
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.mainFragment, fragmentLatest);
			fragmentTransaction.commit();

			myDrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.favorites_button:

			FragFavoritesPreview fragmentFavorites = new FragFavoritesPreview();
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.mainFragment, fragmentFavorites);
			fragmentTransaction.commit();

			myDrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.cloud_button:

			fm = getFragmentManager();
			FragUserCloud userCloudDialog = new FragUserCloud();
			userCloudDialog.setRetainInstance(true);
			userCloudDialog.show(fm, "fragment_name");

			break;

		case R.id.wiki_button:
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri
					.parse("http://firetrap.github.io/Moody/#!index.md"));
			startActivity(intent);

			break;

		default:
			break;
		}
	}

	public void onCoursesClick(View v) {

		// LOADING
		// Intent intent = new Intent(getApplicationContext(),
		// LoadingActivity.class);
		// startActivity(intent);

		// The view id is the same id of the courses

		String courseName = organizedCourses.get(Integer.toString(v.getId()));
		String courseId = Integer.toString(v.getId());

		Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);

		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		FragTopicsPreview fragment = new FragTopicsPreview();

		fragment.setArguments(bundle);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();
		myDrawerLayout.closeDrawer(Gravity.LEFT);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:

			startActivity(new Intent(getApplicationContext(),
					SettingsActivity.class));

			myDrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.action_refresh:

			startService(new Intent(this, ServiceBackground.class));

			startActivity(new Intent(this, MainActivity.class));
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	/**
	 * @param intent
	 * @param manSearch
	 */
	private void search(Intent intent) {
		ManSearch manSearch = new ManSearch(this);
		String query = intent.getStringExtra(SearchManager.QUERY);
		if (query != null) {

			// do the search
			manSearch.doMySearch(query);
			// return the arraylist with the topic which contains the query
			ArrayList<ObjectSearch> results = manSearch.getResults();

			LinearLayout searchResults = (LinearLayout) this
					.findViewById(R.id.searchResults);
			searchResults.setVisibility(View.VISIBLE);
			searchResults.removeAllViews();

			if (results == null) {
				Toast.makeText(this,
						getString(R.string.no_results) + "\"" + query + "\"",
						Toast.LENGTH_LONG).show();
			} else {
				for (int i = 0; i < results.size(); i++) {
					if (i > 1) {
						searchResults.addView(new CardTextView(this,
								R.id.MOODY_SEARCH_ALL_RESULTS_ACTION_MODULE,
								null, null, query));
						break;
					}
					LinearLayout ll = new LinearLayout(getApplicationContext());
					ll.setOrientation(LinearLayout.VERTICAL);

					ll.addView(new CardTextView(this,
							R.id.MOODY_SEARCH_TITLE_ACTION_MODULE, results.get(
									i).getCourseName(), results.get(i), query));

					ll.addView(new CardTextView(this,
							R.id.MOODY_SEARCH_TOPIC_ACTION_MODULE, results.get(
									i).getTopicName(), results.get(i), query));

					searchResults.addView(ll);
				}

			}
			searchResults.addView(new CardTextView(this,
					R.id.MOODY_SEARCH_WEB_SEARCH_ACTION_MODULE, null, null,
					query));
			searchResults.invalidate();

		}
	}

	/**
	 * 
	 * * Moody Fatal Error Method it will display an alert dialog with the
	 * message and it will clear app data and kill the app
	 * 
	 * @param title
	 * @param msg
	 */
	private void fatalError(String title, String msg) {
		ManAlertDialog.showMessageDialog(this, new ModMessage(title, msg),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						session.logoutUser();
						// limpa cache ao fazer logout.
						new ManDataStore(getApplicationContext()).deleteCache();
						finish();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}

				}, false);
	}

	private long performanceMeasure(long startTime, long endTime) {
		long timestamp = endTime - startTime;
		return timestamp;
	}

}