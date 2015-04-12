package activities;

import managers.ManAlertDialog;
import managers.ManDataStore;
import managers.ManSession;
import model.ModConstants;
import model.ModMessage;
import restPackage.MoodleCallRestWebService;
import restPackage.MoodleRestUser;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.firetrap.moody.R;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 */


/**
 * @author SérgioFilipe
 *
 */
public class UserDetailsActivity extends Activity {
	Context				context;
	ManSession			session;
	ManDataStore		data;
	private String		userId;
	private MoodleUser	loggedUserData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);

		this.context = this;

		// Cache data store
		this.data = new ManDataStore(context);

		// shared pref
		session = new ManSession(context);

		// Logged user id
		userId = session.getValues(ModConstants.KEY_ID, null);

		initComponents();
	}

	/**
	 * Method responsible to initialize the userDetails activity
	 */
	private void initComponents() {
		String fileName2Store = MoodleServices.CORE_USER_GET_USERS_BY_ID.name() + userId;

		if (data.isInCache(fileName2Store)) {
			loggedUserData = (MoodleUser) data.getData(fileName2Store);
			initDetails(loggedUserData);
		} else
			new UserDetailsDataAsyncTask().execute(MoodleServices.CORE_USER_GET_USERS_BY_ID, fileName2Store);
	}

	/**
	 * After getting the data it init moodle user
	 *
	 * @param asyncTaskObj
	 */
	private void userDataAsyncResult(Object asyncTaskObj) {
		// After getting the data it init moodle user
		loggedUserData = (MoodleUser) asyncTaskObj;

		if (loggedUserData != null) {
			initDetails(loggedUserData);
		} else {
			ManAlertDialog.showMessageDialog(this, new ModMessage("Moody Error", "An Error Occurred Retrieving Data"),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}

					}, false);
		}
	}

	/**
	 * Initialize all the required views
	 *
	 * @param user
	 */
	private void initDetails(MoodleUser user) {
		processTextView(R.id.textView_full_name, R.id.relativeLayout_fullname, user.getFullname(), false);
		processTextView(R.id.editText_firstname, R.id.linearLayout_firstname, user.getFirstname(), false);
		processTextView(R.id.editText_lastname, R.id.linearLayout_lastname, user.getLastname(), false);
		processTextView(R.id.editText_email, R.id.linearLayout_email, user.getEmail(), false);
		processTextView(R.id.editText_address, R.id.linearLayout_address, user.getAddress(), false);
		processTextView(R.id.editText_phonenumber, R.id.linearLayout_phonenumber, user.getPhone1(), false);
		processTextView(R.id.editText_mobilephonenumber, R.id.linearLayout_mobilephonenumber, user.getPhone2(), false);
		processTextView(R.id.editText_description, R.id.linearLayout_description, user.getDescription(), true);
		processTextView(R.id.editText_url, R.id.linearLayout_url, user.getURL(), false);
		processTextView(R.id.editText_skype, R.id.editText_skype, user.getSkype(), false);
		processTextView(R.id.editText_yahoo, R.id.linearLayout_yahoo, user.getYahoo(), false);

	}

	/**
	 *
	 * If the field is valid it show it else hide the textview
	 *
	 * @param vID
	 * @param lID
	 * @param text
	 * @param hasHTML
	 */
	private void processTextView(int vID, int lID, String text, boolean hasHTML) {
		if (!(isValid(text)))
			showHideLayout(lID, false);
		else
			initTextView(vID, text, hasHTML);
	}

	private boolean isValid(String propertie) {
		return ((propertie != null) && (!propertie.isEmpty()));
	}

	/**
	 * responsible to show or hide only the available fields from Moodle
	 *
	 * @param lID
	 * @param show
	 */
	private void showHideLayout(int lID, Boolean show) {
		findViewById(lID).setVisibility(!(show) ? View.GONE : View.VISIBLE);
	}

	/**
	 *
	 * Check if the data from Moodle is string or HTML and set the correct
	 * attribute
	 *
	 * @param vID
	 * @param text
	 * @param hasHTML
	 */
	private void initTextView(int vID, String text, boolean hasHTML) {
		((TextView) findViewById(vID)).setText(!((hasHTML)) ? text : Html.fromHtml(text));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_details, menu);
		return true;
	}

	private class UserDetailsDataAsyncTask extends AsyncTask<Object, Void, Object> {
		private ProgressDialog	dialog;
		private CountDownTimer	cvt	= createCountDownTimer();
		private MoodleServices	webService;
		private String			fileName2Store;

		public UserDetailsDataAsyncTask() {
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

			MoodleCallRestWebService.init(serverUrl + "/webservice/rest/server.php", token);

			try {
				switch (webService) {

				case CORE_USER_GET_USERS_BY_ID:
					MoodleUser user = MoodleRestUser.getUserById(Long.parseLong(userId));
					return user;

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

			case CORE_USER_GET_USERS_BY_ID:
				userDataAsyncResult(asyncTaskObj);
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
