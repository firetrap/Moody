package model;

/**
 * 
 * ModMessage - Classe abstrata, mãe das mensagens do Moody.
 * 
 * @author MoodyProject Team
 * 
 */
public class ModMessage {

	/**
	 * 
	 * Atributos do Objecto Messagem.
	 * 
	 **/

	private String assunto;
	private String corpo;

	/**
	 * 
	 * Construtor Público com 1 parâmetros.
	 * 
	 * @param mensagem
	 *            - Conteúdo da mensagem.
	 * 
	 **/
	public ModMessage(String corpo) {

		initiateAttributes("Moody Moodle App", corpo);

	}

	/**
	 * 
	 * Construtor Público com 2 parâmetros.
	 * 
	 * @param assunto
	 *            - Assunto da Mensagem.
	 * @param mensagem
	 *            - Conteúdo da mensagem.
	 * 
	 **/
	public ModMessage(String assunto, String corpo) {

		initiateAttributes(assunto, corpo);

	}

	/**
	 * 
	 * Gets dos atributos.
	 * 
	 **/

	public String getAssunto() {
		return assunto;
	}

	public String getCorpo() {
		return corpo;
	}

	/**
	 * 
	 * Método que inicializa os atributos da mensagem.
	 * 
	 * @param assunto
	 *            - Assunto da Mensagem.
	 * @param mensagem
	 *            - Conteúdo da mensagem.
	 * 
	 **/
	public void initiateAttributes(String assunto, String corpo) {

		setAssunto(assunto);
		setCorpo(corpo);

	}

	/**
	 * 
	 * Sets dos atributos.
	 * 
	 **/

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public void setCorpo(String corpo) {
		this.corpo = corpo;
	}
}
