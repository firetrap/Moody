package connections;

import java.io.UnsupportedEncodingException;

import fragments.FragFavoritesPreview;
import fragments.FragLatest;

import managers.ManDataStore;
import managers.ManSession;
import model.ModConstants;

import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourse;
import restPackage.MoodleRestEnrol;
import restPackage.MoodleRestException;
import restPackage.MoodleServices;
import activities.MainActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.CountDownTimer;

public class CORE_ENROL_GET_USERS_COURSES extends AsyncTask<Object, Void, Object> {
	private ProgressDialog	dialog;
	private CountDownTimer	cvt	= createCountDownTimer();
	ManDataStore			data;
	private ManSession		session;
	private String			url;
	private String			token;
	private long			userId;
	private Activity		activity;
	private Fragment		fragment;

	public CORE_ENROL_GET_USERS_COURSES(Activity activity) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
		this.data = new ManDataStore(activity);
		session = new ManSession(activity);
	}

	public CORE_ENROL_GET_USERS_COURSES(Fragment fragment) {
		this.fragment = fragment;
		activity = fragment.getActivity();
		dialog = new ProgressDialog(activity);
		this.data = new ManDataStore(activity);
		session = new ManSession(activity);
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();

		cvt.start();
	}

	@Override
	protected Object doInBackground(Object... params) {
		url = session.getValues(ModConstants.KEY_URL, null);
		token = session.getValues(ModConstants.KEY_TOKEN, null);
		userId = Long.parseLong(session.getValues(ModConstants.KEY_ID, null));

		MoodleCallRestWebService.init(url + "/webservice/rest/server.php", token);

		MoodleCourse[] courses = null;
		try {
			courses = MoodleRestEnrol.getUsersCourses(userId);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MoodleRestException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return courses;

	}

	@Override
	protected void onPostExecute(Object obj) {
		cvt.cancel();
		if (dialog != null)
			dialog.dismiss();

		String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES.name() + String.valueOf(userId);

		if (obj != null)
			data.storeData(obj, fileName);

		// if (fragment != null) {
		// if (fragment instanceof FragFavoritesPreview)
		// ((FragFavoritesPreview) fragment).userCoursesAsyncTaskResult(obj);
		//
		// if (fragment instanceof FragLatest)
		// ((FragLatest) fragment).userCoursesAsyncTaskResult(obj);
		//
		// } else {
		// if (activity instanceof MainActivity)
		// ((MainActivity) activity).usersCoursesAsyncTaskResult(obj);
		// }
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
