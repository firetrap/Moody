/**
 * 
 */
package com.example.MoodyNotifications;

/**
 * @author Sérgio Andaluz Ramos
 *
 */
public class NotificationMessage {

	//Atributos do Objecto Messagem
	private String tkr;
	private String ttl;
	private String msg;
	
	/**
	 * 
	 */
	public NotificationMessage(String tkr, String ttl, String msg) {
		// TODO Auto-generated constructor stub
		setTkr(tkr);
		setTtl(ttl);
		setMsg(msg);
	}
	
	/**
	 * 
	 */
	public NotificationMessage(String ttl, String msg) {
		// TODO Auto-generated constructor stub
		setTkr("Moody");
		setTtl(ttl);
		setMsg(msg);
	}

	//Getters
	public String getTkr() { return tkr; }
	public String getTtl() { return ttl; }
	public String getMsg() { return msg; }
	
	//Setters
	private void setTkr(String tkr) { this.tkr = tkr; }
	private void setTtl(String ttl) { this.ttl = ttl; }
	private void setMsg(String msg) { this.msg = msg; }

}
