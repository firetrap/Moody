/**
 * 
 */
package fragments;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.moody.R;

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
		
		((Button) view.findViewById(R.id.cloud_dropbox_btn))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getBaseContext(), "dropbox",
						Toast.LENGTH_SHORT).show();

			}
		});
		
		((Button) view.findViewById(R.id.cloud_drive_btn))
		.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Toast.makeText(getActivity().getBaseContext(), "drive",
						Toast.LENGTH_SHORT).show();
				
			}
		});
		
		return view;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		
	}
}
