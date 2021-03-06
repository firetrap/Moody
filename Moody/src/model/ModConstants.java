package model;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. F�bio Barreiros - Moody Founder
 */

/**
 *
 * ModConstants - Classe que cont�m todos os valores constants da aplica��o.
 *
 * @author S�rgio Andaluz Ramos
 *
 */
public final class ModConstants {

	// All Shared Preferences Keys
	public static final String IS_LOGIN = "IsLoggedIn";

	// User ID
	public static final String KEY_ID = "id";

	public static final String KEY_JSONFORMAT = "&moodlewsrestformat=json";

	public static final String KEY_N_PARAMS = "%s/webservice/rest/server.php?wstoken=%s&wsfunction=%s";

	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";

	public static final String KEY_FULL_NAME = "fullName";

	public static final String KEY_PARAMS = "%s/webservice/rest/server.php?wstoken=%s&wsfunction=%s=%s";

	// Email address (make variable public to access from outside)
	public static final String KEY_TOKEN = "token";

	// Moodle URL
	public static final String KEY_URL = "url";

	// Sharedpref file name
	public static final String PREF_NAME = "MoodyPreferences";

	/**
	 *
	 * Atributos Constants da Sess�o do Moody.
	 *
	 **/

	// Shared pref mode
	public static final int PRIVATE_MODE = 0;

	// Check if user has picture! it's a workaround because moodle bug
	public static final int DIALOG_FRAG_USER_PIC = 0;

	// PlayStore constants
	public static final String DIALOG_FRAG_USER_CLOUD_DROPBOX = "com.dropbox.android";
	public static final String DIALOG_FRAG_USER_CLOUD_DRIVE = "com.google.android.apps.docs";

	// ADMob constants
	public static final String MY_ADMOB_UNIT_ID = "ca-app-pub-6892180394020125/7421079096";

	// ADMob test devices id
	public static final String ADS_TEST_DEVICE_ID = "D1FB3FA9A4D6CDC5CBA3C5616D291323";

	// Test mode to connect to test server
	public static final String TEST_MODE_USER = "student";

	public static final String TEST_MODE_PASSWORD = "student";

	public static final String TEST_MODE_URL = "193.137.46.10/default_site/Moody";

	/**
	 *
	 * - Construtor privado - Assegura que classe n�o � instanciavel.
	 *
	 **/
	private ModConstants() {
	}

}
