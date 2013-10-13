package managers;

import java.util.ArrayList;

import model.ModConstants;
import android.content.Context;

import com.android.moody.R;

/**
 * @author SérgioFilipe
 * @contributor firetrap
 * 
 */
public class ManFavorites {

	// ManSession Manager Class
	ManSession session;
	Object getContent;
	Context context;
	ManDataStore data;

	public ManFavorites(Context context) {
		this.context = context;
		this.data=new ManDataStore(context);
	}

	public void insertFavorite(long id) {
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add(id);

		actionFavorite(ids);
	}

	public void removeFavorite(ArrayList<Long> ids) {
		actionFavorite(ids);
	}

	public void actionFavorite(ArrayList<Long> ids) {
		String userId = new ManSession(context).getValues(ModConstants.KEY_ID,
				null);
		String fileName = context.getResources().getString(
				R.string.favorites_file_name)
				+ userId;
		ArrayList<Long> idList = getFavorites();

		for (Long id : ids) {

			if (isFavorite(id))
				idList.remove(id);
			else
				idList.add(id);

		}

		data.storeData(idList, fileName);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Long> getFavorites() {
		String userId = new ManSession(context).getValues(ModConstants.KEY_ID,
				null);
		String fileName = context.getResources().getString(
				R.string.favorites_file_name)
				+ userId;

		return (isInCache(fileName)) ? (ArrayList<Long>) data.getData(fileName) : new ArrayList<Long>();
	}

	public boolean isFavorite(long id) {
		return getFavorites().contains(id);
	}

	/**
	 * @param context
	 * @param fileName
	 * @return boolean
	 */
	public boolean isInCache(String fileName) {
		Object content = data.getData(fileName);
		return !(content == null) ? true : false;
	}
}
