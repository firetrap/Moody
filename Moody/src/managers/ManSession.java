package managers;

import java.util.HashMap;

import model.ModConstants;
import activities.LoginActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

/**
 * @author firetrap
 *
 */
public class ManSession {
	// Context
	Context _context;

	// Editor for Shared preferences
	Editor editor;

	// Shared Preferences
	SharedPreferences pref;


	/**
	 * @param context
	 */
	public ManSession(Context context) {
		this._context = context;
		pref = _context.getSharedPreferences(ModConstants.PREF_NAME,
				ModConstants.PRIVATE_MODE);
		editor = pref.edit();
	}

	/**
	 * @param preference
	 */
	public void addPref(String preference) {

		// Storing preference in preferences
		editor.putString("PIC_PATH", preference);

		// commit changes
		editor.commit();
	}


	/**
	 * @param preference
	 */
	public void appVersion(String preference) {

		// Storing preference in preferences
		editor.putString("appVersion", preference);

		// commit changes
		editor.commit();
	}

	/**
	 * Check login method will check user login status If false it will redirect
	 * user to login page Else won't do anything
	 * */
	public void checkLogin() {
		// Check login status
		if (!this.isLoggedIn()) {
			// user is not logged in redirect him to Login Activity
			final Intent i = new Intent(_context, LoginActivity.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

			// Staring Login Activity
			_context.startActivity(i);
		}

	}

	/**
	 * @param name
	 * @param fullName
	 * @param token
	 * @param id
	 * @param mUrl
	 */
	public void createLoginSession(String name, String fullName, String token,
			String id, String mUrl) {
		// Storing login value as TRUE
		editor.putBoolean(ModConstants.IS_LOGIN, true);

		// Storing name in preferences
		editor.putString(ModConstants.KEY_NAME, name);

		// Storing full name in preferences
		editor.putString(ModConstants.KEY_FULL_NAME, fullName);

		// Storing email in preferences
		editor.putString(ModConstants.KEY_TOKEN, token);

		// Storing user id in preferences
		editor.putString(ModConstants.KEY_ID, id);

		// Storing url in preferences
		editor.putString(ModConstants.KEY_URL, mUrl);

		// commit changes
		editor.commit();
	}

	/**
	 *
	 * Get stored session data
	 *
	 * @return HashMap<String, String>
	 */
	public HashMap<String, String> getUserDetails() {
		final HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(ModConstants.KEY_NAME,
				pref.getString(ModConstants.KEY_NAME, null));

		user.put(ModConstants.KEY_NAME,
				pref.getString(ModConstants.KEY_FULL_NAME, null));
		// user token
		user.put(ModConstants.KEY_TOKEN,
				pref.getString(ModConstants.KEY_TOKEN, null));

		// user token
		user.put(ModConstants.KEY_ID, pref.getString(ModConstants.KEY_ID, null));

		// user url
		user.put(ModConstants.KEY_URL,
				pref.getString(ModConstants.KEY_URL, null));

		// return user
		return user;
	}

	/**
	 * @param name
	 * @param options
	 * @return String
	 */
	public String getValues(String name, String options) {

		return pref.getString(name, options);

	}

	/**
	 * Quick check for login
	 * **/
	// Get Login State
	public boolean isLoggedIn() {
		return pref.getBoolean(ModConstants.IS_LOGIN, false);
	}

	/**
	 * Clear session details
	 * */
	public void logoutUser() {
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();

		// // After logout redirect user to Main Activity
		// Intent i = new Intent(_context, LoginActivity.class);
		// // Closing all the Activities
		// i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		//
		// // Add new Flag to start new Activity
		// i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//
		// // Staring Main Activity
		// _context.startActivity(i);
	}
}
