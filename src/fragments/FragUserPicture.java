package fragments;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.firetrap.moody.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import interfaces.UserPictureDialogInterface;
import managers.ManAlertDialog;
import model.ModConstants;
import model.ModMessage;

/**
 * @author firetrap
 * 
 */
public class FragUserPicture extends DialogFragment {

	// FOR GALLERY
	private static final int PICTURE_GALLERY = 1;

	// FOR CAMERA
	private static final int REQUEST_IMAGE = 2;

	String cameraImagePath;

	File destination;
	private String fileManagerImagePath;
	private String selectedImagePath;

	private String getPath(Uri uri) {
		String selectedImagePath;
		// 1:MEDIA GALLERY --- query from MediaStore.Images.Media.DATA
		final String[] projection = { MediaColumns.DATA };
		final Cursor cursor = getActivity().getContentResolver().query(uri,
				projection, null, null, null);
		if (cursor != null) {
			final int column_index = cursor
					.getColumnIndexOrThrow(MediaColumns.DATA);
			cursor.moveToFirst();
			selectedImagePath = cursor.getString(column_index);
		} else {
			selectedImagePath = null;
		}

		if (selectedImagePath == null) {
			// 2:OI FILE Manager --- call method: uri.getPath()
			selectedImagePath = uri.getPath();
		}
		return selectedImagePath;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		getActivity();
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == PICTURE_GALLERY) {
				final Uri selectedImageUri = data.getData();

				// OI FILE Manager
				fileManagerImagePath = selectedImageUri.getPath();

				// MEDIA GALLERY
				selectedImagePath = getPath(selectedImageUri);

				final UserPictureDialogInterface activity = (UserPictureDialogInterface) getActivity();
				activity.onFinishEditDialog(selectedImagePath,
						ModConstants.DIALOG_FRAG_USER_PIC);
				this.dismiss();
			}
			if (requestCode == REQUEST_IMAGE) {

				try {
					new FileInputStream(destination);
				} catch (final FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 10;
				cameraImagePath = destination.getAbsolutePath();

				final UserPictureDialogInterface activity = (UserPictureDialogInterface) getActivity();
				activity.onFinishEditDialog(cameraImagePath,
						ModConstants.DIALOG_FRAG_USER_PIC);
				this.dismiss();
			}
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		getDialog().getWindow().setLayout(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		getDialog().getWindow().requestFeature(STYLE_NO_TITLE);
		// getDialog().setTitle("User details");

		final View view = inflater.inflate(R.layout.dialog_user_picture, container);
		final File folder = new File(Environment.getExternalStorageDirectory()
				+ "/.moody");
		final String name = "thumbnail";

		// If ./moody doesn't exists create it
		boolean success = true;
		if (!folder.exists()) {
			success = folder.mkdir();
		}

		if (success) {
			destination = new File(folder, name + ".jpg");
		} else {
			dismiss();
			ManAlertDialog.showMessageDialog(getActivity(), new ModMessage(
					"Login Error",
					"Sdcard needed to store picture but not available"), false);
		}

		((Button) view.findViewById(R.id.upload_from_gallery_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final Intent intent = new Intent();
						intent.setType("image/*");
						intent.setAction(Intent.ACTION_GET_CONTENT);
						startActivityForResult(
								Intent.createChooser(intent, "Select Picture"),
								PICTURE_GALLERY);
					}
				});

		((Button) view.findViewById(R.id.upload_from_camera_button))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						final Intent intent = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						intent.putExtra(MediaStore.EXTRA_OUTPUT,
								Uri.fromFile(destination));
						startActivityForResult(intent, REQUEST_IMAGE);
					}
				});

		return view;
	}

}
