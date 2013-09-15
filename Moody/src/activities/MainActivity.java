package activities;

import fragments.InsideTopics;
import fragments.TopicsPreview;
import fragments.UserCloud;
import fragments.UserPicture;
import interfaces.InterfaceDialogFrag;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import managers.AlertDialogs;
import managers.DataStore;
import managers.Session;
import model.EnumWebServices;
import model.MoodyConstants;
import model.MoodyMessage;
import model.MoodyConstants.ActivityCode;
import model.MoodyConstants.MoodySession;

import org.json.JSONException;
import org.json.JSONObject;

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

import connections.CopyOfDataAsyncTask;
import connections.DataAsyncTask;

public class MainActivity extends Activity implements OnClickListener,
		InterfaceDialogFrag {

	private JSONObject jsonObj;

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
		final Entry<String, String> course = organizedCourses.entrySet()
				.iterator().next();
		final String courseName = course.getValue();
		final String courseId = course.getKey();
		final Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);
		initContentPreview(bundle);

	}

	private void coursesInit(MoodleCourse[] courses) {

		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		final LinearLayout inserPoint = (LinearLayout) findViewById(R.id.linear_layout_inside_left);
		
		if (courses == null || courses.length == 0) {

			fatalError("Moody Fatal Error - Get Courses",
					"An Error Ocurred Retrieving Data contact your Moodle Administrator");

		} else {
			for (int j = 0; j < courses.length; j++) {

				final LinearLayout row = new LinearLayout(this);
				row.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
				final View view = inflater.inflate(
						R.layout.courses_button_left_drawer, null);

				final Button btnTag = (Button) view
						.findViewById(R.id.course_id);
				btnTag.setLayoutParams(new LayoutParams(
						android.view.ViewGroup.LayoutParams.MATCH_PARENT,
						android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

				final String name = courses[j].getFullname();
				final int id = courses[j].getId().intValue();
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

		final FragmentManager fragmentManager = getFragmentManager();
		final FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		final TopicsPreview fragment = new TopicsPreview();
		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();

	}

	public void onAddFavoritesClick(View v) {

		// The add to favorites button id its the same of the course so we can
		// send the button id to future development

		if (organizedCourses.get(Integer.toString(v.getId())) != null) {
			final String courseName = organizedCourses.get(Integer.toString(v
					.getId()));
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

			final FragmentManager fm = getFragmentManager();
			final UserPicture userDetailsDialog = new UserPicture();
			userDetailsDialog.setRetainInstance(true);
			userDetailsDialog.show(fm, "fragment_name");

			break;
		case R.id.logout_image_button:
			final DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
					case DialogInterface.BUTTON_POSITIVE:
						session.logoutUser();

						// limpa cache ao fazer logout.
						new DataStore().deleteCache(getApplicationContext());

						final Intent intent = new Intent(
								getApplicationContext(), LoginActivity.class);
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

			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Logout");
			builder.setMessage("Are you sure?")
					.setPositiveButton("Yes", dialogClickListener)
					.setNegativeButton("No", dialogClickListener).show();

			break;

		case R.id.fullname_textview:
			final Intent intent = new Intent(getApplicationContext(),
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
		final String courseId = Integer.toString(v.getId());
		final String courseName = organizedCourses.get(Integer.toString(v
				.getId()));
		final String topicId = (String) v.getTag();

		Toast.makeText(
				getApplicationContext(),
				" COURSE ID-> " + courseId + " TOPIC ID-> " + topicId
						+ "COURSE NAME ->" + courseName, Toast.LENGTH_SHORT)
				.show();

		final Bundle bundle = new Bundle();
		bundle.putString("courseId", courseId);
		bundle.putString("courseName", courseName);
		bundle.putString("topicId", topicId);

		final FragmentManager fragmentManager = getFragmentManager();
		final FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		final InsideTopics fragment = new InsideTopics();
		fragment.setArguments(bundle);
		fragmentTransaction.replace(R.id.mainFragment, fragment);
		fragmentTransaction.commit();

	}

	public void onCoursesClick(View v) {

		// The view id is the same of the course id

		final String courseName = organizedCourses.get(Integer.toString(v
				.getId()));
		final String courseId = Integer.toString(v.getId());
		Toast.makeText(getApplicationContext(),
				"Curso-> " + courseName + " ID-> " + v.getId(),
				Toast.LENGTH_SHORT).show();

		final Bundle bundle = new Bundle();
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
			final ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);

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
		final String url = session.getValues(
				MoodyConstants.MoodySession.KEY_URL, null);
		final String token = session.getValues(
				MoodyConstants.MoodySession.KEY_TOKEN, null);

		final String id = session.getValues(MoodyConstants.MoodySession.KEY_ID,
				null);

		Object getContent;
		try {
			getContent = new CopyOfDataAsyncTask().execute(url, token,
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

		final TextView view = (TextView) findViewById(R.id.fullname_textview);
		final String url = session.getValues(
				MoodyConstants.MoodySession.KEY_URL, null);
		final String token = session.getValues(
				MoodyConstants.MoodySession.KEY_TOKEN, null);

		final String id = session.getValues(MoodyConstants.MoodySession.KEY_ID,
				null);

		Object getContent;
		try {
			getContent = new CopyOfDataAsyncTask().execute(url, token,
					EnumWebServices.CORE_USER_GET_USERS_BY_ID, id).get();
			MoodleUser user = (MoodleUser) getContent;
			String jgjhg = user.getFullname();
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
		final ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
		if (session.getValues("PIC_PATH", null) == null) {

			try {

				final String url = session.getValues(
						MoodyConstants.MoodySession.KEY_URL, null);
				final String token = session.getValues(
						MoodyConstants.MoodySession.KEY_TOKEN, null);

				final String con = String.format(
						MoodyConstants.MoodySession.KEY_N_PARAMS, url, token,
						"core_webservice_get_site_info"
								+ MoodySession.KEY_JSONFORMAT);

				jsonObj = new DataAsyncTask().execute(con, "json").get();
				final String userPictureUrl = jsonObj
						.getString("userpictureurl");

				final Drawable pic = DataAsyncTask
						.createDrawableFromUrl(userPictureUrl);

				login_button.setBackgroundResource(R.drawable.bkgd_imagebutton);
				login_button.setImageDrawable(pic);

			} catch (final InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			login_button.setImageBitmap(BitmapResizer
					.decodeSampledBitmapFromResource(
							session.getValues("PIC_PATH", null),
							R.id.login_image_button, 100, 100));

		}
	}

}