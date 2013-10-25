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
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
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