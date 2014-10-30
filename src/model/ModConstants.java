package model;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

/**
 * 
 * ModConstants - Classe que contém todos os valores constants da aplicação.
 * 
 * @author Sérgio Andaluz Ramos
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
	 * Atributos Constants da Sessão do Moody.
	 * 
	 **/

	// Shared pref mode
	public static final int PRIVATE_MODE = 0;

	// Check if user has picture! it's a workaround because moodle bug
	public static final int DIALOG_FRAG_USER_PIC = 0;

	// PlayStore constants
	public static final String DIALOG_FRAG_USER_CLOUD_DROPBOX = "com.dropbox.android";
	public static final String DIALOG_FRAG_USER_CLOUD_DRIVE = "com.google.android.apps.docs";

	/**
	 * 
	 * - Construtor privado - Assegura que classe não é instanciavel.
	 * 
	 **/
	private ModConstants() {
	}

}
