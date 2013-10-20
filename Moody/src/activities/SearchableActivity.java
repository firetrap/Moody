package activities;

import java.util.ArrayList;

import managers.ManSearch;
import model.ObjectSearch;
import android.app.ListActivity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.moody.R;

//public class SearchableActivity extends Activity {
public class SearchableActivity extends ListActivity {

	ManSearch manSearch = new ManSearch(getApplicationContext());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchable);

		 // Get the intent, verify the action and get the query
		 Intent intent = getIntent();
		 if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
		 String query = intent.getStringExtra(SearchManager.QUERY);
		 if (query != null) {
		 // do the search
		 manSearch.doMySearch(query);
		 // return the arraylist with the topic which contains the query
		 ArrayList<ObjectSearch> results = manSearch.getResults();
		 }
		 }

	

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.searchable, menu);
		return true;
	}
}
