package managers;

import android.app.ActionBar.LayoutParams;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.moody.R;

public class DialogFragmentManager extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
		// getDialog().setTitle("User details");
		View view = inflater.inflate(R.layout.user_details_dialog, container);
		return view;
	}

}
