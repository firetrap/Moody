package activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import managers.ManAlertDialog;
import managers.ManSession;
import model.ModConstants;
import model.ModMessage;

import org.json.JSONException;
import org.json.JSONObject;

import restPackage.MoodleCallRestWebService;
import restPackage.MoodleRestException;
import restPackage.MoodleRestWebService;
import restPackage.MoodleRestWebServiceException;
import restPackage.MoodleWebService;
import service.ServiceBackground;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firetrap.moody.R;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fabio Barreiros - Moody Founder
 */

/**
 *
 * Activity which displays a login screen to the user, offering registration as
 * well.
 *
 * @author firetrap
 *
 */
public class LoginActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	private String finalToken = "";

	/**
	 * Keep track of the login initActivity to ensure we can cancel it if
	 * requested.
	 */
	private UserLoginTask mAuthTask = null;

	private View mLoginFormView;
	private TextView mLoginStatusMessageView;
	private View mLoginStatusView;
	private String mPassword;

	// Values for user name and password at the time of the login attempt.
	private String mUrl;
	// UI references.
	private EditText mUrlView;
	private EditText mPasswordView;
	private EditText mUserView;
	private String mUser;

	// License and trademark
	private TextView trademark;
	private TextView licence;

	// ManSession Manager Class
	ManSession session;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session = new ManSession(getApplicationContext());

		if (session.isLoggedIn()) {
			final Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			startActivity(intent);
			finish();

		} else {

			setContentView(R.layout.activity_login);

			// Set up the login form.
			trademark = (TextView) findViewById(R.id.trademark);
			trademark.setMovementMethod(LinkMovementMethod.getInstance());

			licence = (TextView) findViewById(R.id.licence);
			licence.setMovementMethod(LinkMovementMethod.getInstance());

			mUrlView = (EditText) findViewById(R.id.prompt_url);
			mUser = getIntent().getStringExtra(EXTRA_EMAIL);
			mUserView = (EditText) findViewById(R.id.username);
			mUserView.setText(mUser);

			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
					if (id == R.id.login || id == EditorInfo.IME_NULL) {
						attemptLogin();
						return true;
					}
					return false;
				}
			});

			mLoginFormView = findViewById(R.id.login_form);
			mLoginStatusView = findViewById(R.id.login_status);
			mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

			findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					attemptLogin();
				}
			});

			// onSoftKeyboardState();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	private void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUrlView.setError(null);
		mUserView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mUrl = mUrlView.getText().toString().trim();
		mUser = mUserView.getText().toString().trim();
		mPassword = mPasswordView.getText().toString().trim();

		// FOR DEV ONLY, IT WILL CONNECT TO THE TESTS SERVER
		mUrl = ModConstants.TEST_MODE_URL;
		mUser = ModConstants.TEST_MODE_USER;
		mPassword = ModConstants.TEST_MODE_PASSWORD;

		boolean cancel = false;
		String error = "Errors found: \n\n";
		View focusView = null;

		// Check for URL.
		if (TextUtils.isEmpty(mUrl)) {
			mUrlView.setError(getString(R.string.error_field_required));
			focusView = mUrlView;
			cancel = true;
			error += "URL\n";

		}
		// else if (mUrl.length() < 20) {
		// mUrlView.setError(getString(R.string.error_invalid_url));
		// focusView = mUrlView;
		// cancel = true;
		// error += "URL\n";
		// }

		// Check if URL contains the required HTTP protocol.
		else {
			if (mUrl.length() < 4 || !mUrl.subSequence(0, 4).equals("http")) {
				mUrl = "http://" + mUrl;
			}
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
			error += "Password\n";

		}
		// else if (mPassword.length() <= 4) {
		// mPasswordView.setError(getString(R.string.error_invalid_password));
		// focusView = mPasswordView;
		// cancel = true;
		// error += "Password\n";
		// }

		// Check for a valid user.
		if (TextUtils.isEmpty(mUser)) {
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
			error += "User\n";
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.

			focusView.requestFocus();
			ManAlertDialog.showMessageDialog(this, new ModMessage(getResources().getString(R.string.login_error), error), false);

		} else {
			// Show a progress spinner, and kick off a background initActivity
			// to
			// perform the user login attempt.
			hideKeyboard();

			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);

			// Log.d("MoodyDebug", mToken);
		}
	}

	/**
	 * Hides the keyboard
	 *
	 * @author hsousa
	 */
	private void hideKeyboard() {
		InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	/**
	 * Represents an asynchronous login/registration initActivity used to
	 * authenticate the user.
	 */
	private class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		JSONObject jObj = null;

		private String error = getResources().getString(R.string.errors_found) + "\n\n";
		private EditText focusView = new EditText(getApplicationContext());

		private String userId = "";
		private String fullName = "";

		@Override
		protected Boolean doInBackground(Void... params) {

			String url = mUrl + "/login/token.php?username=" + mUser + "&password=" + mPassword + "&service="
					+ getResources().getString(R.string.moodle_service_name);

			try {
				return loadFromNetwork(url);
			} catch (IOException e) {

				error += getResources().getString(R.string.error_internet_url) + "\n";
				focusView.setError(getString(R.string.error_invalid_url));
				return false;
			}

		}

		private Boolean loadFromNetwork(String urlString) throws IOException {

			InputStream inputStream = null;
			String json = null;

			try {
				inputStream = downloadUrl(urlString);
				BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				json = sb.toString();

				jObj = new JSONObject(json);

				if (jObj == null) {
					error += getResources().getString(R.string.error_internet_url) + "\n";
					focusView.setError(getString(R.string.error_invalid_url));
					return false;

				} else {
					if (jObj.has("error")) {
						error += getResources().getString(R.string.error_authentication_service) + " - " + (String) jObj.get("error")
								+ "\n";

						focusView.setError(getString(R.string.error_incorrect_password_username));

						return false;
					}
					// On getToken success it will get the user id
					if (jObj.has("token")) {
						finalToken = (String) jObj.get("token");
						MoodleCallRestWebService.init(mUrl + "/webservice/rest/server.php", finalToken);
						MoodleWebService getSiteInfo = MoodleRestWebService.getSiteInfo();
						userId = Long.toString(getSiteInfo.getUserId());
						fullName = getSiteInfo.getFullName();
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				error += getResources().getString(R.string.error_internet_url) + "\n";
				focusView.setError(getString(R.string.error_invalid_url));
				return false;
			} catch (MoodleRestWebServiceException e) {
				e.printStackTrace();
				error += getResources().getString(R.string.error_get_user_info) + "\n";
				focusView.setError(getString(R.string.error_invalid_url));
				return false;
			} catch (MoodleRestException e) {
				e.printStackTrace();
				error += getResources().getString(R.string.error_get_user_info) + "\n";
				focusView.setError(getString(R.string.error_invalid_url));
				return false;
			}

			finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
				} catch (Exception e) {
					return false;
				}
			}
			return true;

		}

		private InputStream downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			InputStream stream = conn.getInputStream();
			return stream;
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}

		@Override
		protected void onPostExecute(Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				// Send to shared preferences:
				// user-name, user-token, user-id, full name
				session = new ManSession(getApplicationContext());
				session.createLoginSession(mUser, fullName, finalToken, userId, mUrl);

				showProgress(true);
				getApplicationContext().startService(new Intent(getApplicationContext(), ServiceBackground.class));
				Handler handler = new Handler();
				handler.postDelayed(initActivity, 5000);

			} else {
				if (focusView.getError().equals(getString(R.string.error_invalid_url))) {
					mUrlView.setError(getString(R.string.error_invalid_url));
					mUrlView.requestFocus();
				}
				if (focusView.getError().equals(getString(R.string.error_incorrect_password_username))) {
					mPasswordView.setError(getString(R.string.error_incorrect_password_username));
					mPasswordView.requestFocus();
					mUserView.setError(getString(R.string.error_incorrect_password_username));
					mUserView.requestFocus();
				}

				ManAlertDialog.showMessageDialog(LoginActivity.this, new ModMessage("Login Error", error), false);
			}
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
				}
			});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
				}
			});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}

	/**
	 *
	 * This will wait until the main activity is loaded to start the intent
	 *
	 */
	private Runnable initActivity = new Runnable() {
		@Override
		public void run() {

			Intent intent = new Intent(getApplicationContext(), MainActivity.class);
			// intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			finish();
		}
	};

	/**
	 *
	 * Android OS doesn't support hiding view's on SoftKeyboard call, so we have
	 * to implement our own method
	 *
	 */
	@SuppressWarnings("unused")
	private void onSoftKeyboardState() {
		final View activityRootView = findViewById(R.id.login_form);
		activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				Rect r = new Rect();
				// r will be populated with the coordinates of your
				// view that area still visible.
				activityRootView.getWindowVisibleDisplayFrame(r);

				int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
				if (heightDiff > 100) {
					// if more than 100 pixels,its probably a
					// keyboard

					findViewById(R.id.LoginFormContainer).setLayoutParams(
							new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT,
									android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
					activityRootView.invalidate();

				} else {
					findViewById(R.id.LoginFormContainer).setLayoutParams(
							new LinearLayout.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 0, 2f));
					activityRootView.invalidate();

				}
			}
		});
	}

}
