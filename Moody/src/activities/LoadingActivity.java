package activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.android.moody.R;

/**
 * @author firetrap
 *
 */
public class LoadingActivity extends Activity {

	private View mLoadingStatusView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		mLoadingStatusView = findViewById(R.id.loading_status);
		showProgress(true);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		int shortAnimTime = 3000;
		// getResources().getInteger(
		// android.R.integer.config_longAnimTime);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

			mLoadingStatusView.setVisibility(View.VISIBLE);
			mLoadingStatusView.animate().setDuration(5000).alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							finish();
						}
					});

		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoadingStatusView.setVisibility(View.VISIBLE);
			try {
				wait(shortAnimTime);
				finish();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
