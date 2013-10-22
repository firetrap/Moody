/**
 * 
 */
package fragments;

import managers.ManAlertDialog;
import managers.ManUserContacts;
import model.ModMessage;
import restPackage.MoodleContact;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.moody.R;

/**
 * @author Sfbramos
 * 
 */
public class FragUserContactMessage extends DialogFragment {
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().setLayout(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		getDialog().getWindow().setTitle(
				getActivity().getBaseContext().getResources()
						.getString(R.string.message_title));

		view = inflater.inflate(R.layout.frag_message, container);

		((Button) view.findViewById(R.id.button_cancel))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						dismiss();
					}
				});

		((Button) view.findViewById(R.id.button_send))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						String message = ((EditText) view
								.findViewById(R.id.edittext_message)).getText()
								.toString();

						try {

							new ManUserContacts(getActivity().getBaseContext())
									.sendMessage(message, getArguments()
											.getLong("contact"));

							MoodleContact contact = null;

							if (new ManUserContacts(getActivity()
									.getBaseContext()).isBlocked(getArguments()
									.getLong("contact")))
								contact = new ManUserContacts(getActivity()
										.getBaseContext())
										.getBlockedContact(getArguments()
												.getLong("contact"));
							else
								contact = new ManUserContacts(getActivity()
										.getBaseContext())
										.getContact((getArguments()
												.getLong("contact")));

							ManAlertDialog
									.showMessageDialog(
											getActivity(),
											new ModMessage(
													"Done!",
													"Your message was sent to "
															+ contact
																	.getContactProfile()
																	.getFullname()
															+ "!"), false);

						} catch (Exception e) {

							ManAlertDialog.showMessageDialog(getActivity(),
									new ModMessage("Error",
											"Something Went Wrong!"), false);
							e.printStackTrace();
						}

						dismiss();
					}
				});

		return view;
	}
}
