package managers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.os.AsyncTask;
import android.util.Log;
import connections.XMLparser;

public class DataManager {

	private String url;
	private String fullname;
	
//	private AttemptConnection mAuthTask = null;
	
//	Class constructor
	public DataManager(String url) {
		this.url = url;
	}

//	public String getUserFullName() {
//
//		// Get user id URL
//		// urlName =
//		// "http://193.137.46.10/default_site/Moody/webservice/rest/server.php?wstoken=1cfced5578e4c32fe857df433cdb7ba6&wsfunction=core_user_get_users_by_id&userids[0]=3";
//		// Parent node
//		String nodeParent = "KEY";
//		// init XMLparser
//		XMLparser parser = new XMLparser();
//		// getting XML
//		String xml = parser.getXmlFromUrl(url);
//		// getting DOM element
//		Document doc = parser.getDomElement(xml);
//
//		NodeList nl = doc.getElementsByTagName(nodeParent);
//
//		// looping through all item nodes <item>
//		for (int i = 0; i < nl.getLength(); i++) {
//			Element e = (Element) nl.item(i);
//
//			if (e.getParentNode().getParentNode().getParentNode().getNodeName()
//					.equals("RESPONSE")) {
//				if (e.getAttribute("name").equals("fullname")) {
//					fullname = e.getTextContent();
//					return fullname;
//				}
//			}
//		}
//		return null;
//
//	}
//
//	public void Validation() {
//		if (mAuthTask != null) {
//			return;
//		}
//		boolean cancel = false;
//
////		// Check for url.
////		if (TextUtils.isEmpty(mUrl)) {
////			mUrlView.setError(getString(R.string.error_field_required));
////			focusView = mUrlView;
////			cancel = true;
////
////		} else if (mUrl.length() < 20) {
////			mUrlView.setError(getString(R.string.error_invalid_url));
////			focusView = mUrlView;
////			cancel = true;
////		}
////
////		// Check if URL contains the required http protocol.
////		if (mUrl.subSequence(0, 7).equals("http://")) {
////
////		} else {
////			mUrl = "http://" + mUrl;
////		}
//
//		if (cancel) {
//			// There was an error; don't attempt login and focus the first
//			// form field with an error.
//
//		} else {
//			// Show a progress spinner, and kick off a background task to
//			// perform the user login attempt.
//
//			mAuthTask = new AttemptConnection();
//			mAuthTask.execute((Void) null);
//
//		}
//	}
//
//	// Represents an asynchronous login/registration task used to authenticate
//	// the user.
//	public class AttemptConnection extends AsyncTask<Void, Void, Boolean> {
//		@Override
//		protected Boolean doInBackground(Void... params) {
//			// TODO: attempt authentication against a network service.
//			URL urlAsync = null;
//
//			try {
//				urlAsync = new URL(url);
//				Log.d("AsyncTask", urlAsync.toString());
//			} catch (MalformedURLException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Log.d("MoodyErrorConnection", e.toString());
//				return false;
//			}
//			try {
//				Log.d("MoodyDebug", "Connect");
//				HttpURLConnection con = (HttpURLConnection) urlAsync
//						.openConnection();
//				con.connect();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				Log.d("MoodyDebug", "Cant Connect");
//				return false;
//
//			}
//
//			return true;
//
//		}
//
//		@Override
//		protected void onPostExecute(final Boolean success) {
//
//			if (success) {
////				// Session Manager and shared pref send to shared pref:
////				// user-name, user-token, User-id in database
////				session = new SessionManager(getApplicationContext());
////				session.createLoginSession(mUser, FinalToken, UserId);
////				finish();
//			} else {
////				Log.d("MoodyDebug", "onPOstExecute-FAILED");
////				mPasswordView
////
////				.setError(getString(R.string.error_incorrect_password));
////				mPasswordView.requestFocus();
//			}
//		}
//
//		@Override
//		protected void onCancelled() {
//			mAuthTask = null;
////			showProgress(false);
//		}
//	}
}
