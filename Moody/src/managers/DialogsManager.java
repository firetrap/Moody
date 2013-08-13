/**
 * 
 */
package managers;

import model.MoodyMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.moody.R;

/**
 * 
 * AlertDialogMAnager servirá para notificar caso não a dropbox ou drive. Caso
 * não esteja instalado pergunta se quer ir sacar e vai para o menu de decisão
 * de abertura. - Classe que será responsável por
 * 
 * @author Sérgio Andaluz Ramos
 * 
 */
public final class DialogsManager {

	/**
	 * 
	 * Atributos do Dialog.
	 * 
	 **/
	private static AlertDialog alertDialog;
	private static AlertDialog.Builder builder;

	/**
	 * 
	 * Gets dos atributos.
	 * 
	 **/
	private static AlertDialog getAlertDialog() {
		return alertDialog;
	}

	private static AlertDialog.Builder getBuilder() {
		return builder;
	}

	/**
	 * 
	 * Construtor privado - Assegura que classe não é instanciavel.
	 * 
	 **/
	private DialogsManager() {
		throw new AssertionError();
	}

	/**
	 * 
	 * Método showMessageDialog - Para mensagens de erro, sucesso ou simples
	 * Debugging
	 * 
	 * @param context
	 *            - Contexto actual da aplicação (estado da Actividade actual)
	 *            (Activity).
	 * @param mensagem
	 *            - Mensagem do Dialog
	 * @param status
	 *            - Flag para definir icon da janela (true -> success, false ->
	 *            error, null -> sem Icon)
	 * 
	 **/
	public static void showMessageDialog(Activity context,
			MoodyMessage mensagem, Boolean status) {

		builder = new AlertDialog.Builder(context);

		getBuilder().setTitle(mensagem.getAssunto());
		getBuilder().setMessage(mensagem.getCorpo());

		// ################################ ADAPTAR PARA USAR OS ICONS DA
		// APLICAÇÃO!!!! ############################
		// if(status != null)
		// getAlertDialog().setIcon((status) ? R.drawable.success :
		// R.drawable.fail);
		// #########################################################################################################

		alertDialog = getBuilder().create();

		getAlertDialog().setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.moodyButtonOk),
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {

						getAlertDialog().dismiss();

					}

				});

		getAlertDialog().show();

	}

}
