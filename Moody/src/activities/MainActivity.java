package activities;

import fragments.TopicsPreview;
import fragments.UserPicture;
import interfaces.InterfaceDialogFrag;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;

import managers.Session;
import model.MoodyConstants;
import model.MoodyConstants.ActivityCode;
import model.MoodyConstants.MoodySession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

	// Session Manager Class
	Session session;

	private HashMap<String, String> organizedCourses = new HashMap<String, String>();

	private JSONObject jsonObj;

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

		// When its created it will get any course to populate the main fragment
		Entry<String, String> course = organizedCourses.entrySet().iterator()
				.next();
		String courseName = course.getValue();
		String courseId = course.getKey();
		Bundle bundle = new Bundle();
		bundle.putString("courseName", courseName);
		bundle.putString("courseId", courseId);
		initContentPreview(bundle);

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// // Inflate the menu; this adds items to the action bar if it is
		// present.
		getMenuInflater().inflate(R.menu.activity_main, menu);

		return true;

	}

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

	public void populateUsername() {

		TextView view = (TextView) findViewById(R.id.fullname_textview);
		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		String con = String.format(MoodyConstants.MoodySession.KEY_N_PARAMS,
				url, token, "core_webservice_get_site_info"
						+ MoodySession.KEY_JSONFORMAT);

		try {
			jsonObj = new DataAsyncTask().execute(con, "json").get();
			view.setText(jsonObj.getString("fullname"));

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void populateLeftListview() {

		String url = session.getValues(MoodyConstants.MoodySession.KEY_URL,
				null);
		String token = session.getValues(MoodyConstants.MoodySession.KEY_TOKEN,
				null);

		String id = session.getValues(MoodyConstants.MoodySession.KEY_ID, null);

		String con = String.format(MoodyConstants.MoodySession.KEY_PARAMS, url,
				token, "core_enrol_get_users_courses&userid", id
						+ MoodySession.KEY_JSONFORMAT);

		try {
			jsonObj = new DataAsyncTask().execute(con, "json").get();
			JSONArray coursesArray = jsonObj.getJSONArray("array");
			coursesInit(coursesArray);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void coursesInit(JSONArray coursesArray) throws JSONException {

		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		LinearLayout inserPoint = (LinearLayout) findViewById(R.id.linear_layout_inside_left);

		for (int j = 0; j < coursesArray.length(); j++) {

			JSONObject arrayCursor = coursesArray.getJSONObject(j);

			LinearLayout row = new LinearLayout(this);
			row.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
			View view = inflater.inflate(R.layout.courses_button_left_drawer,
					null);

			Button btnTag = (Button) view.findViewById(R.id.course_id);
			btnTag.setLayoutParams(new LayoutParams(
					android.view.ViewGroup.LayoutParams.MATCH_PARENT,
					android.view.ViewGroup.LayoutParams.WRAP_CONTENT));

			String name = arrayCursor.getString("fullname");
			int id = arrayCursor.getInt("id");
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

	public void populateUserPicture() {
		ImageButton login_button = (ImageButton) findViewById(R.id.login_image_button);
		if (session.getValues("PIC_PATH", null) == null) {

			try {

				String url = session.getValues(
						MoodyConstants.MoodySession.KEY_URL, null);
				String token = session.getValues(
						MoodyConstants.MoodySession.KEY_TOKEN, null);

				String con = String.format(
						MoodyConstants.MoodySession.KEY_N_PARAMS, url, token,
						"core_webservice_get_site_info"
								+ MoodySession.KEY_JSONFORMAT);

				jsonObj = new DataAsyncTask().execute(con, "json").get();
				String userPictureUrl = jsonObj.getString("userpictureurl");

				Drawable pic = DataAsyncTask
						.createDrawableFromUrl(userPictureUrl);

				login_button.setBackgroundResource(R.drawable.bkgd_imagebutton);
				login_button.setImageDrawable(pic);

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
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

	public void onAddFavoritesClick(View v) {

		// The add to favorites button id its the same of the course so we can
		// send the button id to future development

		if (organizedCourses.get(Integer.toString(v.getId())) != null) {
			String courseName = organizedCourses
					.get(Integer.toString(v.getId()));
			String courseId = Integer.toString(v.getId());

			Toast.makeText(getApplicationContext(),
					"Curso-> " + courseName + " ID-> " + v.getId(),
					Toast.LENGTH_SHORT).show();
		}

	}

	public void onContentPreviewClick(View v) {

		Toast.makeText(getApplicationContext(), " ID-> " + v.getId(),
				Toast.LENGTH_SHORT).show();

	}

}