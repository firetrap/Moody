package fragments;

import it.gmariotti.changelibs.library.view.ChangeLogListView;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;

import com.firetrap.moody.R;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

public class FragChangeLog extends DialogFragment {

	public FragChangeLog() {
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater layoutInflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ChangeLogListView chgList = (ChangeLogListView) layoutInflater.inflate(
				R.layout.frag_change_log, null);

		return new AlertDialog.Builder(getActivity())
				.setTitle(R.string.demo_changelog_title)
				.setView(chgList)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).create();

	}
}
