package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

/**
 * @author firetrap
 *
 */
public class ServiceAlarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, ServiceBackground.class));
	}

	public void setAlarm(Context context) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		String alarm = Context.ALARM_SERVICE;
		AlarmManager am = (AlarmManager) context.getSystemService(alarm);

		Intent intent = new Intent("moody_get_data");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		// long interval = 1000 * 60 * 2;
		long interval = Long.parseLong(sharedPrefs.getString("sync_frequency",
				"63600000"));
		
		
		long triggerTime = SystemClock.elapsedRealtime() + interval;
		Log.d("MoodyService", Long.toString(interval / 60000) + " minutes");

		// At exact time
		// am.setRepeating(type, triggerTime, interval, pi);

		// At inexact time, but more battery friendly
		am.setInexactRepeating(type, triggerTime, interval, pi);

	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, ServiceAlarm.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

}
