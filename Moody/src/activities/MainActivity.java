package activities;

import java.util.ArrayList;
import java.util.Arrays;

import managers.SessionManager;

import com.actionbarsherlock.app.SherlockActivity;

import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.example.moody.R;

public class MainActivity extends SherlockActivity {
	private ListView mainListView;
	private ListView leftListView;
	private ListView rightListView;
	private ArrayAdapter<String> listAdapter;
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// Find the ListView resource.
		mainListView = (ListView) findViewById(R.id.main_list_viewer);
		leftListView = (ListView) findViewById(R.id.left_list_viewer);
		rightListView = (ListView) findViewById(R.id.right_list_viewer);

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
		mainListView.setAdapter(listAdapter);
		leftListView.setAdapter(listAdapter);
		rightListView.setAdapter(listAdapter);
		
		
	ImageButton loginImageButton = (ImageButton)findViewById(R.id.login_image_button);
 		
 		loginImageButton.setOnClickListener(new OnClickListener() {
 			@Override
 			public void onClick(View v) {
 				Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
 				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
 				startActivity(intent);
 			}
 		});
 	
 		
 	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// // Inflate the menu; this adds items to the action bar if it is
		// present.
		getSupportMenuInflater().inflate(R.menu.activity_main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		switch (item.getItemId()) {
//		case R.id.menu_settings:
//			Intent intent = new Intent(this, LoginActivity.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//			startActivity(intent);
//			break;
//
//		default:
//			return super.onOptionsItemSelected(item);
//		}

		return true;
	}

}
