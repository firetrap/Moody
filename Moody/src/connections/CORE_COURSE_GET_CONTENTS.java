package connections;

import java.io.UnsupportedEncodingException;

import managers.ManDataStore;
import managers.ManSession;
import model.ModConstants;
import restPackage.MoodleCallRestWebService;
import restPackage.MoodleCourseContent;
import restPackage.MoodleRestCourse;
import restPackage.MoodleRestCourseException;
import restPackage.MoodleRestException;
import restPackage.MoodleServices;
import activities.MainActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import fragments.FragCoursesList;
import fragments.FragFavoritesPreview;

public class CORE_COURSE_GET_CONTENTS extends AsyncTask<Object, Void, Object> {
	private ProgressDialog	dialog;
	private CountDownTimer	cvt	= createCountDownTimer();
	private ManDataStore	data;
	private ManSession		session;
	private Activity		activity;
	private String			url;
	private String			token;
	private long			courseId;
	private Fragment		fragment;

	public CORE_COURSE_GET_CONTENTS(Activity activity) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
		this.data = new ManDataStore(activity);
		session = new ManSession(activity);
	}

	public CORE_COURSE_GET_CONTENTS(Fragment fragment) {
		this.fragment = fragment;
		this.activity = fragment.getActivity();
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
		courseId = Long.parseLong((String) params[0]);

		MoodleCallRestWebService.init(url + "/webservice/rest/server.php", token);

		MoodleCourseContent[] courseContent = null;
		try {
			courseContent = MoodleRestCourse.getCourseContent(courseId, null);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (MoodleRestCourseException e) {
			e.printStackTrace();
		} catch (MoodleRestException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return courseContent;

	}

	@Override
	protected void onPostExecute(Object obj) {
		cvt.cancel();
		if (dialog != null)
			dialog.dismiss();

		String fileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + String.valueOf(courseId);

		if (obj != null)
			data.storeData(obj, fileName);

		if (fragment != null) {
			// if (fragment instanceof FragCoursesList)
			// ((FragCoursesList) fragment).courseContentAsyncTaskResult(obj);
			// if (fragment instanceof FragFavoritesPreview)
			// ((FragFavoritesPreview)
			// fragment).courseContentAsyncTaskResult(obj);
		} else {
		}

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
