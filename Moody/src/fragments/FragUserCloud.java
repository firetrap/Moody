/**
 *
 */
package fragments;

import managers.ManAlertDialog;
import model.ModConstants;
import model.ModMessage;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.firetrap.moody.R;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. F�bio Barreiros - Moody Founder
 */

/**
 * @author S�rgioFilipe
 *
 */
public class FragUserCloud extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().setLayout(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

		final View view = inflater.inflate(R.layout.dialog_cloud_apps,
				container);

		((Button) view.findViewById(R.id.cloud_dropbox_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						processCloud(ModConstants.DIALOG_FRAG_USER_CLOUD_DROPBOX);

					}
				});

		((Button) view.findViewById(R.id.cloud_drive_btn))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						processCloud(ModConstants.DIALOG_FRAG_USER_CLOUD_DRIVE);

					}
				});

		return view;
	}

	private void processCloud(final String appName) {

		if (isInstalled(appName))
			// This intent will help you to launch if the package is already
			// installed
			startActivity(getActivity().getBaseContext().getPackageManager()
					.getLaunchIntentForPackage(appName));

		else {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:

						// Se tem google play chama, sen�o chama o browser.
						try {

							callOnline("market://details?id=" + appName);

						} catch (Exception e) {

							// google play app is not installed
							callOnline("https://play.google.com/store/apps/details?id="
									+ appName);

						}

						break;

					case DialogInterface.BUTTON_NEGATIVE:

						dialog.dismiss();

						break;
					}
				}

			};

			ManAlertDialog.showMessageDialog(getActivity(), new ModMessage(
					"Get It Now!",
					"You dont have this Cloud Service Installed. Go online?"),
					dialogClickListener, dialogClickListener, false);

		}

	}

	/**
	 * Call the browser or the play store if installed
	 *
	 * @param uri
	 */
	private void callOnline(String uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		startActivity(intent);
	}

	/**
	 *
	 * Check if the cloud service has been chosen from the user is installed.
	 *
	 * @param uri
	 * @return
	 */
	private boolean isInstalled(String uri) {

		try {
			getActivity().getBaseContext().getPackageManager()
					.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			return true;
		} catch (Exception e) {
			return false;
		}

	}
}
