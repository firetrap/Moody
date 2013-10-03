package service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class Alarm extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, ServiceBackground.class));
	}

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
