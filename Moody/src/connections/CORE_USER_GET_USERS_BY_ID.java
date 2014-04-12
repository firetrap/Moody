package connections;

import java.io.UnsupportedEncodingException;

import managers.ManDataStore;
import managers.ManSession;
import model.ModConstants;
import restPackage.MoodleCallRestWebService;
import restPackage.MoodleRestException;
import restPackage.MoodleRestUser;
import restPackage.MoodleRestUserException;
import restPackage.MoodleServices;
import restPackage.MoodleUser;
import activities.MainActivity;
import activities.UserDetailsActivity;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.CountDownTimer;

public class CORE_USER_GET_USERS_BY_ID extends AsyncTask<Object, Void, Object> {
	private ProgressDialog	dialog;
	private CountDownTimer	cvt	= createCountDownTimer();
	private Activity		activity;
	ManDataStore			data;
	ManSession				session;
	private String			token;
	private long			userId;
	private String			fileName;
	private String			url;

	public CORE_USER_GET_USERS_BY_ID(Activity activity) {
		this.activity = activity;
		dialog = new ProgressDialog(activity);
		data = new ManDataStore(activity);
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
		fileName = MoodleServices.CORE_USER_GET_USERS_BY_ID.name() + String.valueOf(userId);

		MoodleCallRestWebService.init(url + "/webservice/rest/server.php", token);
		MoodleUser user = null;

		if (!data.isInCache(fileName)) {
			try {
				user = MoodleRestUser.getUserById(userId);
			} catch (MoodleRestUserException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (MoodleRestException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else
			user = (MoodleUser) data.getData(fileName);

		return user;

	}

	@Override
	protected void onPostExecute(Object obj) {
		cvt.cancel();
		if (dialog != null)
			dialog.dismiss();

		if (obj != null) {
			data.storeData(obj, fileName);

			// if (activity instanceof MainActivity)
			// ((MainActivity) activity).userDataAsyncTaskResult(obj);

			// if (activity instanceof UserDetailsActivity)
			// ((UserDetailsActivity) activity).userDataAsyncTaskResult(obj);
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
