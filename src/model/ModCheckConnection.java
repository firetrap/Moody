package model;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

public class ModCheckConnection {

	private Context context;

	public ModCheckConnection(Context context) {
		this.context = context;
	}

	/**
	 * Check if has Internet connection
	 *
	 * @return true or false
	 */
	public boolean hasConnection() {
		ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		android.net.NetworkInfo wifi = connec.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		android.net.NetworkInfo mobile = connec.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		// Here if condition check for wifi and mobile network is available or
		// not.
		// If anyone of them is available or connected then it will return true,
		// otherwise false;

		if (wifi.isConnected() || mobile.isConnected()) {
			return true;
		}
		return false;
	}

}
