package model;

/**
 * 
 * MoodyConstants - Classe que cont�m todos os valores constants da aplica��o.
 * 
 * @author S�rgio Andaluz Ramos
 * 
 */
public final class MoodyConstants {

	/**
	 * 
	 * - Construtor privado - Assegura que classe n�o � instanciavel.
	 * 
	 **/
	private MoodyConstants() {
	}

	public final class MoodySession {

		/**
		 * 
		 * Atributos Constants da Sess�o do Moody.
		 * 
		 **/

		// Shared pref mode
		public static final int PRIVATE_MODE = 0;

		// Sharedpref file name
		public static final String PREF_NAME = "MoodyPreferences";

		// All Shared Preferences Keys
		public static final String IS_LOGIN = "IsLoggedIn";

		// User name (make variable public to access from outside)
		public static final String KEY_NAME = "name";

		// Email address (make variable public to access from outside)
		public static final String KEY_TOKEN = "token";

		// User ID
		public static final String KEY_ID = "id";

		// Moodle URL
		public static final String KEY_URL = "url";

		public static final String KEY_N_PARAMS = "%s/webservice/rest/server.php?wstoken=%s&wsfunction=%s";

		public static final String KEY_PARAMS = "%s/webservice/rest/server.php?wstoken=%s&wsfunction=%s=%s";

		/**
		 * 
		 * - Construtor privado - Assegura que classe n�o � instanciavel.
		 * 
		 **/
		private MoodySession() {
		}

	}

	public final class ActivityCode {

		public static final int DIALOG_FRAG_USER_PIC = 0;

		/**
		 * 
		 * - Construtor privado - Assegura que classe n�o � instanciavel.
		 * 
		 **/
		private ActivityCode() {
		}

	}

}
