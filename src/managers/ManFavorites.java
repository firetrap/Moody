package managers;

import java.util.ArrayList;

import model.ModConstants;
import android.content.Context;

import com.firetrap.moody.R;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

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

	/**
	 * @param context
	 */
	public ManFavorites(Context context) {
		this.context = context;
		this.data = new ManDataStore(context);
	}

	/**
	 * @param id
	 */
	public void insertFavorite(long id) {
		ArrayList<Long> ids = new ArrayList<Long>();
		ids.add(id);

		actionFavorite(ids);
	}

	/**
	 * @param ids
	 */
	public void removeFavorite(ArrayList<Long> ids) {
		actionFavorite(ids);
	}

	/**
	 * @param ids
	 */
	public void actionFavorite(ArrayList<Long> ids) {
		String userId = new ManSession(context).getValues(ModConstants.KEY_ID, null);
		String fileName = context.getResources().getString(R.string.favorites_file_name) + userId;
		ArrayList<Long> idList = getFavorites();

		for (Long id : ids) {

			if (isFavorite(id))
				idList.remove(id);
			else
				idList.add(id);

		}

		data.storeData(idList, fileName);
	}

	/**
	 * @return ArrayList<Long>
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Long> getFavorites() {
		String userId = new ManSession(context).getValues(ModConstants.KEY_ID, null);
		String fileName = context.getResources().getString(R.string.favorites_file_name) + userId;

		return (isInCache(fileName)) ? (ArrayList<Long>) data.getData(fileName) : new ArrayList<Long>();
	}

	/**
	 * @param id
	 */
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
