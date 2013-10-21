package fragments;

import java.util.ArrayList;

import managers.ManSearch;
import model.ObjectSearch;
import ui.CardTextView;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.moody.R;

//public class FragSearch extends Activity {
public class FragSearch extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ManSearch manSearch = new ManSearch(getActivity());
		String query = getArguments().getString("query");
		manSearch.doMySearch(query);
		ArrayList<ObjectSearch> results = manSearch.getResults();
		View view = inflater.inflate(R.layout.frag_search, null);
		LinearLayout searchResults = (LinearLayout) view
				.findViewById(R.id.full_results_layout);
		for (int i = 0; i < results.size(); i++) {
			searchResults.addView(new CardTextView(getActivity(), 0, results
					.get(i).getCourseName(), View.VISIBLE, true, 0, results
					.get(i), query));
		}
		return view;
	}

}
