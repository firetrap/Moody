package service;

import managers.ManDataStore;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
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

	ManDataStore data = new ManDataStore();
	boolean notify = false;

	private void sendNotification(Context context) {
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
	private void removeNotification(Context context) {
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(0);
	}

	/**
	 * @param resources
	 * @param context
	 * @param newCourse
	 * @param fileName
	 */
	public void hasNewContent(Context context, Object newCourse, String fileName) {

		MoodleCourseContent[] oldObj = (MoodleCourseContent[]) data.getData(
				context, fileName);
		MoodleCourseContent[] newObj = (MoodleCourseContent[]) newCourse;

		if (oldObj != null && newObj != null) {
			notify = checkTopics(notify, oldObj, newObj);
		}
		if (notify == true) {
			sendNotification(context);
			notify = false;
		}
	}

	/**
	 * @param notify
	 * @param oldObj
	 * @param newObj
	 * @return
	 */
	private boolean checkTopics(boolean notify, MoodleCourseContent[] oldObj,
			MoodleCourseContent[] newObj) {
		if (oldObj.length == newObj.length) {
			// TOPICS LOOP
			for (int i = 0; i < newObj.length; i++) {
				// CHECK IF THE TOPIC AREN'T NULL
				if (newObj[i] != null && oldObj[i] != null) {
					notify = checkModules(notify, oldObj, newObj, i);
				}
			}
		} else {
			// ELSE NEW TOPICS
			notify = true;
		}
		return notify;
	}

	/**
	 * @param notify
	 * @param oldObj
	 * @param newObj
	 * @param i
	 * @return
	 */
	private boolean checkModules(boolean notify, MoodleCourseContent[] oldObj,
			MoodleCourseContent[] newObj, int i) {
		MoodleModule[] oldModule = oldObj[i].getMoodleModules();
		MoodleModule[] newModule = newObj[i].getMoodleModules();
		// CHECK IF THE MODULES AREN'T NULL
		if (oldModule != null && newModule != null) {
			// MODULES LOOP
			if (oldModule.length == newModule.length) {
				for (int j = 0; j < newModule.length; j++) {
					notify = checkContent(notify, oldModule, newModule, j);
				}
			} else {
				// ELSE NEW MODULES
				notify = true;
			}
		}
		return notify;
	}

	/**
	 * @param notify
	 * @param oldModule
	 * @param newModule
	 * @param j
	 * @return
	 */
	private boolean checkContent(boolean notify, MoodleModule[] oldModule,
			MoodleModule[] newModule, int j) {
		MoodleModuleContent[] oldContent = oldModule[j].getContent();
		MoodleModuleContent[] newContent = newModule[j].getContent();

		// CHECK IF THE CONTENTS AREN'T NULL
		if (oldContent != null && newContent != null) {
			// CONTENTS LOOP
			if (oldContent.length == newContent.length) {

			} else {
				// ELSE NEW CONTENTS
				notify = true;
			}
		}
		return notify;
	}

}
