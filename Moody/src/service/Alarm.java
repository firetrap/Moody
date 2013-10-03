package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class Alarm {

	// // Restart service every x seconds,mins,hours
	// // Millisec * Second * Minute *hour
	// private static final long REPEAT_TIME = 1000 * 60;
	//
	// @Override
	// public void onReceive(Context context, Intent intent) {
	// AlarmManager service = (AlarmManager) context
	// .getSystemService(Context.ALARM_SERVICE);
	// Intent i = new Intent(context, StartServiceReceiver.class);
	// PendingIntent pending = PendingIntent.getBroadcast(context, 0, i,
	// PendingIntent.FLAG_CANCEL_CURRENT);
	// Calendar cal = Calendar.getInstance();
	// // Start 30 seconds after boot completed
	// cal.add(Calendar.SECOND, 30);
	// //
	// // Fetch every 30 seconds
	// // InexactRepeating allows Android to optimize the energy consumption
	// service.setInexactRepeating(AlarmManager.RTC_WAKEUP,
	// cal.getTimeInMillis(), REPEAT_TIME, pending);
	// Log.d("service", "Entrou no alarme");
	//
	// // service.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
	// // REPEAT_TIME, pending);
	//
	// }

	public void setAlarm(Context context) {
		String alarm = Context.ALARM_SERVICE;
		AlarmManager am = (AlarmManager) context.getSystemService(alarm);

		Intent intent = new Intent("moody_get_data");
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		int type = AlarmManager.ELAPSED_REALTIME_WAKEUP;
		// long interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
		long interval = 1000 * 60 * 2;
		long triggerTime = SystemClock.elapsedRealtime() + interval;

		am.setRepeating(type, triggerTime, interval, pi);

	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, Alarm.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

}
