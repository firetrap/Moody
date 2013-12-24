/**
 * 
 */
package managers;

import model.ModMessage;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.firetrap.moody.R;

/**
 * 
 * AlertDialogMAnager it's to notify if Gdrive or dropbox are not installed. If
 * not installed asks if you want to download it from Play Store.
 * 
 * @author SérgioFilipe
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
	 * Método showMessageDialog - For error messages
	 * 
	 * @param context
	 * @param mensagem
	 * @param status
	 * 
	 **/
	public static void showMessageDialog(Activity context, ModMessage mensagem,
			Boolean status) {

		showMessageDialog(context, mensagem,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						getAlertDialog().dismiss();

					}

				}, status);
	}

	/**
	 * @param context
	 * @param mensagem
	 * @param onClick
	 * @param status
	 */
	public static void showMessageDialog(Activity context, ModMessage mensagem,
			DialogInterface.OnClickListener onClick, Boolean status) {

		initBuilder(context, mensagem, status);

		getAlertDialog().setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.moody_ok), onClick);

		getAlertDialog().show();

	}

	/**
	 * @param context
	 * @param mensagem
	 * @param frtOnClick
	 * @param sndOnClick
	 * @param status
	 */
	public static void showMessageDialog(Activity context, ModMessage mensagem,
			DialogInterface.OnClickListener frtOnClick,
			DialogInterface.OnClickListener sndOnClick, Boolean status) {

		initBuilder(context, mensagem, status);

		getAlertDialog().setButton(DialogInterface.BUTTON_POSITIVE,
				context.getString(R.string.moody_yes), frtOnClick);

		getAlertDialog().setButton(DialogInterface.BUTTON_NEGATIVE,
				context.getString(R.string.moody_no), sndOnClick);

		getAlertDialog().show();

	}

	/**
	 * @param context
	 * @param mensagem
	 * @param status
	 */
	private static void initBuilder(Activity context, ModMessage mensagem,
			Boolean status) {

		builder = new AlertDialog.Builder(context);
		getBuilder().setTitle(mensagem.getAssunto());
		getBuilder().setMessage(mensagem.getCorpo());
		alertDialog = getBuilder().create();
	}

	private ManAlertDialog() {
		throw new AssertionError();
	}

}
