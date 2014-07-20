package activities;

import fragments.FragChangeLog;
import fragments.FragCoursesList;
import fragments.FragFavoritesPreview;
import fragments.FragLatest;
import fragments.FragTopics;
import fragments.FragTopicsPreview;
import fragments.FragUserCloud;
import fragments.FragUserContactMessage;
import fragments.FragUserContacts;
import fragments.FragUserPicture;
import interfaces.MainActivityAsyncInterface;
import interfaces.UserDetailsInterface;
import interfaces.UserPictureDialogInterface;
import it.gmariotti.changelibs.library.view.ChangeLogListView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import managers.ManAlertDialog;
import managers.ManDataStore;
import managers.ManFavorites;
import managers.ManSearch;
import managers.ManSession;
import managers.ManUserContacts;
import model.ModConstants;
import model.ModMessage;
import model.ObjectSearch;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

import restPackage.MoodleCallRestWebService;
import restPackage.MoodleContact;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleMessage;
import restPackage.MoodleRestCourse;
import restPackage.MoodleRestEnrol;
import restPackage.MoodleRestMessage;
import restPackage.MoodleRestUser;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import service.ServiceBackground;
import ui.CardTextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import bitmap.BitmapResizer;
import com.firetrap.moody.R;
import connections.DataAsyncTaskNew;

/**
 * @author firetrap
 *
 */

@ReportsCrashes(formKey = "", formUri = "https://moody.iriscouch.com/acra-moody/_design/acra-storage/_update/report", reportType = org.acra.sender.HttpSender.Type.JSON, httpMethod = org.acra.sender.HttpSender.Method.PUT, formUriBasicAuthLogin = "moody", formUriBasicAuthPassword = "moody")
public class MainActivity extends Activity implements MainActivityAsyncInterface, OnClickListener, UserPictureDialogInterface {
	private DrawerLayout			moodydrawerLayout;
	private HashMap<String, String>	organizedCourses	= new HashMap<String, String>();
	ManSession						session;
	private long					startTime;
	private long					endTime;
	public MoodleUser				currentUser;
	private String					url;
	private String					token;
	private String					userId;
	private static long				backPressed;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// The following line triggers the initialization of ACRA
		ACRA.init(this.getApplication());
		startTime = System.currentTimeMillis();
		setContentView(R.layout.activity_main);
		moodydrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		session = new ManSession(getApplicationContext());
		url = session.getValues(ModConstants.KEY_URL, null);
		token = session.getValues(ModConstants.KEY_TOKEN, null);
		userId = session.getValues(ModConstants.KEY_ID, null);
		getUserData();

		populateLeft();
		populateRight();
		receiveNotification();
		drawerLayoutListener();
		warningMessage(checkConnection(), Toast.LENGTH_LONG, null, getString(R.string.no_internet));

