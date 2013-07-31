package model;

/**
 * 
 * MoodyConstants
 * 	- Classe que cont�m todos os valores constants da aplica��o.
 * 
 * @author S�rgio Andaluz Ramos
 *
 */
public final class MoodyConstants {

	public final class MoodySession {
		
		/**
		 * 
		 * Atributos Constants da Sess�o do Moody.
		 * 
		 **/
		
	    public static final int 	PRIVATE_SESSION_MODE 	= 0;
	    public static final String 	KEY_MOODY_PREF_NAME 	= "MoodySessionPref";
	    public static final String 	KEY_IS_LOGGEDIN 		= "IsLoggedIn";
	    public static final String 	KEY_MOODY_USER_TOKEN 	= "MoodyUserToken";		
		
	    
		/**
		 * 
		 * - Construtor privado - Assegura que classe n�o � instanciavel.
		 * 
		 **/
		private MoodySession() { }	
		
	}
	
	/**
	 * 
	 * - Construtor privado - Assegura que classe n�o � instanciavel.
	 * 
	 **/
	private MoodyConstants() { }

}
