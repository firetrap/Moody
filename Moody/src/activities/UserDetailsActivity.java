package activities;

import java.util.concurrent.ExecutionException;

import managers.AlertDialogs;
import managers.Session;
import model.MoodyConstants.MoodySession;
import model.MoodyMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.example.moody.R;

import connections.DataAsyncTask;

public class UserDetailsActivity extends Activity
// implements TextWatcher
{

	public JSONObject getData() {
		try {
			final Session session = new Session(getApplicationContext());

			final String url = session.getValues(MoodySession.KEY_URL, null);
			final String token = session
					.getValues(MoodySession.KEY_TOKEN, null);
			final String id = session.getValues(MoodySession.KEY_ID, null);
			final String con = String.format(MoodySession.KEY_PARAMS, url,
					token, "core_user_get_users_by_id&userids[0]", id
							+ MoodySession.KEY_JSONFORMAT);

			return new DataAsyncTask().execute(con, "json").get();
		} catch (final InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		return null;
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
		final JSONObject list = getData();

		if (list != null) {
			initText(list);
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

	public void initText(JSONObject list) {
		try {
			final JSONArray coursesArray = list.getJSONArray("array");

			final String[] attributes = { "fullname", "firstname", "lastname",
					"email", "address", "phone1", "phone2", "description",
					"url", "skype", "yahoo" };

			final int[] textIds = { R.id.textView_full_name,
					R.id.editText_firstname, R.id.editText_lastname,
					R.id.editText_email, R.id.editText_address,
					R.id.editText_phonenumber, R.id.editText_mobilephonenumber,
					R.id.editText_description, R.id.editText_url,
					R.id.editText_skype, R.id.editText_yahoo };

			final int[] layoutIds = { R.id.relativeLayout_fullname,
					R.id.linearLayout_firstname, R.id.linearLayout_lastname,
					R.id.linearLayout_email, R.id.linearLayout_address,
					R.id.linearLayout_phonenumber,
					R.id.linearLayout_mobilephonenumber,
					R.id.linearLayout_description, R.id.linearLayout_url,
					R.id.linearLayout_skype, R.id.linearLayout_yahoo };

			for (int i = 0; i < coursesArray.length(); i++) {
				final JSONObject arrayCursor = coursesArray.getJSONObject(i);

				for (int j = 0; j < attributes.length; j++) {
					// String asdsa = (String) arrayCursor.get(attributes[j]);
					Object obj = null;

					// se não existir, rebenta e entra no catch, não faz nada,
					// manda null para o IsValid que invalida e o resto do
					// código
					// esconde as opções.
					try {
						obj = arrayCursor.get(attributes[j]);
					} catch (final JSONException ex) {

					} catch (final Exception ex) {

					}

					if (isValidObject(obj)) {
						String text = (String) arrayCursor.get(attributes[j]);

						((TextView) findViewById(textIds[j]))
								.setText((attributes[j]
										.equalsIgnoreCase("description")) ? Html
										.fromHtml(text) : text);
					} else {
						findViewById(layoutIds[j]).setVisibility(View.GONE);
					}

				}

			}

		} catch (final JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean isValidObject(Object object) {
		return object != null && !((String) object).isEmpty();
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
