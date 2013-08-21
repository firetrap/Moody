package activities;

import interfaces.InterfaceDialogFrag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import managers.DialogFragmentManager;
import managers.SessionManager;
import model.MoodyConstants;
import model.MoodyConstants.ActivityCode;
import android.R.attr;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import bitmap.BitmapResizer;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.moody.R;

import connections.DownloadDataTask;

public class MainActivity extends SherlockActivity implements OnClickListener,
		InterfaceDialogFrag, OnItemClickListener {
	private ListView mainListView;
	private ListView leftListView;
	private ListView rightListView;
	private ArrayAdapter<String> listAdapter;

	// Session Manager Class
	SessionManager session;

	private HashMap<String, String> xmlList;

	private ArrayList<String> coursesNamesList;
	private ArrayList<String> coursesIdList;

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
	@Override
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

				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							session.logoutUser();

							Intent intent = new Intent(getApplicationContext(),
									MainActivity.class);
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
							startActivity(intent);
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							dialog.dismiss();

							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Logout");
				builder.setMessage("Are you sure?")
						.setPositiveButton("Yes", dialogClickListener)
						.setNegativeButton("No", dialogClickListener).show();

			} else {
				// s� entra neste else caso o utilizador ainda nao esteja
				// loggado entao � reencaminhado para o LoginActivity
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			break;

		case R.id.textView1:
			Intent intent = new Intent(getApplicationContext(),
					UserDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			break;

		default:
			throw new RuntimeException("Unknown button ID");
		}
	}

	public void populateUsername() {

		if (session.isLoggedIn() == true) {
			TextView view = (TextView) findViewById(R.id.textView1);
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
		// leftListView = (ListView) findViewById(R.id.left_list_viewer);
		// leftListView.setOnItemClickListener(this);

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

			coursesNamesList = new ArrayList<String>();
			coursesIdList = new ArrayList<String>();

			for (String keyName : xmlList.keySet()) {
				if (keyName.length() >= "fullname".length()
						&& keyName.substring(0, 8).equals("fullname"))
					coursesNamesList.add(xmlList.get(keyName));

				if (keyName.length() >= "id".length()
						&& keyName.substring(0, 2).equals("id")) {
					int number = 0;

					try {
						number = Integer.parseInt(keyName.substring(2));
					} catch (NumberFormatException ex1) {
					}

					if (number > 0)
						coursesIdList.add(xmlList.get(keyName));

				}

			}
			xmlList.clear();
			listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow,
					coursesNamesList);

			coursesInit();

		} else {

			coursesInitEmpty();
		}
	}

	/**
	 * @throws NumberFormatException
	 */
	private void coursesInit() throws NumberFormatException {
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		for (int j = coursesIdList.size() - 1; j >= 0; j--) {

			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			Button btnTag = new Button(this);
			btnTag.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			btnTag.setText(coursesNamesList.get(j));
			btnTag.setId(Integer.parseInt(coursesIdList.get(j)));
			btnTag.setBackgroundColor(attr.selectableItemBackground);

			if (j < coursesIdList.size() - 1) {
				btnTag.setBackgroundResource(R.drawable.border_inside);
			}

			btnTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.books, 0,
					0, 0);
			btnTag.setCompoundDrawablePadding(10);
			btnTag.setPadding(10, 0, 0, 0);
			btnTag.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			btnTag.setOnClickListener(this);

			row.addView(btnTag);

			layout.addView(row, 3);
		}
	}

	/**
	 * 
	 */
	private void coursesInitEmpty() {
		LinearLayout layout = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		for (int j = 5; j >= 0; j--) {

			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));

			Button btnTag = new Button(this);
			btnTag.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			btnTag.setText("Your Course " + (j + 1));
			btnTag.setId(j);
			btnTag.setBackgroundColor(attr.selectableItemBackground);

			if (j < 5) {
				btnTag.setBackgroundResource(R.drawable.border_inside);
			}

			btnTag.setCompoundDrawablesWithIntrinsicBounds(R.drawable.books, 0,
					0, 0);
			btnTag.setCompoundDrawablePadding(10);
			btnTag.setPadding(10, 0, 0, 0);
			btnTag.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			btnTag.setOnClickListener(this);

			row.addView(btnTag);

			layout.addView(row, 3);
		}
	}

	public void populateUserPicture() {
		ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
		if (session.isLoggedIn() == true) {
			if (session.getValues("PIC_PATH", null) == null) {

				try {

					String url = session.getValues(
							MoodyConstants.MoodySession.KEY_URL, null);
					String token = session.getValues(
							MoodyConstants.MoodySession.KEY_TOKEN, null);

					String con = String.format(
							MoodyConstants.MoodySession.KEY_N_PARAMS, url,
							token, "core_webservice_get_site_info");

					xmlList = new DownloadDataTask().execute(con, "xml").get();
					String userPictureUrl = xmlList.get("userpictureurl1");

					Drawable pic = DownloadDataTask
							.createDrawableFromUrl(userPictureUrl);

					login_button
							.setBackgroundResource(R.drawable.bkgd_imagebutton);
					login_button.setImageDrawable(pic);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {

				login_button.setImageBitmap(BitmapResizer
						.decodeSampledBitmapFromResource(
								session.getValues("PIC_PATH", null),
								R.id.login_image_button, 100, 100));

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
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

		// ############## JA TEM O ID, FALTA CHAMAR O GET_CONTENTS DA CADEIRA.
		String id = "";

		try {
			id = coursesIdList.get(arg2);
		} catch (Exception ex1) {

		}

		Toast.makeText(getApplicationContext(), "selected :" + id,
				Toast.LENGTH_SHORT).show();

	}
}
