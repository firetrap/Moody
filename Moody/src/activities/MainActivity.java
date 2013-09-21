package activities;

import fragments.Topics;
import fragments.TopicsPreview;
import fragments.UserCloud;
import fragments.UserPicture;
import interfaces.InterfaceDialogFrag;

import java.util.HashMap;
import java.util.Map.Entry;

import managers.AlertDialogs;
import managers.Contents;
import managers.DataStore;
import managers.Session;
import model.MoodyConstants;
import model.MoodyMessage;
import restPackage.MoodleCourse;
import restPackage.MoodleUser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import bitmap.BitmapResizer;

import com.example.moody.R;

import connections.DataAsyncTask;

public class MainActivity extends Activity implements OnClickListener,
		InterfaceDialogFrag {

	private DrawerLayout myDrawerLayout;

	private HashMap<String, String> organizedCourses = new HashMap<String, String>();

	// Session Manager Class
	Session session;

	public long startTime;
	public long endTime;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		startTime = System.currentTimeMillis();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// shared pref
		session = new Session(getApplicationContext());
		myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		populateUsername();
		populateUserCourses();
		populateUserPicture();

		// When its created it will get any course to populate the main
		// fragment
		Entry<String, String> course = organizedCourses.entrySet().iterator()
				.next();
		int courseId = Integer.parseInt(course.getKey());
		Button btnTag = (Button) findViewById(courseId);
		btnTag.performClick();

	}

	public void populateUsername() {

		TextView view = (TextView) findViewById(R.id.fullname_textview);

		MoodleUser user = new Contents().getUser(getResources(),
				getApplicationContext());
		view.setText(user.getFullname());

	}

	public void populateUserPicture() {
		ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
		if (session.getValues("PIC_PATH", null) == null) {

			Drawable pic = null;
			MoodleUser user = new Contents().getUser(getResources(),
					getApplicationContext());

			user.getProfileImageURL();
			pic = DataAsyncTask
					.createDrawableFromUrl(user.getProfileImageURL());
			login_button.setBackgroundResource(R.drawable.bkgd_imagebutton);
			login_button.setImageDrawable(pic);

		} else {

			login_button.setImageBitmap(BitmapResizer
					.decodeSampledBitmapFromResource(
							session.getValues("PIC_PATH", null),
							R.id.login_image_button, 100, 100));

		}
	}

	private void populateUserCourses() {

		// Get all the courses from current user
		MoodleCourse[] courses = new Contents().getUserCourses(getResources(),
				getApplicationContext());

		// Start populating the menus and views
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout inserPoint = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		if (courses == null || courses.length == 0) {

			fatalError("Moody Fatal Error - Get Courses",
					"An Error Ocurred Retrieving Data contact your Moodle Administrator");

		} else {
			for (int j = 0; j < courses.length; j++) {

				LinearLayout row = new LinearLayout(this);
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				View view = inflater.inflate(
						R.layout.courses_button_left_drawer, null);

				Button btnTag = (Button) view.findViewById(R.id.course_id);
				btnTag.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

				String name = courses[j].getFullname();
				int id = courses[j].getId().intValue();
				btnTag.setText(name);
				btnTag.setId(id);

				organizedCourses.put(Integer.toString(id), name);


				row.addView(view);

				inserPoint.addView(row, 3);

			}

		}

	}

	public void onAddFavoritesClick(View v) {

		// The add to favorites button id its the same of the course so we can
		// send the button id to future development

		if (organizedCourses.get(Integer.toString(v.getId())) != null) {
			String courseName = organizedCourses
					.get(Integer.toString(v.getId()));
			Integer.toString(v.getId());

			Toast.makeText(getApplicationContext(),
					"Curso-> " + courseName + " ID-> " + v.getId(),
					Toast.LENGTH_SHORT).show();
		}

	}

	// Method to decide what to do from what button was pressed
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.login_image_button:

			FragmentManager fm = getFragmentManager();
			UserPicture userDetailsDialog = new UserPicture();
			userDetailsDialog.setRetainInstance(true);
			userDetailsDialog.show(fm, "fragment_name");

			break;
		case R.id.logout_image_button:
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						session.logoutUser();

						// limpa cache ao fazer logout.
						new DataStore().deleteCache(getApplicationContext());

						Intent intent = new Intent(getApplicationContext(),
								LoginActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
						startActivity(intent);

						finish();
						break;

					case DialogInterface.BUTTON_NEGATIVE:
						dialog.dismiss();

						break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Logout");
			builder.setMessage("Are you sure?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();

			break;

		case R.id.fullname_textview:
			Intent intent = new Intent(getApplicationContext(),
					UserDetailsActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			myDrawerLayout.closeDrawer(Gravity.LEFT);
			startActivity(intent);
			break;

		case R.id.cloud_button:
			Toast.makeText(getApplicationContext(), "CLOUD", Toast.LENGTH_SHORT)
					.show();

			FragmentManager frag = getFragmentManager();
			UserCloud userCloudDialog = new UserCloud();
			userCloudDialog.setRetainInstance(true);
			userCloudDialog.show(frag, "fragment_name");

			// Intent intents = getPackageManager().getLaunchIntentForPackage(
			// "com.dropbox.android");
			// intents.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// startActivity(intents);

			break;

		default:
			// myDrawerLayout.closeDrawer(Gravity.LEFT);

			// Toast.makeText(getApplicationContext(),
			// "ENTROU NO PRIMEIRO :" + v.getId(), Toast.LENGTH_SHORT)
			// .show();
			// throw new RuntimeException("Unknown button ID");
		}
	}

	public void onTopicsPreviewClick(View v) {
		String courseId = Integer.toString(v.getId());
		String courseName = organizedCourses.get(Integer.toString(v.getId()));
		String topicId = (String) v.getTag();

		Toast.makeText(
				getApplicationContext(),
				" COURSE ID-> " + courseId + " TOPIC ID-> " + topicId
						+ "COURSE NAME ->" + courseName, Toast.LENGTH_SHORT)
				.show();

		Bundle bundle = new Bundle();
		bundle.putString("courseId", courseId);
		bundle.putString("courseName", courseName);
		bundle.putString("topicId", topicId);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		Topics insideTopicsFrag = new Topics();
		insideTopicsFrag.setArguments(bundle);
		fragmentTransaction.replace(R.id.mainFragment, insideTopicsFrag);
		fragmentTransaction.commit();

	}

	public void onCoursesClick(View v) {

		// The view id is the same id of the courses

		String courseName = organizedCourses.get(Integer.toString(v.getId()));
		String courseId = Integer.toString(v.getId());
		Toast.makeText(getApplicationContext(),
				"Curso-> " + courseName + " ID-> " + v.getId(),
				Toast.LENGTH_SHORT).show();

		Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);

		FragmentTransaction fragmentTransaction = getFragmentManager()
				.beginTransaction();
		TopicsPreview fragment = new TopicsPreview();
		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();
		myDrawerLayout.closeDrawer(Gravity.LEFT);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// // Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;

	}

	/**
	 * The method it's responsible to retrieve the bitmap data/resize/compress
	 * The first case of the switch it's not fully implemented as planned,
	 * moodle doesn't support the users to update their own user details this
	 * issue is described and reported here
	 * https://tracker.moodle.org/browse/CONTRIB-4282 The next code it's ready
	 * 
	 * TO DO: implement the user update details in Moody when Moodle.org decide
	 * to add the required web service function.
	 * 
	 */
	@Override
	public void onFinishEditDialog(String inputText, int code) {
		switch (code) {
		case MoodyConstants.DIALOG_FRAG_USER_PIC:
			session.addPref(inputText);
			ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);

			login_button.setImageBitmap(BitmapResizer
					.decodeSampledBitmapFromResource(inputText,
							R.id.login_image_button, 100, 100));
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_settings:

			break;

		default:
			return super.onOptionsItemSelected(item);
		}

		return true;
	}

	/**
	 * Moody Fatal Error Method it will display an alert dialog with the message
	 * and it will clear app data and kill the app
	 */
	private void fatalError(String title, String msg) {
		AlertDialogs.showMessageDialog(this, new MoodyMessage(title, msg),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						session.logoutUser();
						// limpa cache ao fazer logout.
						new DataStore().deleteCache(getApplicationContext());
						finish();
						android.os.Process.killProcess(android.os.Process
								.myPid());
					}

				}, false);
	}

	@Override
	protected void onResume() {
		endTime = System.currentTimeMillis();
		Log.d("MoodyPerformance",
				Long.toString(performanceMeasure(startTime, endTime)));
		super.onResume();
	}

	public long performanceMeasure(long startTime, long endTime) {
		long timestamp = endTime - startTime;
		return timestamp;
	}

}