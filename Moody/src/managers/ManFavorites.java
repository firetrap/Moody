package managers;

import java.util.ArrayList;

import model.ModConstants;
import android.content.Context;
import android.content.res.Resources;

import com.android.moody.R;

/**
 * 
 * @author MoodyProject Team
 *
 */
public class ManFavorites {

	// ManSession Manager Class
	ManSession session;
	ManDataStore data = new ManDataStore();
	Object getContent;

	public void insertFavorite(long id, Context context, Resources resource) {
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add(id);

		actionFavorite(ids, context, resource);
	}

	public void removeFavorite(ArrayList<Long> ids, Context context,
			Resources resource) {
		actionFavorite(ids, context, resource);
	}

	public void actionFavorite(ArrayList<Long> ids, Context context,
			Resources resource) {
		String userId = new ManSession(context).getValues(ModConstants.KEY_ID,
				null);
		String fileName = resource.getString(R.string.favorites_file_name)
				+ userId;
		ArrayList<Long> idList = getFavorites(context, resource);

		for (Long id : ids) {

			if (isFavorite(id, context, resource))
				idList.remove(id);
			else
				idList.add(id);

		}

		new ManDataStore().storeData(context, idList, fileName);
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Long> getFavorites(Context context, Resources resource) {
		String userId = new ManSession(context).getValues(ModConstants.KEY_ID,
				null);
		String fileName = resource.getString(R.string.favorites_file_name)
				+ userId;

		return (isInCache(context, fileName)) ? (ArrayList<Long>) data.getData(
				context, fileName) : new ArrayList<Long>();
	}

	public boolean isFavorite(long id, Context context, Resources resource) {
		return getFavorites(context, resource).contains(id);
	}

	/**
	 * @param context
	 * @param fileName
	 * @return boolean
	 */
	public boolean isInCache(Context context, String fileName) {
		Object content = new ManDataStore().getData(context, fileName);
		return !(content == null) ? true : false;
	}
}
