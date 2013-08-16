package managers;

import android.app.ActionBar.LayoutParams;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.Activity;

import com.example.moody.R;

public class DialogFragmentManager extends DialogFragment {

	private static final int SELECT_PICTURE = 1;

	private String selectedImagePath;

	private String filemanagerstring;

	// Context context;
	// public DialogFragmentManager(Context applicationContext) {
	// context = applicationContext;
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().setLayout(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
		// getDialog().setTitle("User details");

		View view = inflater.inflate(R.layout.user_details_dialog, container);

//		((Button) view.findViewById(R.id.change_user_picture_button))
//				.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Intent intent = new Intent();
//						intent.setType("image/*");
//						intent.setAction(Intent.ACTION_GET_CONTENT);
//						startActivityForResult(
//								Intent.createChooser(intent, "Select Picture"),
//								SELECT_PICTURE);
//
//					}
//				});

		return view;
	}

	

	
	
	
//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		if (resultCode == RESULT_OK) {
//			if (requestCode == SELECT_PICTURE) {
//				Uri selectedImageUri = data.getData();
//
//				// OI FILE Manager
//				filemanagerstring = selectedImageUri.getPath();
//
//				// MEDIA GALLERY
//				selectedImagePath = getPath(selectedImageUri);
//
//				// DEBUG PURPOSE - you can delete this if you want
//				if (selectedImagePath != null)
//					System.out.println(selectedImagePath);
//				else
//					System.out.println("selectedImagePath is null");
//				if (filemanagerstring != null)
//					System.out.println(filemanagerstring);
//				else
//					System.out.println("filemanagerstring is null");
//
//				// NOW WE HAVE OUR WANTED STRING
//				if (selectedImagePath != null)
//					System.out
//							.println("selectedImagePath is the right one for you!");
//				else
//					System.out
//							.println("filemanagerstring is the right one for you!");
//			}
//		}
//	}
//
//	// UPDATED!
//	public String getPath(Uri uri) {
//		String res = null;
//		String[] proj = { MediaStore.Images.Media.DATA };
//		Cursor cursor = getActivity().getContentResolver().query(uri, proj,
//				null, null, null);
//		if (cursor.moveToFirst()) {
//			;
//			int column_index = cursor
//					.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//			res = cursor.getString(column_index);
//		}
//		cursor.close();
//		return res;
//	}
}
