package activities;

import managers.ManAlertDialog;
import managers.ManContents;
import model.ModMessage;
import restPackage.MoodleUser;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.android.moody.R;

/**
 * @author SérgioFilipe
 * 
 */
public class UserDetailsActivity extends Activity {

	/**
	 * Method responsible to initialize the userDetails activity
	 */
	private void initComponents() {
		MoodleUser user = new ManContents(getApplicationContext()).getUser();

		if (user != null) {
			initDetails(user);
		} else {
			ManAlertDialog.showMessageDialog(this, new ModMessage(
					"Moody Error", "An Error Occurred Retrieving Data"),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							finish();
						}

					}, false);
		}
	}

	/**
	 * responsible to show or hide only the available fields from Moodle
	 * 
	 * @param lID
	 * @param show
	 */
	private void showHideLayout(int lID, Boolean show) {

		findViewById(lID).setVisibility(!(show) ? View.GONE : View.VISIBLE);

	}

	/**
	 * 
	 * Check if the data from Moodle is string or HTML and set the correct
	 * attribute
	 * 
	 * @param vID
	 * @param text
	 * @param hasHTML
	 */
	private void initTextView(int vID, String text, boolean hasHTML) {

		((TextView) findViewById(vID)).setText(!((hasHTML)) ? text : Html
				.fromHtml(text));

	}

	/**
	 * 
	 * If the field is valid it show it else hide the textview
	 * 
	 * @param vID
	 * @param lID
	 * @param text
	 * @param hasHTML
	 */
	private void processTextView(int vID, int lID, String text, boolean hasHTML) {

		if (!(isValid(text)))
			showHideLayout(lID, false);
		else
			initTextView(vID, text, hasHTML);

	}

	/**
	 * Initialize all the required views
	 * 
	 * @param user
	 */
	private void initDetails(MoodleUser user) {

		processTextView(R.id.textView_full_name, R.id.relativeLayout_fullname,
				user.getFullname(), false);
		processTextView(R.id.editText_firstname, R.id.linearLayout_firstname,
				user.getFirstname(), false);
		processTextView(R.id.editText_lastname, R.id.linearLayout_lastname,
				user.getLastname(), false);
		processTextView(R.id.editText_email, R.id.linearLayout_email,
				user.getEmail(), false);
		processTextView(R.id.editText_address, R.id.linearLayout_address,
				user.getAddress(), false);
		processTextView(R.id.editText_phonenumber,
				R.id.linearLayout_phonenumber, user.getPhone1(), false);
		processTextView(R.id.editText_mobilephonenumber,
				R.id.linearLayout_mobilephonenumber, user.getPhone2(), false);
		processTextView(R.id.editText_description,
				R.id.linearLayout_description, user.getDescription(), true);
		processTextView(R.id.editText_url, R.id.linearLayout_url,
				user.getURL(), false);
		processTextView(R.id.editText_skype, R.id.editText_skype,
				user.getSkype(), false);
		processTextView(R.id.editText_yahoo, R.id.linearLayout_yahoo,
				user.getYahoo(), false);

	}

	private boolean isValid(String propertie) {
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
