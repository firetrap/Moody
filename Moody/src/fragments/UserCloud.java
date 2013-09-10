/**
 * 
 */
package fragments;

import com.example.moody.R;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author MoodyProject Team
 *
 */
public class UserCloud extends DialogFragment{

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().setLayout(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);

		final View view = inflater.inflate(R.layout.cloud_dialog, container);
		
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}
}
