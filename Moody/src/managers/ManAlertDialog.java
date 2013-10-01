/**
 * 
 */
package managers;

import model.ModMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.example.moody.R;

/**
 * 
 * AlertDialogMAnager servir� para notificar caso n�o a dropbox ou drive. Caso
 * n�o esteja instalado pergunta se quer ir sacar e vai para o menu de decis�o
 * de abertura. - Classe que ser� respons�vel por
 * 
 * @author S�rgio Andaluz Ramos
 * 
 */
public final class ManAlertDialog {

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
	 * M�todo showMessageDialog - Para mensagens de erro, sucesso ou simples
	 * Debugging
	 * 
	 * @param context
	 *            - Contexto actual da aplica��o (estado da Actividade actual)
	 *            (Activity).
	 * @param mensagem
	 *            - Mensagem do Dialog
	 * @param status
	 *            - Flag para definir icon da janela (true -> success, false ->
	 *            error, null -> sem Icon)
	 * 
	 **/
	public static void showMessageDialog(Activity context,
			ModMessage mensagem, Boolean status) {

		showMessageDialog(context, mensagem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						getAlertDialog().dismiss();

					}

				}, status);
	}

	public static void showMessageDialog(Activity context,
			ModMessage mensagem, DialogInterface.OnClickListener onClick,
			Boolean status) {

		initBuilder(context, mensagem, status);

		getAlertDialog().setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.moody_ok), onClick);

		getAlertDialog().show();

	}

	public static void showMessageDialog(Activity context,
			ModMessage mensagem, DialogInterface.OnClickListener frtOnClick,
			DialogInterface.OnClickListener sndOnClick, Boolean status) {

		initBuilder(context, mensagem, status);

		getAlertDialog().setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.moody_yes), frtOnClick);

		getAlertDialog().setButton(DialogInterface.BUTTON_NEGATIVE,
				context.getString(R.string.moody_no), sndOnClick);

		getAlertDialog().show();

	}

	private static void initBuilder(Activity context, ModMessage mensagem,
			Boolean status) {

		builder = new AlertDialog.Builder(context);

		getBuilder().setTitle(mensagem.getAssunto());
		getBuilder().setMessage(mensagem.getCorpo());

		// ################################ ADAPTAR PARA USAR OS ICONS DA
		// APLICA��O!!!! ############################
		// if(status != null)
		// getAlertDialog().setIcon((status) ? R.drawable.success :
		// R.drawable.fail);
		// #########################################################################################################

		alertDialog = getBuilder().create();

	}

	/**
	 * 
	 * Construtor privado - Assegura que classe n�o � instanciavel.
	 * 
	 **/
	private ManAlertDialog() {
		throw new AssertionError();
	}

}
