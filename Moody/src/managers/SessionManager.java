package managers;

import java.util.HashMap;

import activities.LoginActivity;
import activities.MainActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;

	// Editor for Shared preferences
	Editor editor;

	// Context
	Context _context;

	// Shared pref mode
	int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "MoodyPreferences";

	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";

	// Email address (make variable public to access from outside)
	public static final String KEY_TOKEN = "token";

	// User ID
	public static final String KEY_ID = "id";

	// Moodle URL
	public static final String KEY_URL = "url";

	// Constructor
	public SessionManager(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * Create login session
	 * 
	 * @param mUrl
	 * */
	public void createLoginSession(String name, String token, String id, String mUrl){
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		
		// Storing name in preferences
		editor.putString(KEY_NAME, name);
		
		// Storing email in preferences
		editor.putString(KEY_TOKEN, token);
		
		
		// Storing user id in preferences
		editor.putString(KEY_ID, id);
		
		editor.putString(KEY_URL, mUrl);
		
		// commit changes
		editor.commit();
	}
	public String getValues(String name, String oppp) {

		return pref.getString(name, oppp);

	}

	/**
	 * Check login method will check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public void checkLogin() {
		// Check login status
		if (!this.isLoggedIn()) {
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);
		}

	}

	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails() {
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));

		// user token
		user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

		// user token
		user.put(KEY_ID, pref.getString(KEY_ID, null));

		// user url
		user.put(KEY_URL, pref.getString(KEY_URL, null));

		// return user
		return user;
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser() {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// After logout redirect user to Main Activity
		Intent i = new Intent(_context, MainActivity.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		// Staring Main Activity
		_context.startActivity(i);
	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(IS_LOGIN, false);
	}
}
