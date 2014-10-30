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
 * ModMessage - Classe abstrata, m�e das mensagens do Moody.
 * 
 * @author S�rgioFilipe
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
	 * Construtor P�blico com 1 par�metros.
	 * 
	 * @param mensagem
	 *            - Conte�do da mensagem.
	 * 
	 **/
	public ModMessage(String corpo) {

		initiateAttributes("Moody Moodle App", corpo);

	}

	/**
	 * 
	 * Construtor P�blico com 2 par�metros.
	 * 
	 * @param assunto
	 *            - Assunto da Mensagem.
	 * @param mensagem
	 *            - Conte�do da mensagem.
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
	 * M�todo que inicializa os atributos da mensagem.
	 * 
	 * @param assunto
	 *            - Assunto da Mensagem.
	 * @param mensagem
	 *            - Conte�do da mensagem.
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