		ChangeLogListView sad = new ChangeLogListView(getApplicationContext());

	}

	private void getUserData() {
		new MainActivityAsyncTask(this).execute(url, token, MoodleServices.CORE_USER_GET_USERS_BY_ID, userId);
	}

	/**
	 * Method to initialize the leftMenu
	 */
	private void populateLeft() {
		populateFullName();
		getUserCourses();
	}

	/**
	 * Method responsible to initialize the right menu
	 */
	private void populateRight() {
		setupSearchView();
		// populateContacts();
	}

	/**
	 *
	 */
	private void warningMessage(boolean statement, int duration, String sucessMessage, String errorMessage) {
		if (statement) {
			if (sucessMessage != null)
				Toast.makeText(this, sucessMessage, duration).show();
		} else if (errorMessage != null)
			Toast.makeText(this, errorMessage, duration).show();
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
	 *
	 * Method to initialize the contacts
	 *
	 */
	private void populateContacts() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout contactsLinearLayout = (LinearLayout) findViewById(R.id.contacts_linear_layout);
		LinearLayout strangersLinearLayout = (LinearLayout) findViewById(R.id.strangers_linear_layout);
		final MoodleContact[] contacts = new ManUserContacts(getApplicationContext()).getContacts();
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
	 * Method responsible to differentiate what type of notification the user
	 * has received
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
			// Bundle bundle = new Bundle();
			// bundle.putSerializable("organizedCourses", organizedCourses);
			// FragmentTransaction fragmentTransaction =
			// getFragmentManager().beginTransaction();
			// FragCoursesList fragment = new FragCoursesList();
			// fragment.setArguments(bundle);
			// fragmentTransaction.addToBackStack(null);
			// fragmentTransaction.replace(R.id.mainFragment, fragment);
			// fragmentTransaction.commit();
			// moodydrawerLayout.closeDrawer(Gravity.LEFT);
		}
	}

	/**
	 *
	 * Method to initialize the user name textview
	 *
	 */
	private void populateFullName() {
		TextView view = (TextView) findViewById(R.id.fullname_textview);
		view.setText(session.getValues(ModConstants.KEY_FULL_NAME, null));
	}

	/**
	 *
	 * Method to responsible to get the user picture from Moodle
	 *
	 */
	void populateUserPicture() {
		ImageButton loginButton = (ImageButton) findViewById(R.id.login_image_button);
		if (session.getValues("PIC_PATH", null) == null) {
			new MainActivityAsyncTask(this).execute(currentUser.getProfileImageURL(), null, MoodleServices.MOODLE_USER_GET_PICTURE, null);
		} else {
			loginButton.setImageBitmap(BitmapResizer.decodeSampledBitmapFromResource(session.getValues("PIC_PATH", null),
					R.id.login_image_button, 100, 100));
		}
	}

	/**
	 *
	 * Method to initialize the user courses
	 *
	 */
	private void getUserCourses() {
		new MainActivityAsyncTask(this).execute(url, token, MoodleServices.CORE_ENROL_GET_USERS_COURSES, userId);
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
					new ManFavorites(getApplicationContext()).insertFavorite(id);

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
				new ModMessage(getString(R.string.favorites_add_message), String.format(getString(R.string.favorites_add_confirm_message),
						organizedCourses.get(Integer.toString(v.getId())))), dialogClickListener, dialogClickListener, false);

	}

	@Override
	public void onClick(View v) {

		FragmentManager fm;
		Intent intent;
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

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
			ManAlertDialog.showMessageDialog(this, new ModMessage("Logout", "Are you sure?"), dialogClickListener, dialogClickListener,
					false);
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
			clearBackStack();
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
			break;

		case R.id.menu_changelog:
			FragChangeLog dialogStandardFragment = new FragChangeLog();
			FragmentManager fm = getFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment prev = fm.findFragmentByTag("changelogdemo_dialog");

			if (prev != null)
				ft.remove(prev);

			dialogStandardFragment.show(ft, "changelogdemo_dialog");
			moodydrawerLayout.closeDrawer(Gravity.LEFT);
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
	 * Private method for tests proposes only
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
	 * Check if the has Internet connection
	 *
	 * @return true or false
	 */
	public boolean checkConnection() {
		ConnectivityManager connec = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (wifi.isConnected() || mobile.isConnected())
			return true;
		return false;
	}

	/**
	 * The interface with all implemented methods with asyncTask response
	 */

	@Override
	public void coursesAsyncTaskResult(Object result) {
		MoodleCourse[] courses = (MoodleCourse[]) result;
		// Start populating the menus and views
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout inserPoint = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		if (courses == null || courses.length == 0)
			fatalError("Moody Fatal Error - Get Courses", "An Error Occurred Retrieving Data contact your Moodle Administrator");
		else
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

				initCoursesPreview();
			}
	}

	private void initCoursesPreview() {
		Bundle bundle = new Bundle();
		bundle.putSerializable("organizedCourses", organizedCourses);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		FragCoursesList fragment = new FragCoursesList();
		fragment.setArguments(bundle);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();
		moodydrawerLayout.closeDrawer(Gravity.LEFT);
	}

	@Override
	public void userAsyncTaskResult(Object result) {
		if (result != null) {
			currentUser = (MoodleUser) result;
			populateUserPicture();
		}
	}

	@Override
	public void courseContentsAsyncTaskResult(Object result) {
	}

	@Override
	public void createContactsAsyncTaskResult(Object result) {
	}

	@Override
	public void deleteContactsAsyncTaskResult(Object result) {
	}

	@Override
	public void blockContactsAsyncTaskResult(Object result) {
	}

	@Override
	public void unblockContactsAsyncTaskResult(Object result) {
	}

	@Override
	public void getContactsAsyncTaskResult(Object result) {
	}

	@Override
	public void sendInstanteMessageAsyncTaskResult(Object result) {
	}

	@Override
	public void pictureAsyncTaskResult(Object result) {
		ImageButton loginButton = (ImageButton) findViewById(R.id.login_image_button);
		loginButton.setBackgroundResource(R.drawable.bkgd_imagebutton);
		loginButton.setImageDrawable((Drawable) result);
	}
}

