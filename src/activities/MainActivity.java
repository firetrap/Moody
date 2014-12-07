package activities;

import fragments.FragChangeLog;
import fragments.FragCoursesOverview;
import fragments.FragFavoritesPreview;
import fragments.FragLatest;
import fragments.FragTopics;
import fragments.FragTopicsPreview;
import fragments.FragUserCloud;
import fragments.FragUserContactMessage;
import fragments.FragUserContacts;
import fragments.FragUserPicture;
import interfaces.InterDialogFrag;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import managers.ManAlertDialog;
import managers.ManDataStore;
import managers.ManFavorites;
import managers.ManSearch;
import managers.ManSession;
import managers.ManUserContacts;
import model.ModCheckConnection;
import model.ModConstants;
import model.ModMessage;
import model.ObjectSearch;

import org.acra.ACRA;

import restPackage.MoodleCallRestWebService;
import restPackage.MoodleContact;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleRestCourse;
import restPackage.MoodleRestEnrol;
import restPackage.MoodleRestMessage;
import restPackage.MoodleRestUser;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import service.ServiceBackground;
import ui.CardTextView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentManager.OnBackStackChangedListener;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.ScrollView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import bitmap.BitmapResizer;

import com.firetrap.moody.R;
import com.google.android.gms.ads.AdView;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fabio Barreiros - Moody Founder
 */

/**
 * @author firetrap
 *
 */

public class MainActivity extends Activity implements OnBackStackChangedListener, OnClickListener, InterDialogFrag {

	private DrawerLayout moodydrawerLayout;

	private HashMap<String, String> organizedCourses = new HashMap<String, String>();

	// ManSession Manager Class
	ManSession session;

	private long startTime;

	private long endTime;

	Context context;

	private static long backPressed;

	ManDataStore data;

	private String userId;

	private MoodleUser loggedUserData;

	int counter = 0;

	private Menu optionsMenu;

	private AdView adView;

	private ScrollView leftScrollView;

	private ScrollView rightScrollView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// The following line triggers the initialization of ACRA
		ACRA.init(this.getApplication());

		// Init time for performance measures
		startTime = System.currentTimeMillis();

		setContentView(R.layout.activity_main);

		context = this;

		// Cache data store
		this.data = new ManDataStore(context);

		// shared pref
		session = new ManSession(getApplicationContext());

		// Logged user id
		userId = session.getValues(ModConstants.KEY_ID, null);

