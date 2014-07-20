package fragments;

import it.gmariotti.changelibs.library.view.ChangeLogListView;

import com.firetrap.moody.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				}).create();

	}
}
