package activities;

import fragments.InsideTopics;
import fragments.TopicsPreview;
import fragments.UserCloud;
import fragments.UserPicture;
import interfaces.InterfaceDialogFrag;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import managers.AlertDialogs;
import managers.DataStore;
import managers.Session;
import model.EnumWebServices;
import model.MoodyConstants;
import model.MoodyConstants.ActivityCode;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// shared pref
		session = new Session(getApplicationContext());
		myDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		populateUsername();
		populateLeftListview();
		populateUserPicture();

		// When its created it will get any course to populate the main
		// fragment
		Entry<String, String> course = organizedCourses.entrySet().iterator()
				.next();
		String courseName = course.getValue();
		String courseId = course.getKey();
		Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);
		initContentPreview(bundle);

	}

	private void coursesInit(MoodleCourse[] courses) {

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

				if (j != 0) {
					btnTag.setBackgroundResource(R.drawable.border_inside);
				}

				row.addView(view);

				inserPoint.addView(row, 3);

			}

		}

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

	public void initContentPreview(Bundle bundle) {

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		TopicsPreview fragment = new TopicsPreview();
		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();

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

	public void onContentPreviewClick(View v) {
		String courseId = Integer.toString(v.getId());
		String courseName = organizedCourses.get(Integer.toString(v.getId()));
		String topicId = (String) v.getTag();

		topicId.trim();
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

		InsideTopics insideTopicsFrag = new InsideTopics();
		insideTopicsFrag.setArguments(bundle);
		fragmentTransaction.replace(R.id.mainFragment, insideTopicsFrag);
		fragmentTransaction.commit();

	}

	public void onCoursesClick(View v) {

		// The view id is the same of the course id

		String courseName = organizedCourses.get(Integer.toString(v.getId()));
		String courseId = Integer.toString(v.getId());
		Toast.makeText(getApplicationContext(),
				"Curso-> " + courseName + " ID-> " + v.getId(),
				Toast.LENGTH_SHORT).show();

		Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);

		initContentPreview(bundle);
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
		case ActivityCode.DIALOG_FRAG_USER_PIC:
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
		// // switch (item.getItemId()) {
		// // case R.id.menu_settings:
		// // Intent intent = new Intent(this, LoginActivity.class);
		// // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		// // startActivity(intent);
		// // break;
		// //
		// // default:
		// // return super.onOptionsItemSelected(item);
		// // }
		//
		return true;
	}

	public void populateLeftListview() {
		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		String id = session.getValues(MoodyConstants.MoodySession.KEY_ID, null);

		Object getContent;
		try {
			getContent = new DataAsyncTask().execute(url, token,
					EnumWebServices.CORE_ENROL_GET_USERS_COURSES, id).get();
			MoodleCourse[] courses = (MoodleCourse[]) getContent;
			coursesInit(courses);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void populateUsername() {

		TextView view = (TextView) findViewById(R.id.fullname_textview);
		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		String id = session.getValues(MoodyConstants.MoodySession.KEY_ID, null);

		Object getContent = null;
		try {
			getContent = new DataAsyncTask().execute(url, token,
					EnumWebServices.CORE_USER_GET_USERS_BY_ID, id).get();
			MoodleUser user = (MoodleUser) getContent;
			view.setText(user.getFullname());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void populateUserPicture() {
		ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
		if (session.getValues("PIC_PATH", null) == null) {

			String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
					null);
			String token = session.getValues(
					MoodyConstants.MoodySession.KEY_TOKEN, null);

			String id = session.getValues(MoodyConstants.MoodySession.KEY_ID,
					null);

			Object getContent;
			Drawable pic = null;
			MoodleUser user = null;
			try {
				getContent = new DataAsyncTask().execute(url, token,
						EnumWebServices.CORE_USER_GET_USERS_BY_ID, id).get();
				user = (MoodleUser) getContent;

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			user.getProfileImageURL();
			pic = DataAsyncTask.createDrawableFromUrl(user
					.getProfileImageURL());
			login_button.setBackgroundResource(R.drawable.bkgd_imagebutton);
			login_button.setImageDrawable(pic);

		} else {

			login_button.setImageBitmap(BitmapResizer
					.decodeSampledBitmapFromResource(
							session.getValues("PIC_PATH", null),
							R.id.login_image_button, 100, 100));

		}
	}

}