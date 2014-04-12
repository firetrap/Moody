package connections;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import activities.MainActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.CountDownTimer;

public class GET_USER_PICTURE_FROM_URL extends AsyncTask<Object, Void, Drawable> {
	private ProgressDialog	dialog;
	private CountDownTimer	cvt	= createCountDownTimer();
	private Activity		activity;

	public GET_USER_PICTURE_FROM_URL(Activity activity) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		cvt.start();
	}

	@Override
	protected Drawable doInBackground(Object... params) {
		Drawable drawable = null;
		String imageWebAddress = (String) params[0];
		InputStream inputStream;
		// try {
		// Thread.sleep(10000);// For tests porpuses
		// } catch (InterruptedException e1) {
		// // TODO Auto-generated catch block
		// e1.printStackTrace();
		// }
		try {
			inputStream = new URL(imageWebAddress).openStream();
			drawable = Drawable.createFromStream(inputStream, null);
			inputStream.close();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return drawable;
	}

	@Override
	protected void onPostExecute(Drawable result) {
		super.onPostExecute(result);
		cvt.cancel();
		if (dialog != null)
			dialog.dismiss();
		// ((MainActivity) activity).userPictureAsyncTaskResult(result);
	}

	private CountDownTimer createCountDownTimer() {
		return new CountDownTimer(250, 10) {
			@Override
			public void onTick(long millisUntilFinished) {

			}

			@Override
			public void onFinish() {
				dialog = new ProgressDialog(activity);
				dialog.setMessage("Loading...");
				dialog.setCancelable(false);
				dialog.setCanceledOnTouchOutside(false);
				dialog.show();
			}
		};
	}

}
