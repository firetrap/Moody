package activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import managers.DialogsManager;
import managers.SessionManager;
import model.MoodyConstants.MoodySession;
import model.MoodyMessage;

import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moody.R;

import connections.DataAsyncTask;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for username and password at the time of the login attempt.
	private String mUrl;
	private String mToken;
	private String mUser;
	private String mPassword;

	// UI references.
	private EditText mUrlView;
	private EditText mUserView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private String finalToken = "";
	private String url = "";
	private String UserId = "";
	private JSONObject getJson;

	private String jsonFormat = MoodySession.KEY_JSONFORMAT;

	// Session Manager Class
	SessionManager session;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		session = new SessionManager(getApplicationContext());
		Toast.makeText(getApplicationContext(),
				"User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG)
				.show();

		if (session.isLoggedIn()) {
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();

		} else {

			setContentView(R.layout.activity_login);

			// Set up the login form.
			mUrlView = (EditText) findViewById(R.id.prompt_url);
			mUser = getIntent().getStringExtra(EXTRA_EMAIL);
			mUserView = (EditText) findViewById(R.id.username);
			mUserView.setText(mUser);

			mPasswordView = (EditText) findViewById(R.id.password);
			mPasswordView
					.setOnEditorActionListener(new TextView.OnEditorActionListener() {
						@Override
						public boolean onEditorAction(TextView textView,
								int id, KeyEvent keyEvent) {
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

			findViewById(R.id.sign_in_button).setOnClickListener(
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							attemptLogin();
						}
					});
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
	public void attemptLogin() {
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

		boolean cancel = false;
		String error = "Errors found: \n";
		View focusView = null;

		// Check for url.
		if (TextUtils.isEmpty(mUrl)) {
			mUrlView.setError(getString(R.string.error_field_required));
			focusView = mUrlView;
			cancel = true;
			error += "\t - URL\n";

		} else if (mUrl.length() < 20) {
			mUrlView.setError(getString(R.string.error_invalid_url));
			focusView = mUrlView;
			cancel = true;
			error += "\t - URL\n";
		}

		// Check if URL contains the required http protocol.
		else {
			if (!mUrl.subSequence(0, 7).equals("http://")) {
				mUrl = "http://" + mUrl;
			}
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
			error += "\t - Password\n";

		} else if (mPassword.length() <= 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
			error += "\t - Password\n";
		}

		// Check for a valid user.
		if (TextUtils.isEmpty(mUser)) {
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
			error += "\t - User\n";
		}

		// Try to verify user&password with the services
		if (!cancel) {
			// Inicialize the full context to generate token &&
			mToken = mUrl + "/login/token.php?username=" + mUser + "&password="
					+ mPassword + "&service=moody_service" + jsonFormat;
			try {
				getJson = new DataAsyncTask().execute(mToken, "json").get();

				if (getJson == null) {
					cancel = true;
					error += "\t - Error in Authentication service- check your internet service or the website URL\n";
					mUrlView.setError(getString(R.string.error_invalid_url));
					focusView = mUrlView;

				} else {
					if (getJson.has("error")) {
						cancel = true;
						mUserView
								.setError(getString(R.string.error_invalid_username));
						mPasswordView
								.setError(getString(R.string.error_incorrect_password));
						focusView = mUserView;
						error += "\t - Error in Authentication service - "
								+ (String) getJson.get("error") + "\n";
					}
					if (getJson.has("token")) {
						finalToken = (String) getJson.get("token");
					}
				}

			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ExecutionException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (!finalToken.isEmpty()) {

			// Get user id URL
			url = mUrl + "/webservice/rest/server.php?wstoken=" + finalToken
					+ "&wsfunction=core_webservice_get_site_info" + jsonFormat;

			try {
				getJson = new DataAsyncTask().execute(url, "json").get();
				UserId = Integer.toString((Integer) getJson.get("userid"));

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.

			focusView.requestFocus();
			DialogsManager.showMessageDialog(this, new MoodyMessage(
					"Login Error", error), false);

		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);

			Log.d("MoodyDebug", mToken);
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
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			Log.d("MoodyDebug", "Entrou no Async");
			URL url = null;

			try {
				url = new URL(mUrl);
				Log.d("AsyncTask", url.toString());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("MoodyErrorConnection", e.toString());
				return false;
			}
			try {
				Log.d("MoodyDebug", "Connect");
				HttpURLConnection con = (HttpURLConnection) url
						.openConnection();
				con.connect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("MoodyDebug", "Cant Connect");
				return false;

			}

			return true;

		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {
				// Session Manager and shared pref send to shared pref:
				// user-name, user-token, User-id in database
				session = new SessionManager(getApplicationContext());
				session.createLoginSession(mUser, finalToken, UserId, mUrl);

				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				finish();
			} else {
				Log.d("MoodyDebug", "onPOstExecute-FAILED");
				mPasswordView

				.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
	}
}