class MainActivityAsyncTask extends AsyncTask<Object, Void, Object> {
	Object					jObj	= null;
	private ProgressDialog	dialog;
	private CountDownTimer	cvt		= createCountDownTimer();
	private Context			context;
	private MoodleServices	webService;

	public MainActivityAsyncTask(Context context) {
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		cvt.start();
	}

	@Override
	protected Object doInBackground(Object... params) {
		String urlString = (String) params[0];
		String token = (String) params[1];
		webService = (MoodleServices) params[2];
		Object webServiceParams = params[3];

		MoodleCallRestWebService.init(urlString + "/webservice/rest/server.php", token);

		long userId;
		long courseId;
		try {
			switch (webService) {
			case CORE_ENROL_GET_USERS_COURSES:
				userId = Long.parseLong((String) webServiceParams);
				MoodleCourse[] courses = MoodleRestEnrol.getUsersCourses(userId);
				return courses;

			case CORE_USER_GET_USERS_BY_ID:
				userId = Long.parseLong((String) webServiceParams);
				MoodleUser user = MoodleRestUser.getUserById(userId);
				return user;

			case CORE_COURSE_GET_CONTENTS:
				courseId = Long.parseLong((String) webServiceParams);
				MoodleCourseContent[] courseContent = MoodleRestCourse.getCourseContent(courseId, null);
				return courseContent;
			case CORE_MESSAGE_CREATE_CONTACTS:
				MoodleRestMessage.createContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_DELETE_CONTACTS:
				MoodleRestMessage.deleteContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_BLOCK_CONTACTS:
				MoodleRestMessage.blockContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_UNBLOCK_CONTACTS:
				MoodleRestMessage.unblockContacts(parseIds(webServiceParams));
				return true;

			case CORE_MESSAGE_GET_CONTACTS:
				return MoodleRestMessage.getContacts();

			case CORE_MESSAGE_SEND_INSTANT_MESSAGES:
				return MoodleRestMessage.sendInstantMessage((MoodleMessage) webServiceParams);

			case MOODLE_USER_GET_PICTURE:
				InputStream inputStream = new URL(urlString).openStream();
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

	/**
	 * <p>
	 * Method that parses a supposed id list object
	 * </p>
	 *
	 * @param Object
	 *            ids - The object to be parsed to Long[].
	 * @return resultList - The ids List
	 */
	private Long[] parseIds(Object ids) {
		Long[] resultList = null;
		try {
			resultList = (Long[]) ids;
		} catch (Exception e) {
			resultList = new Long[1];
			resultList[0] = (Long) ids;
		}
		return resultList;
	}

	@Override
	protected void onPostExecute(Object obj) {
		cvt.cancel();

		if (dialog != null && dialog.isShowing())
			dialog.dismiss();

		switch (webService) {
		case CORE_ENROL_GET_USERS_COURSES:
			break;

		case CORE_USER_GET_USERS_BY_ID:
			if (obj != null) {
				currentUser = (MoodleUser) obj;
				populateUserPicture();
			}
			break;

		case CORE_COURSE_GET_CONTENTS:
			break;

		case CORE_MESSAGE_CREATE_CONTACTS:
			break;

		case CORE_MESSAGE_DELETE_CONTACTS:
			break;

		case CORE_MESSAGE_BLOCK_CONTACTS:
			break;

		case CORE_MESSAGE_UNBLOCK_CONTACTS:
			break;

		case CORE_MESSAGE_GET_CONTACTS:
			break;

		case CORE_MESSAGE_SEND_INSTANT_MESSAGES:
			break;

		case MOODLE_USER_GET_PICTURE:
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
