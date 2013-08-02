package activities;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import managers.SessionManager;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;

import com.example.moody.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginActivity extends Activity {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */

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
	private String FinalToken;

	// Session Manager Class
		SessionManager session;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
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

		
		//shared pref
		session = new SessionManager(getApplicationContext());
        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(), "VALUES: " + session.getValues(mUser, null), Toast.LENGTH_LONG).show();

		
		
		findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
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
		mUser = mUserView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mUrl = mUrlView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for url.
		if (TextUtils.isEmpty(mUrl)) {
			mUrlView.setError(getString(R.string.error_field_required));
			focusView = mUrlView;
			cancel = true;

		} else if (mUrl.length() < 20) {
			mUrlView.setError(getString(R.string.error_invalid_url));
			focusView = mUrlView;
			cancel = true;
		}

		// Check if URL contains the required http protocol.
		if (mUrl.subSequence(0, 7).equals("http://")) {

		} else {
			mUrl = "http://" + mUrl;
		}

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;

		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid user.
		if (TextUtils.isEmpty(mUser)) {
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
		}

		// Inicialize the full context to generate token.
		mToken = mUrlView.getText().toString() + "/login/token.php?username="
				+ mUser + "&password=" + mPassword + "&service=moody_service";

		// checks token integrity
		if (getToken().toString().length() != 32 || getToken().toString().contains("username")) {
			mUserView.setError(getString(R.string.error_invalid_username));
			focusView = mUserView;
			mPasswordView
					.setError(getString(R.string.error_incorrect_password));
			focusView = mPasswordView;
			cancel = true;
			Log.d("MoodyDebug", "getToken failed");
		} else {
			FinalToken = getToken();

		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
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
				// Session Manager and shared pref
		        session = new SessionManager(getApplicationContext());
		        session.createLoginSession(mUser, FinalToken);
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
	
	/**
	 * Inicialize the requirements for getSiteStats the required token from the site.
	 */
	public String getToken() {

		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		// decide output
		String value = "";
		String userToken = "";
		try {
			value = getSiteStats();
			StringTokenizer tokens = new StringTokenizer(value, "\"");

			do {
				userToken = tokens.nextToken();

			} while (userToken.length() != 32);

			Log.d("MoodyDebug", "aqui" + userToken);
			return userToken;
		} catch (Exception ex) {
			Log.d("MoodyDebug",
					"userToken failed in: getToken()-> " + ex.toString());

		}
		return userToken;

	}
	/**
	
 * Get the required token from the site.
	 */
	public String getSiteStats() throws Exception {
		String stats = "";

		// config cleaner properties

		HtmlCleaner htmlCleaner = new HtmlCleaner();
		CleanerProperties props = htmlCleaner.getProperties();
		props.setAllowHtmlInsideAttributes(false);
		props.setAllowMultiWordAttributes(true);
		props.setRecognizeUnicodeChars(true);
		props.setOmitComments(true);

		// Check if token contains the required http protocol.
		if (mToken.subSequence(0, 7).equals("http://")) {

		} else {
			mToken = "http://" + mToken;
		}
		Log.d("Check", mToken);

		// create URL object
		URL urlToken = new URL(mToken);
		// get HTML page root node
		TagNode root = htmlCleaner.clean(urlToken);

		// query XPath
		Object[] statsNode = root.evaluateXPath("");
		// process data if found any node
		if (statsNode.length > 0) {
			// I already know there's only one node, so pick index at 0.
			TagNode resultNode = (TagNode) statsNode[0];
			// get text data from HTML node
			stats = resultNode.getText().toString();
		}

		Log.d("MoodyDebug", stats);

		return stats;
	}

}