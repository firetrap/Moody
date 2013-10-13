package service;

import managers.ManDataStore;
import activities.MainActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.android.moody.R;

/**
 * @author firetrap
 * 
 */
public class ServiceNotifications {

	ManDataStore data;
	Context context;
	String courseName;
	String courseId;

	public ServiceNotifications(Context context, String courseName,
			String courseId) {
		this.context = context;
		this.courseName = courseName;
		this.courseId = courseId;
		this.data = new ManDataStore(context);
	}

	/**
	 * @param context
	 * @param contentText
	 * @param courseName
	 * @param courseId
	 * @param topicId
	 * @param actionModule
	 */
	public void sendNotification(String contentText, String topicId,
			int actionModule) {
		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		NotificationCompat.Builder builder = new NotificationCompat.Builder(
				context)
				.setSmallIcon(R.drawable.notification_icon)
				.setContentTitle("New contents")
				.setContentText(contentText)
				.setTicker("New contents in " + courseName)
				.setContentInfo(courseName)
				.setSound(
						Uri.parse(sharedPrefs.getString(
								"notifications_new_message_ringtone", "NULL")))
				.setAutoCancel(true);

		Intent notificationIntent = new Intent(context, MainActivity.class);
		notificationIntent.setFlags(actionModule);
		notificationIntent.putExtra("courseId", courseId);
		notificationIntent.putExtra("courseName", courseName);
		notificationIntent.putExtra("topicId", topicId);

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
				notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		builder.setContentIntent(contentIntent);

		// Add as notification
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(0, builder.build());
	}

	// Remove notification
	private void removeNotification(Context context) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(0);
	}

}
