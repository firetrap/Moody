package com.example.moody;

import java.util.ArrayList;
import java.util.Arrays;

import managers.SessionManager;
import activities.LoginActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockActivity;

public class LeftActivity extends SherlockActivity implements OnClickListener{
	private ListView leftListView;
	private ArrayAdapter<String> listAdapter;
	// Session Manager Class
	SessionManager session;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_left);
		moveTaskToBack(true);
		leftListView = (ListView) findViewById(R.id.left_list_viewer);

		// Create and populate a List of planet names.
		String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
				"Jupiter", "Saturn", "Uranus", "Neptune" };
		ArrayList<String> planetList = new ArrayList<String>();
		planetList.addAll(Arrays.asList(planets));

		// Create ArrayAdapter using the planet list.
		listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow,
				planetList);

		// Add more planets. If you passed a String[] instead of a List<String>
		// into the ArrayAdapter constructor, you must not add more items.
		// Otherwise an exception will occur.
		listAdapter.add("Ceres");
		listAdapter.add("Pluto");
		listAdapter.add("Haumea");
		listAdapter.add("Makemake");
		listAdapter.add("Eris");

		// Set the ArrayAdapter as the ListView's adapter.
		leftListView.setAdapter(listAdapter);

		// OnClickListener's for all button, after pressed it will send for the
		// onClick method the button pressed
		ImageButton loginImageButton = (ImageButton) findViewById(R.id.login_image_button);
		loginImageButton.setOnClickListener(this);
		ImageButton logouImageButton = (ImageButton) findViewById(R.id.logout_image_button);
		logouImageButton.setOnClickListener(this);

	}

	// Method to decide what to do from what button was pressed
	public void onClick(View v) {
		session = new SessionManager(getApplicationContext());
		switch (v.getId()) {
		case R.id.login_image_button:
			if (session.isLoggedIn() == false) {
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			} else {
				Log.d("MoodyDebud",
						"Entrará aqui se o utilizador ja estiver logado e em vez de vir para aqui irá para as defeniçoes de utilizador");
			}
			break;
		case R.id.logout_image_button:
			if (session.isLoggedIn() == true) {
				session.logoutUser();
			} else {
				// só entra neste else caso o utilizador ainda nao esteja
				// loggado entao é reencaminhado para o LoginActivity
				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
			}
			break;
		default:
			throw new RuntimeException("Unknown button ID");
		}
	}

	

}
