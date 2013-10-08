package service;

import com.android.moody.R;

import activities.MainActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

public class ServiceNotifications {
	Context context;

	public ServiceNotifications(Context context) {
		this.context = context;
	}
	
	public void sendNotification() {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle("ServiceNotifications Example")
				.setContentText("This is a test notification")
				.setSound(
						Uri.parse(sharedPrefs.getString(
								"notifications_new_message_ringtone", "NULL")))
								.setAutoCancel(true);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);

		// Add as notification
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, builder.build());
	}

	// Remove notification
	private void removeNotification() {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(0);
	}
	
	
}
