package model;

/**
 * 
 * MoodyMessage
 * 	- Classe abstrata, m�e das mensagens do Moody.
 * 
 * @author MoodyProject Team
 *
 */
public class MoodyMessage {


	/**
	 * 
	 * Atributos do Objecto Messagem.
	 * 
	 **/

	private String 	assunto;
	private String 	corpo;


	/**
	 * 
	 * Gets dos atributos.
	 * 
	 **/

	public 	String 	getAssunto() 					{ return assunto; }
	public 	String 	getCorpo() 						{ return corpo; }

	/**
	 * 
	 * Sets dos atributos.
	 * 
	 **/

	public 	void 	setAssunto(String assunto) 		{ this.assunto = assunto; }
	public 	void 	setCorpo(String corpo) 			{ this.corpo = corpo; }


	/**
	 * 
	 * Construtor P�blico com 2 par�metros.
	 * 
	 * @param assunto 	- Assunto da Mensagem.
	 * @param mensagem  - Conte�do da mensagem.
	 * 
	 **/
	public MoodyMessage(String assunto, String corpo) { 

		initiateAttributes(assunto, corpo); 

	}


	/**
	 * 
	 * Construtor P�blico com 1 par�metros.
	 * 
	 * @param mensagem  - Conte�do da mensagem.
	 *  
	 **/
	public MoodyMessage(String corpo) { 

		initiateAttributes("Moody Moodle App", corpo); 

	}


	/**
	 * 
	 * M�todo que inicializa os atributos da mensagem.
	 * 
	 * @param assunto 	- Assunto da Mensagem.
	 * @param mensagem  - Conte�do da mensagem.
	 * 
	 **/
	public void initiateAttributes(String assunto, String corpo){

		setAssunto(assunto);
		setCorpo(corpo);

	}
}
