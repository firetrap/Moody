package model;

/**
 * 
 * MoodyConstants
 * 	- Classe que contém todos os valores constants da aplicação.
 * 
 * @author Sérgio Andaluz Ramos
 *
 */
public final class MoodyConstants {

	public final class MoodySession {
		
		/**
		 * 
		 * Atributos Constants da Sessão do Moody.
		 * 
		 **/
		
	    public static final int 	PRIVATE_SESSION_MODE 	= 0;
	    public static final String 	KEY_MOODY_PREF_NAME 	= "MoodySessionPref";
	    public static final String 	KEY_IS_LOGGEDIN 		= "IsLoggedIn";
	    public static final String 	KEY_MOODY_USER_TOKEN 	= "MoodyUserToken";		
		
	    
		/**
		 * 
		 * - Construtor privado - Assegura que classe não é instanciavel.
		 * 
		 **/
		private MoodySession() { }	
		
	}
	
	/**
	 * 
	 * - Construtor privado - Assegura que classe não é instanciavel.
	 * 
	 **/
	private MoodyConstants() { }

}
