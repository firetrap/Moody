package activities;

import interfaces.IgetDialogResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import managers.DialogFragmentManager;
import managers.SessionManager;
import model.MoodyConstants;
import model.MoodyConstants.ActivityCode;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.moody.R;

import connections.DownloadDataTask;

public class MainActivity extends SherlockActivity implements OnClickListener,
		IgetDialogResult {
	private ListView mainListView;
	private ListView leftListView;
	private ListView rightListView;
	private ArrayAdapter<String> listAdapter;

	// Session Manager Class
	SessionManager session;

	private HashMap<String, String> xmlList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// shared pref
		session = new SessionManager(getApplicationContext());
		Toast.makeText(getApplicationContext(),
				"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
				.show();

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Find the ListView resource.
		mainListView = (ListView) findViewById(R.id.main_list_viewer);

		rightListView = (ListView) findViewById(R.id.right_list_viewer);

		// Create and populate a List of planet names.
		String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
				"Jupiter", "Saturn", "Uranus", "Neptune" };

		ArrayList<String> planetList = new ArrayList<String>();
		planetList.addAll(Arrays.asList(planets));

		// Create ArrayAdapter using the planet list.
		listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow,
				planetList);

		// Add more planets. If you passed a String[] instead of a List<String>
		// into the ArrayAdapter constructor, you must not add more items.
		// Otherwise an exception will occur.
		listAdapter.add("Ceres");
		listAdapter.add("Pluto");
		listAdapter.add("Haumea");
		listAdapter.add("Makemake");
		listAdapter.add("Eris");

		// Set the ArrayAdapter as the ListView's adapter.
		mainListView.setAdapter(listAdapter);
		rightListView.setAdapter(listAdapter);

		populateUsername();
		populateLeftListview();
		populateUserPicture();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// // Inflate the menu; this adds items to the action bar if it is
		// present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);

		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// switch (item.getItemId()) {
		// case R.id.menu_settings:
		// Intent intent = new Intent(this, LoginActivity.class);
		// intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// startActivity(intent);
		// break;
		//
		// default:
		// return super.onOptionsItemSelected(item);
		// }

		return true;
	}

	// Method to decide what to do from what button was pressed
	public void onClick(View v) {
		session = new SessionManager(getApplicationContext());
		switch (v.getId()) {
		case R.id.login_image_button:
			if (session.isLoggedIn() == false) {
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {

				FragmentManager fm = getFragmentManager();
				DialogFragmentManager userDetailsDialog = new DialogFragmentManager();
				userDetailsDialog.setRetainInstance(true);
				userDetailsDialog.show(fm, "fragment_name");

			}
			break;
		case R.id.logout_image_button:
			if (session.isLoggedIn() == true) {
				session.logoutUser();

				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);

			} else {
				// só entra neste else caso o utilizador ainda nao esteja
				// loggado entao é reencaminhado para o LoginActivity
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			break;

		default:
			throw new RuntimeException("Unknown button ID");
		}
	}

	public void populateUsername() {

		if (session.isLoggedIn() == true) {
			TextView view = (TextView) findViewById(R.id.fullName_textView);
			try {

				String url = session.getValues(
						MoodyConstants.MoodySession.KEY_URL, null);
				String token = session.getValues(
						MoodyConstants.MoodySession.KEY_TOKEN, null);

				String con = String.format(
						MoodyConstants.MoodySession.KEY_N_PARAMS, url, token,
						"core_webservice_get_site_info");

				xmlList = new DownloadDataTask().execute(con, "xml").get();
				view.setText(xmlList.get("fullname1"));

				xmlList.clear();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void populateLeftListview() {
		leftListView = (ListView) findViewById(R.id.left_list_viewer);

		if (session.isLoggedIn() == true) {
			String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
					null);
			String token = session.getValues(
					MoodyConstants.MoodySession.KEY_TOKEN, null);

			String id = session.getValues(MoodyConstants.MoodySession.KEY_ID,
					null);

			String con = String.format(MoodyConstants.MoodySession.KEY_PARAMS,
					url, token, "core_enrol_get_users_courses&userid", id);

			try {
				xmlList = new DownloadDataTask().execute(con, "xml").get();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<String> coursesList = new ArrayList<String>();

			for (String keyName : xmlList.keySet()) {
				if (keyName.length() >= "fullname".length()
						&& keyName.substring(0, 8).equals("fullname")) {
					String coursesIndi = xmlList.get(keyName);
					coursesList.add(coursesIndi);

				}

			}
			xmlList.clear();
			listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow,
					coursesList);
			listAdapter.add("Ceres");
			listAdapter.add("Pluto");
			listAdapter.add("Haumea");
			listAdapter.add("Makemake");
			listAdapter.add("Eris");
			listAdapter.add("Ceres");
			listAdapter.add("Pluto");
			listAdapter.add("Haumea");
			listAdapter.add("Makemake");
			listAdapter.add("Eris");
			listAdapter.add("Ceres");
			listAdapter.add("Pluto");
			listAdapter.add("Haumea");
			listAdapter.add("Makemake");
			listAdapter.add("Eris");
			leftListView.setAdapter(listAdapter);
		}

	}

	public void populateUserPicture() {
		if (session.isLoggedIn() == true) {
			ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
			try {

				String url = session.getValues(
						MoodyConstants.MoodySession.KEY_URL, null);
				String token = session.getValues(
						MoodyConstants.MoodySession.KEY_TOKEN, null);

				String con = String.format(
						MoodyConstants.MoodySession.KEY_N_PARAMS, url, token,
						"core_webservice_get_site_info");

				xmlList = new DownloadDataTask().execute(con, "xml").get();
				String userPictureUrl = xmlList.get("userpictureurl1");

				Drawable pic = DownloadDataTask
						.createDrawableFromUrl(userPictureUrl);

				login_button.setBackgroundResource(R.drawable.bkgd_imagebutton);
				login_button.setImageDrawable(pic);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * The first case of the switch it's not fully implemented as planned,
	 * moodle doesn't support the users to update their own user details this
	 * issue is described and reported here
	 * https://tracker.moodle.org/browse/CONTRIB-4282 The next code it's ready
	 * 
	 * TO DO: implement the user update details in Moody when Moodle.org decide
	 * to add the required web service function.
	 * 
	 */
	@Override
	public void onFinishEditDialog(String inputText, int code) {
		switch (code) {
		case ActivityCode.DIALOG_FRAG_USER_PIC:
			ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
			login_button.setImageDrawable(Drawable.createFromPath(inputText));

			break;

		default:
			break;
		}

	}

}
