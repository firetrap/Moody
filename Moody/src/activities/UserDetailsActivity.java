package activities;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import managers.DialogsManager;
import managers.SessionManager;
import model.MoodyConstants.MoodySession;
import model.MoodyMessage;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.moody.R;

import connections.DataAsyncTask;

public class UserDetailsActivity extends Activity
// implements TextWatcher
{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_details);

		initComponents();
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
		HashMap<String, String> list = getData();

		if (list != null)
			initText(list);
		else {
			DialogsManager.showMessageDialog(this, new MoodyMessage(
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.user_details, menu);
		return true;
	}

	public HashMap<String, String> getData() {
		try {
			SessionManager session = new SessionManager(getApplicationContext());

			String url = session.getValues(MoodySession.KEY_URL, null);
			String token = session.getValues(MoodySession.KEY_TOKEN, null);
			String id = session.getValues(MoodySession.KEY_ID, null);
			String con = String.format(MoodySession.KEY_PARAMS, url, token,
					"core_user_get_users_by_id&userids[0]", id);

			return (HashMap<String, String>) new DataAsyncTask().execute(con, "xml").get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public void initText(HashMap<String, String> list) {

		String[] attributes = { "fullname1", "firstname1", "lastname1",
				"email1", "address1", "phone11", "phone21", "description1",
				"url1", "skype1", "yahoo1" };

		int[] textIds = { R.id.textView_full_name, R.id.editText_firstname,
				R.id.editText_lastname, R.id.editText_email,
				R.id.editText_address, R.id.editText_phonenumber,
				R.id.editText_mobilephonenumber, R.id.editText_description,
				R.id.editText_url, R.id.editText_skype, R.id.editText_yahoo };

		int[] layoutIds = { R.id.relativeLayout_fullname,
				R.id.linearLayout_firstname, R.id.linearLayout_lastname,
				R.id.linearLayout_email, R.id.linearLayout_address,
				R.id.linearLayout_phonenumber,
				R.id.linearLayout_mobilephonenumber,
				R.id.linearLayout_description, R.id.linearLayout_url,
				R.id.linearLayout_skype, R.id.linearLayout_yahoo };

		for (int i = 0; i < attributes.length; i++) {
			if ((isValid(list.get(attributes[i]))))
				((TextView) findViewById(textIds[i])).setText(list
						.get(attributes[i]));
			else
				findViewById(layoutIds[i]).setVisibility(View.GONE);
		}

	}

	public boolean isValid(String value) {
		return ((value != null) && (!value.isEmpty()));
	}

	// @Override
	// public void afterTextChanged(Editable arg0) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void beforeTextChanged(CharSequence s, int start, int count,
	// int after) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void onTextChanged(CharSequence s, int start, int before, int
	// count) {
	// // TODO Auto-generated method stub
	//
	// }

}