		drawerLayoutDynamicWidth();
		getUserData();
		initLeftContent();
		intRightContent();
		receiveNotification();
		showCase();
		drawerLayoutListener();
		warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
				getString(R.string.no_internet));

	}

	private void drawerLayoutDynamicWidth() {
		moodydrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// The width will be 70% of the available screen size
		int screenWidth = (int) Math.round((getResources().getDisplayMetrics().widthPixels) * 0.65);
		leftScrollView = (ScrollView) findViewById(R.id.left_drawer);
		rightScrollView = (ScrollView) findViewById(R.id.right_drawer);

		DrawerLayout.LayoutParams leftParams = (android.support.v4.widget.DrawerLayout.LayoutParams) leftScrollView.getLayoutParams();
		DrawerLayout.LayoutParams rightParams = (android.support.v4.widget.DrawerLayout.LayoutParams) rightScrollView.getLayoutParams();
		leftParams.width = screenWidth;
		rightParams.width = screenWidth;

		leftScrollView.setLayoutParams(leftParams);
		rightScrollView.setLayoutParams(rightParams);
	}

	/**
	 * Method to get moodle user data
	 */
	private void getUserData() {
		String fileName = MoodleServices.CORE_USER_GET_USERS_BY_ID.name() + userId;

		if (data.isInCache(fileName))
			loggedUserData = (MoodleUser) data.getData(fileName);
		else {
			if (!new ModCheckConnection(getApplicationContext()).hasConnection())
				warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
						getString(R.string.no_internet));
			else
				new MainActivityDataAsyncTask().execute(MoodleServices.CORE_USER_GET_USERS_BY_ID, fileName, "");
		}
	}

	/**
	 * After getting the data it init moodle user
	 *
	 * @param asyncTaskObj
	 */
	private void userDataAsyncResult(Object asyncTaskObj) {
		// After getting the data it init moodle user
		loggedUserData = (MoodleUser) asyncTaskObj;
	}

	/**
	 * Method to initialize the leftMenu
	 */
	private void initLeftContent() {
		getUserFullName();
		getUserCourses();
		getUserPicture();
	}

	/**
	 * Method to initialize the user name textview
	 */
	private void getUserFullName() {
		TextView view = (TextView) findViewById(R.id.fullname_textview);
		view.setText(session.getValues(ModConstants.KEY_FULL_NAME, null));
	}

	/**
	 * Method to initialize the user courses
	 */
	private void getUserCourses() {
		// Get all the courses from current user
		String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES.name() + userId;

		// If the data is in cache send the object to the builder else download
		// and store in cache
		if (data.isInCache(fileName))
			userCoursesAsyncResult(data.getData(fileName));
		else {
			if (!new ModCheckConnection(getApplicationContext()).hasConnection())
				warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
						getString(R.string.no_internet));
			else
				new MainActivityDataAsyncTask().execute(MoodleServices.CORE_ENROL_GET_USERS_COURSES, fileName, "");
		}
	}

	private void userCoursesAsyncResult(Object asyncTaskObj) {
		// Get all the courses from current user
		MoodleCourse[] courses = (MoodleCourse[]) asyncTaskObj;

		// Start populating the menus and views
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout inserPoint = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		if (courses == null || courses.length == 0)
			fatalError("Moody Fatal Error - Get Courses", "An Error Occurred Retrieving Data contact your Moodle Administrator");
		else {
			for (int j = 0; j < courses.length; j++) {

				LinearLayout row = new LinearLayout(this);
				row.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View view = inflater.inflate(R.layout.courses_button_left_drawer, null);

				Button btnTag = (Button) view.findViewById(R.id.course_id);
				btnTag.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
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

	private void getUserPicture() {
		String fileName = MoodleServices.MOODLE_USER_GET_PICTURE.name() + userId;
		ImageButton loginButton = (ImageButton) findViewById(R.id.login_image_button);

		if (session.getValues("PIC_PATH", null) == null) {
			if (!new ModCheckConnection(getApplicationContext()).hasConnection())
				warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
						getString(R.string.no_internet));
			else
				new MainActivityDataAsyncTask().execute(MoodleServices.MOODLE_USER_GET_PICTURE, fileName, "");
		} else
			loginButton.setImageBitmap(BitmapResizer.decodeSampledBitmapFromResource(session.getValues("PIC_PATH", null),
					R.id.login_image_button, 100, 100));
	}

	private void userPicAsyncTaskResult(Object asyncTaskObj) {
		ImageButton loginButton = (ImageButton) findViewById(R.id.login_image_button);
		loginButton.setBackgroundResource(R.drawable.bkgd_imagebutton);
		loginButton.setImageDrawable((Drawable) asyncTaskObj);
	}

	/**
	 * Method responsible to initialize the right menu
	 */
	private void intRightContent() {
		setupSearchView();
		getUserContacts();
	}

	/**
	 * Initialization of searchView
	 *
	 */
	private void setupSearchView() {
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		final SearchView searchView = (SearchView) findViewById(R.id.searchView);
		SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
		searchView.setSearchableInfo(searchableInfo);
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

			LinearLayout searchResults = (LinearLayout) this.findViewById(R.id.searchResults);
			searchResults.setVisibility(View.VISIBLE);
			searchResults.removeAllViews();

			if (results == null) {
				Toast.makeText(this, getString(R.string.no_results) + "\"" + query + "\"", Toast.LENGTH_LONG).show();
			} else {
				for (int i = 0; i < results.size(); i++) {
					if (i > 1) {
						searchResults.addView(new CardTextView(this, R.id.MOODY_SEARCH_ALL_RESULTS_ACTION_MODULE, null, null, query));
						break;
					}
					LinearLayout ll = new LinearLayout(getApplicationContext());
					ll.setOrientation(LinearLayout.VERTICAL);

					ll.addView(new CardTextView(this, R.id.MOODY_SEARCH_TITLE_ACTION_MODULE, results.get(i).getCourseName(),
							results.get(i), query));

					ll.addView(new CardTextView(this, R.id.MOODY_SEARCH_TOPIC_ACTION_MODULE, results.get(i).getTopicName(), results.get(i),
							query));

					searchResults.addView(ll);
				}

			}
			searchResults.addView(new CardTextView(this, R.id.MOODY_SEARCH_WEB_SEARCH_ACTION_MODULE, null, null, query));
			searchResults.invalidate();

		}
	}

	/**
	 *
	 * Method to initialize the contacts
	 *
	 */
	private void getUserContacts() {
		String fileName = MoodleServices.CORE_MESSAGE_GET_CONTACTS.name() + userId;

		if (data.isInCache(fileName))
			userContactsAsyncResult(data.getData(fileName));
		else {
			if (!new ModCheckConnection(getApplicationContext()).hasConnection())
				warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
						getString(R.string.no_internet));
			else
				new MainActivityDataAsyncTask().execute(MoodleServices.CORE_MESSAGE_GET_CONTACTS, fileName, "");
		}
	}

	private void userContactsAsyncResult(Object asyncTaskObj) {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout contactsLinearLayout = (LinearLayout) findViewById(R.id.contacts_linear_layout);
		LinearLayout strangersLinearLayout = (LinearLayout) findViewById(R.id.strangers_linear_layout);

		final MoodleContact[] contacts = (MoodleContact[]) asyncTaskObj;

		if (contacts == null) {
			contactsLinearLayout.setVisibility(View.GONE);
			strangersLinearLayout.setVisibility(View.GONE);
		} else {
			for (int i = 0; i < contacts.length; i++) {
				View view = inflater.inflate(R.layout.contact, null);
				final MoodleUser singleContact = contacts[i].getContactProfile();
				TextView contactTxtview = (TextView) view.findViewById(R.id.contact_textView);
				contactTxtview.setText(singleContact.getFullname());

				contactSetOnClickListener(singleContact, contactTxtview);

				contactSetOnLongClickListener(singleContact, contactTxtview);

				switch (contacts[i].getState()) {
				case ONLINE:
					contactTxtview.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.contact_online), null,
							getResources().getDrawable(R.drawable.ic_action_email), null);

					// add the textView to the linearLayout
					contactsLinearLayout.addView(contactTxtview);
					break;

				case OFFLINE:
					contactTxtview.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.contact_offline), null,
							getResources().getDrawable(R.drawable.ic_action_email), null);
					// add the textView to the linearLayout
					contactsLinearLayout.addView(contactTxtview);
					break;

				case STRANGERS:
					contactTxtview.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.contact_stranger), null,
							getResources().getDrawable(R.drawable.ic_action_email), null);
					// add the textView to the linearLayout
					strangersLinearLayout.addView(contactTxtview);
					break;

				}

			}
		}

		ArrayList<MoodleContact> blockedContacts = new ManUserContacts(getApplicationContext()).getBlockedContacts();
		LinearLayout blockedLinearLayout = (LinearLayout) findViewById(R.id.blocked_linear_layout);
		if (blockedContacts == null) {
			blockedLinearLayout.setVisibility(View.GONE);

		} else {
			for (int j = 0; j < blockedContacts.size(); j++) {
				View view = inflater.inflate(R.layout.contact, null);
				final MoodleUser singleContact = blockedContacts.get(j).getContactProfile();
				TextView contactTxtview = (TextView) view.findViewById(R.id.contact_textView);
				contactTxtview.setText(singleContact.getFullname());
				contactTxtview.setPaintFlags(contactTxtview.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
				contactTxtview.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.contact_blocked), null,
						getResources().getDrawable(R.drawable.ic_action_email), null);
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
	private void contactSetOnLongClickListener(final MoodleUser singleContact, TextView contactTxtview) {
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
	private void contactSetOnClickListener(final MoodleUser singleContact, TextView contactTxtview) {
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

	/**
	 * Method responsible to initialize the main view and differentiate what
	 * type of notification the user has received
	 */
	private void receiveNotification() {
		if (getIntent().getFlags() == R.id.MOODY_NOTIFICATION_ACTION_TOPIC) {
			int courseId2 = getIntent().getFlags();
			Button btnTag2 = (Button) findViewById(courseId2);
			btnTag2.performClick();
		} else if (getIntent().getFlags() == R.id.MOODY_NOTIFICATION_ACTION_MODULE) {
			Bundle bundle = getIntent().getExtras();
			FragmentManager fragmentManager = getFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

			FragTopics insideTopicsFrag = new FragTopics();
			insideTopicsFrag.setArguments(bundle);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.mainFragment, insideTopicsFrag);
			fragmentTransaction.commit();
		} else {
			// If it's a normal startup
			initCoursesOverview();
		}
	}

	/**
	 * Before start FragCoursesOverview it will get all data from courses and
	 * store in cache, after is done it will start the fragment
	 */
	private void initCoursesContents() {
		for (Entry<String, String> entry : organizedCourses.entrySet()) {
			String courseId = entry.getKey();
			String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + courseId + userId;

			if (!data.isInCache(fileName)) {
				if (!new ModCheckConnection(getApplicationContext()).hasConnection())
					warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
							getString(R.string.no_internet));
				else
					new MainActivityDataAsyncTask().execute(MoodleServices.CORE_COURSE_GET_CONTENTS, fileName, courseId);
			} else
				getCourseContentsAsyncResult();
		}

	}

	/**
	 * Checks if all the user courses content are collected and if its true it
	 * will start the fragment
	 */
	private void getCourseContentsAsyncResult() {
		counter++;
		if (counter == organizedCourses.size())
			initCoursesOverview();
	}

	/**
	 * Responsible to give the first overview over user courses when it's
	 * initialized
	 */
	private void initCoursesOverview() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("organizedCourses", organizedCourses);

		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		FragCoursesOverview fragment = new FragCoursesOverview();
		FragmentManager fragmentManager = getFragmentManager();

		fragment.setArguments(bundle);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();
		fragmentManager.executePendingTransactions();
	}

	/**
	 * Method to initialize the demo overlay on first run.
	 */
	private void showCase() {
	}

	public void help() {

		CharSequence options[] = new CharSequence[] { "Tutorial", "Moody web site" };

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Help");
		builder.setItems(options, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					if (getFragmentManager().getBackStackEntryCount() > 1) {
						do {
							getFragmentManager().popBackStackImmediate();
						} while (getFragmentManager().getBackStackEntryCount() > 1);
					}
					showCase();

				} else {
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setData(Uri.parse("http://firetrap.github.io/Moody/#!first-time-with-moody.md"));
					startActivity(intent);
					{

					}
				}
			}
		});
		builder.show();

	}

	/**
	 * The listener of the left/right drawerLayout
	 */
	private void drawerLayoutListener() {
		moodydrawerLayout.setDrawerListener(new DrawerListener() {
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
					break;

				case R.id.right_drawer:
					break;

				}

			}

			@Override
			public void onDrawerClosed(View arg0) {
				switch (arg0.getId()) {
				case R.id.left_drawer:
					break;

				case R.id.right_drawer:
					break;
				}

			}
		});
	}

	@Override
	protected void onResume() {
		endTime = System.currentTimeMillis();
		Log.d("MoodyPerformance", Long.toString(performanceMeasure(startTime, endTime)));
		// changeLogCheckVersion();
		// adView.resume();
		super.onResume();

	}

	@Override
	protected void onDestroy() {
		// adView.destroy();
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// adView.pause();
		super.onPause();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		// Get the intent, verify the action and get the query
		if (Intent.ACTION_SEARCH.equals(intent.getAction()))
			search(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		optionsMenu = menu;
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return super.onCreateOptionsMenu(menu);
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
			ImageButton loginButton = (ImageButton) findViewById(R.id.login_image_button);

			loginButton.setImageBitmap(BitmapResizer.decodeSampledBitmapFromResource(inputText, R.id.login_image_button, 100, 100));
			break;

		default:
			break;
		}

	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() == 1) {
			if (backPressed + 2000 > System.currentTimeMillis()) {
				finish();
			} else
				Toast.makeText(this, getString(R.string.exit_msg), Toast.LENGTH_SHORT).show();
			backPressed = System.currentTimeMillis();
		} else {
			super.onBackPressed();
		}
	}

	public void onAddFavoritesClick(View v) {
		// The add to favorites button id its the same of the course so we can
		// send the button id to future development
		final int id = v.getId();

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					if (getFragmentManager().getBackStackEntryCount() == 1) {
						new ManFavorites(getApplicationContext()).insertFavorite(id);
						dialog.dismiss();

						clearBackStack();
						initCoursesOverview();
					} else {

					}
					break;

				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();

					break;
				}
			}
		};

		ManAlertDialog.showMessageDialog(
				this,
				new ModMessage(getResources().getString(R.string.favorites_add_message),
						String.format(getResources().getString(R.string.favorites_add_confirm_message),
								organizedCourses.get(Integer.toString(v.getId())))), dialogClickListener, dialogClickListener, false);

	}

	@Override
	public void onClick(View v) {

		FragmentManager fm;
		Intent intent;
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

		switch (v.getId()) {
		case R.id.course_path_textView:
			initCoursesOverview();
			// clearBackStack();
			break;

		case R.id.login_image_button:

			fm = getFragmentManager();
			FragUserPicture userDetailsDialog = new FragUserPicture();
			userDetailsDialog.setRetainInstance(true);
			userDetailsDialog.show(fm, "fragment_name");

			break;

		case R.id.fullname_textview:
			intent = new Intent(getApplicationContext(), UserDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);

			moodydrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.latest_button:

			FragLatest fragmentLatest = new FragLatest();
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.mainFragment, fragmentLatest);
			fragmentTransaction.commit();

			moodydrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.favorites_button:

			FragFavoritesPreview fragmentFavorites = new FragFavoritesPreview();
			// clearBackStack();
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.replace(R.id.mainFragment, fragmentFavorites);
			fragmentTransaction.commit();
			moodydrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.cloud_button:

			fm = getFragmentManager();
			FragUserCloud userCloudDialog = new FragUserCloud();
			userCloudDialog.setRetainInstance(true);
			userCloudDialog.show(fm, "fragment_name");

			break;

		case R.id.wiki_button:
			help();
			break;

		default:
			break;
		}
	}

	/**
	 *
	 * Method responsible to clear fragments backstack
	 *
	 */
	private void clearBackStack() {
		FragmentManager fm = this.getFragmentManager();
		for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
			fm.popBackStack();
		}
	}

	/**
	 *
	 * The view id is the same id of the courses
	 *
	 * @param v
	 */
	public void onCoursesClick(View v) {
		String courseName = organizedCourses.get(Integer.toString(v.getId()));
		String courseId = Integer.toString(v.getId());
		String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + courseId + userId;

		if (!data.isInCache(fileName)) {
			if (!new ModCheckConnection(getApplicationContext()).hasConnection())
				warningMessage(new ModCheckConnection(getApplicationContext()).hasConnection(), Toast.LENGTH_LONG, null,
						getString(R.string.no_internet));
			else
				new MainActivityDataAsyncTask().execute(MoodleServices.CORE_COURSE_GET_CONTENTS, fileName, courseId);
		} else
			go2ClickedCourse(courseName, courseId);
	}

	private void go2ClickedCourse(String courseName, String courseId) {
		Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);

		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		FragTopicsPreview fragment = new FragTopicsPreview();

		fragment.setArguments(bundle);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();
		moodydrawerLayout.closeDrawer(Gravity.LEFT);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.menu_settings:
			startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
			moodydrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.action_refresh:
			startService(new Intent(this, ServiceBackground.class));
			startActivity(new Intent(this, MainActivity.class));
			setRefreshActionButtonState(true);
			break;

		case R.id.menu_changelog:
			FragChangeLog dialogStandardFragment = new FragChangeLog();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment prev = fm.findFragmentByTag("changelogdemo_dialog");
			if (prev != null) {
				ft.remove(prev);
			}
			dialogStandardFragment.show(ft, "changelogdemo_dialog");

			moodydrawerLayout.closeDrawer(Gravity.LEFT);
			break;

		case R.id.menu_logout:
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						session.logoutUser();
						// Clear cache on logout
						new ManDataStore(getApplicationContext()).deleteCache();
						Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
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

			ManAlertDialog.showMessageDialog(this, new ModMessage(getString(R.string.logout_confirmation_title),
					getString(R.string.logout_confirmation_content)), dialogClickListener, dialogClickListener, false);
			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	/**
	 * action refresh animation
	 *
	 * @param refreshing
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void setRefreshActionButtonState(final boolean refreshing) {
		if (optionsMenu != null) {
			final MenuItem refreshItem = optionsMenu.findItem(R.id.action_refresh);
			if (refreshItem != null) {
				if (refreshing) {
					refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
					new CountDownTimer(3000, 10) {
						@Override
						public void onTick(long millisUntilFinished) {
						}

						@Override
						public void onFinish() {
							refreshItem.setActionView(null);
						}
					}.start();
				} else {
					refreshItem.setActionView(null);
				}
			}
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
		ManAlertDialog.showMessageDialog(this, new ModMessage(title, msg), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				session.logoutUser();
				// Clear cache on logout
				new ManDataStore(getApplicationContext()).deleteCache();
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());
			}

		}, false);
	}

	/**
	 *
	 * Private method for performance tests proposes only
	 *
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	private long performanceMeasure(long startTime, long endTime) {
		long timestamp = endTime - startTime;
		return timestamp;
	}

	/**
	 * Send a toast with a warning
	 */
	private void warningMessage(boolean statement, int duration, String sucessMessage, String errorMessage) {
		if (statement) {
			if (sucessMessage != null)
				Toast.makeText(this, sucessMessage, duration).show();
		} else if (errorMessage != null)
			Toast.makeText(this, errorMessage, duration).show();
	}

	/**
	 * Checks if it's the first Run or a new version and shows a changeLog
	 */
	private void changeLogCheckVersion() {
		String currentAppVersion = null;
		String storedAppVersion = session.getValues("appVersion", null);
		try {
			currentAppVersion = Integer.toString(getPackageManager().getPackageInfo(getPackageName(), 0).versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}

		// It's the first run or a new version, so show changeLog
		if (storedAppVersion == null || Integer.parseInt(storedAppVersion) < Integer.parseInt(currentAppVersion)) {
			FragChangeLog dialogStandardFragment = new FragChangeLog();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment prev = fm.findFragmentByTag("changelogdemo_dialog");
			if (prev != null)
				ft.remove(prev);
			dialogStandardFragment.show(ft, "changelogdemo_dialog");
			session.appVersion(currentAppVersion); // Store the app version to
													// the sharedPref
		}

	}

	@Override
	public void onBackStackChanged() {
		if (getFragmentManager().getBackStackEntryCount() == 1) {
		}
	}

	private class MainActivityDataAsyncTask extends AsyncTask<Object, Void, Object> {
		private ProgressDialog dialog;
		private CountDownTimer cvt = createCountDownTimer();
		private MoodleServices webService;
		private String fileName2Store;

		public MainActivityDataAsyncTask() {
			dialog = new ProgressDialog(getApplicationContext());
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			cvt.start();
		}

		@Override
		protected Object doInBackground(Object... params) {
			String serverUrl = session.getValues(ModConstants.KEY_URL, null);
			String token = session.getValues(ModConstants.KEY_TOKEN, null);
			webService = (MoodleServices) params[0];
			fileName2Store = (String) params[1];
			String webServiceParams = (String) params[2];

			MoodleCallRestWebService.init(serverUrl + "/webservice/rest/server.php", token);
			long courseId;
			try {
				switch (webService) {
				case CORE_ENROL_GET_USERS_COURSES:
					MoodleCourse[] courses = MoodleRestEnrol.getUsersCourses(Long.parseLong(userId));
					return courses;

				case CORE_USER_GET_USERS_BY_ID:
					MoodleUser user = MoodleRestUser.getUserById(Long.parseLong(userId));
					return user;

				case CORE_COURSE_GET_CONTENTS:
					courseId = Long.parseLong(webServiceParams);
					MoodleCourseContent[] courseContent = MoodleRestCourse.getCourseContent(courseId, null);
					return courseContent;

				case CORE_MESSAGE_GET_CONTACTS:
					return MoodleRestMessage.getContacts();

				case MOODLE_USER_GET_PICTURE:
					String userPicUrl = loggedUserData.getProfileImageURL();
					InputStream inputStream = new URL(userPicUrl).openStream();
					Drawable drawable = Drawable.createFromStream(inputStream, null);
					inputStream.close();
					return drawable;

				default:
					return null;

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Object asyncTaskObj) {
			// Store all objects in cache for future faster access
			if (asyncTaskObj != null)
				data.storeData(asyncTaskObj, fileName2Store);

			cvt.cancel();

			if (dialog != null && dialog.isShowing())
				dialog.dismiss();

			switch (webService) {
			case CORE_ENROL_GET_USERS_COURSES:
				userCoursesAsyncResult(asyncTaskObj);
				break;

			case CORE_USER_GET_USERS_BY_ID:
				userDataAsyncResult(asyncTaskObj);
				break;

			case CORE_COURSE_GET_CONTENTS:
				getCourseContentsAsyncResult();
				break;

			case CORE_MESSAGE_GET_CONTACTS:
				userContactsAsyncResult(asyncTaskObj);
				break;

			case MOODLE_USER_GET_PICTURE:
				userPicAsyncTaskResult(asyncTaskObj);
				break;

			default:
				break;
			}
		}

		private CountDownTimer createCountDownTimer() {
			return new CountDownTimer(250, 10) {
				@Override
				public void onTick(long millisUntilFinished) {
				}

				@Override
				public void onFinish() {
					dialog = new ProgressDialog(context);
					dialog.setMessage("Loading...");
					dialog.setCancelable(false);
					dialog.setCanceledOnTouchOutside(false);
					dialog.show();
				}
			};
		}
	}

}
