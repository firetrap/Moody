package activities;

import java.util.concurrent.ExecutionException;

import managers.AlertDialogs;
import managers.Session;
import model.MoodyConstants.MoodySession;
import model.EnumWebServices;
import model.MoodyMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import restPackage.MoodleUser;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.moody.R;

import connections.CopyOfDataAsyncTask;
import connections.DataAsyncTask;

public class UserDetailsActivity extends Activity
// implements TextWatcher
{

	public MoodleUser getData() {

		Session session = new Session(getApplicationContext());
		String url = session.getValues(MoodySession.KEY_URL, null);
		String token = session.getValues(MoodySession.KEY_TOKEN, null);
		String id = session.getValues(MoodySession.KEY_ID, null);

		Object getContent = null;

		try {
			getContent = new CopyOfDataAsyncTask().execute(url, token,
					EnumWebServices.CORE_USER_GET_USERS_BY_ID, id).get();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return (MoodleUser) getContent;
	}

	public void initComponents() {
		/**
		 * ADDS LISTENERS, SO INPUT CAN BE VALIDATED
		 * ((EditText)findViewById(R.id
		 * .editText_firstname)).addTextChangedListener(this);
		 * ((EditText)findViewById
		 * (R.id.editText_lastname)).addTextChangedListener(this);
		 * ((EditText)findViewById
		 * (R.id.editText_email)).addTextChangedListener(this);
		 **/
		MoodleUser user = getData();

		if (user != null) {
			initDetails(user);
		} else {
			AlertDialogs.showMessageDialog(this, new MoodyMessage(
					"Moody Error", "An Error Ocurred Retrieving Data"),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}

					}, false);
		}
	}

	public void showHideLayout(int lID, Boolean show) {

		findViewById(lID).setVisibility(show ? View.VISIBLE : View.GONE);

	}

	public void initTextView(int vID, String text, boolean hasHTML) {

		// precisa de cast porque a View não tem text.
		// se for campo que tenha HTML formata.
		((TextView) findViewById(vID)).setText((hasHTML) ? Html.fromHtml(text)
				: text);

	}

	public void processTextView(int vID, int lID, String text, boolean hasHTML) {

		if (isValid(text))
			initTextView(vID, text, hasHTML);
		else
			showHideLayout(lID, false);
	}

	public void initDetails(MoodleUser user) {
		
		processTextView(R.id.textView_full_name, R.id.relativeLayout_fullname, user.getFullname(), false);
		processTextView(R.id.editText_firstname, R.id.linearLayout_firstname, user.getFirstname(), false);
		processTextView(R.id.editText_lastname, R.id.linearLayout_lastname, user.getLastname(), false);
		processTextView(R.id.editText_email, R.id.linearLayout_email, user.getEmail(), false);
		processTextView(R.id.editText_address, R.id.linearLayout_address, user.getAddress(), false);
		processTextView(R.id.editText_phonenumber, R.id.linearLayout_phonenumber, user.getPhone1(), false);
		processTextView(R.id.editText_mobilephonenumber, R.id.linearLayout_mobilephonenumber, user.getPhone2(), false);
		processTextView(R.id.editText_description, R.id.linearLayout_description, user.getDescription(), true);
		processTextView(R.id.editText_url, R.id.linearLayout_url, user.getURL(), false);
		processTextView(R.id.editText_skype, R.id.editText_skype, user.getSkype(), false);
		processTextView(R.id.editText_yahoo, R.id.linearLayout_yahoo, user.getYahoo(), false);
		
	}

	public boolean isValid(String propertie) {
		return ((propertie != null) && (!propertie.isEmpty()));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);

		initComponents();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_details, menu);
		return true;
	}

}
