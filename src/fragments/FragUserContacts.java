package fragments;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.firetrap.moody.R;

import managers.ManAlertDialog;
import managers.ManUserContacts;
import model.ModMessage;
import restPackage.MoodleContact;
import restPackage.MoodleContactAction;

/**
 * @author S�rgioFilipe
 * 
 */
public class FragUserContacts extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		MoodleContact contact;

		getDialog().getWindow().setLayout(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);

		View view = inflater.inflate(R.layout.dialog_contacts_context,
				container);

		contact = new ManUserContacts(getActivity().getBaseContext())
				.getContact(getArguments().getLong("contact"));

		if (contact != null) {
			// its a normal contact
			((Button) view.findViewById(R.id.contacts_unblock_btn))
					.setVisibility(View.GONE);

			((Button) view.findViewById(R.id.contacts_block_btn))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							try {

								new ManUserContacts(getActivity()
										.getBaseContext())
										.blockContact(getArguments().getLong(
												"contact"));

								showResult(MoodleContactAction.BLOCK);

							} catch (Exception e) {

								ManAlertDialog
										.showMessageDialog(
												getActivity(),
												new ModMessage("Error",
														"Something Went Wrong!"),
												false);
								e.printStackTrace();
							}

							dismiss();
						}
					});
		} else {
			// its blocked so hide option acordingly
			contact = new ManUserContacts(getActivity().getBaseContext())
					.getBlockedContact(getArguments().getLong("contact"));

			((Button) view.findViewById(R.id.contacts_block_btn))
					.setVisibility(View.GONE);

			((Button) view.findViewById(R.id.contacts_unblock_btn))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							try {

								new ManUserContacts(getActivity()
										.getBaseContext())
										.unblockContact(getArguments().getLong(
												"contact"));

								showResult(MoodleContactAction.UNBLOCK);

							} catch (Exception e) {

								ManAlertDialog
										.showMessageDialog(
												getActivity(),
												new ModMessage("Error",
														"Something Went Wrong!"),
												false);
								e.printStackTrace();
							}

							dismiss();
						}
					});
		}

		if (!new ManUserContacts(getActivity().getBaseContext())
				.isStranger(contact.getContactProfile().getId())) {
			((Button) view.findViewById(R.id.contacts_create_contact_btn))
					.setVisibility(View.GONE);
		} else {
			((Button) view.findViewById(R.id.contacts_create_contact_btn))
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							try {

								new ManUserContacts(getActivity()
										.getBaseContext())
										.createContact(getArguments().getLong(
												"contact"));

								showResult(MoodleContactAction.CREATE);

							} catch (Exception e) {

								ManAlertDialog
										.showMessageDialog(
												getActivity(),
												new ModMessage("Error",
														"Something Went Wrong!"),
												false);
								e.printStackTrace();
							}

							dismiss();
						}
					});
		}

		if (new ManUserContacts(getActivity().getBaseContext())
				.isStranger(getArguments().getLong("contact"))
				|| new ManUserContacts(getActivity().getBaseContext())
						.isBlocked(getArguments().getLong("contact"))) {
			((Button) view.findViewById(R.id.contacts_delete_btn))
					.setText(getResources().getString(R.string.contacts_delete));
		}

		((Button) view.findViewById(R.id.contacts_delete_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						try {

							new ManUserContacts(getActivity().getBaseContext())
									.deleteContact(getArguments().getLong(
											"contact"));

							showResult(MoodleContactAction.DELETE);

						} catch (Exception e) {

							ManAlertDialog.showMessageDialog(getActivity(),
									new ModMessage("Error",
											"Something Went Wrong!"), false);
							e.printStackTrace();
						}

						dismiss();
					}
				});

		((Button) view.findViewById(R.id.contacts_send_message_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dismiss();
						FragmentManager fm = getActivity().getFragmentManager();
						FragUserContactMessage userContactContextDialog = null;
						Bundle bund = new Bundle();

						bund.putLong("contact",
								getArguments().getLong("contact"));
						bund.putString("name", getArguments().getString("name"));
						// make debug easy
						try {
							userContactContextDialog = new FragUserContactMessage();
						} catch (Exception ex) {

						}

						userContactContextDialog.setArguments(bund);
						userContactContextDialog.setRetainInstance(true);
						userContactContextDialog.show(fm, "fragment_name");
					}
				});

		getDialog().setTitle(contact.getContactProfile().getFullname());

		return view;
	}

	private void showResult(MoodleContactAction action) {
		MoodleContact contact = null;

		String actionName = "";

		if (new ManUserContacts(getActivity().getBaseContext())
				.isBlocked(getArguments().getLong("contact")))
			contact = new ManUserContacts(getActivity().getBaseContext())
					.getBlockedContact(getArguments().getLong("contact"));
		else
			contact = new ManUserContacts(getActivity().getBaseContext())
					.getContact((getArguments().getLong("contact")));

		switch (action) {
		case BLOCK:
			actionName = "blocked";
			break;
		case CREATE:
			actionName = "added to contacts";
			break;
		case DELETE:

			actionName = (new ManUserContacts(getActivity().getBaseContext())
					.isBlocked(getArguments().getLong("contact")) || new ManUserContacts(
					getActivity().getBaseContext()).isStranger(getArguments()
					.getLong("contact"))) ? "removed" : "removed from contacts";

			break;
		case UNBLOCK:
			actionName = "unblocked";
			break;
		}

		ManAlertDialog.showMessageDialog(getActivity(), new ModMessage("Done!",
				contact.getContactProfile().getFullname() + " was "
						+ actionName + "!"), false);
	}
}
