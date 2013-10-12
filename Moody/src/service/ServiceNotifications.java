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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.android.moody.R;

/**
 * @author firetrap
 * 
 */
public class ServiceNotifications {

	ManDataStore data = new ManDataStore();
	Context context;
	String courseName;
	String courseId;

	public ServiceNotifications(Context context, String courseName,
			String courseId) {
		this.context = context;
		this.courseName = courseName;
		this.courseId = courseId;
	}

	/**
	 * @param context
	 * @param contentText
	 * @param courseName
	 * @param courseId
	 * @param topicId
	 * @param actionModule
	 */
	private void sendNotification(String contentText, String topicId,
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

	public void hasNewContent(Object newCourse, String fileName) {

		MoodleCourseContent[] oldObj = (MoodleCourseContent[]) data.getData(
				context, fileName);
		MoodleCourseContent[] newObj = (MoodleCourseContent[]) newCourse;

		if (oldObj != null && newObj != null) {
			checkTopics(context, courseName, courseId, oldObj, newObj);
		}

	}

	/**
	 * @param context
	 * @param courseName
	 * @param courseId
	 * @param oldObj
	 * @param newObj
	 */
	private void checkTopics(Context context, String courseName,
			String courseId, MoodleCourseContent[] oldObj,
			MoodleCourseContent[] newObj) {
		if (oldObj.length == newObj.length) {
			// TOPICS LOOP
			for (int i = 0; i < newObj.length; i++) {
				// CHECK IF THE TOPIC AREN'T NULL
				if (newObj[i] != null && oldObj[i] != null) {
					checkModules(Long.toString(newObj[i].getId()), oldObj,
							newObj, i);
				}
			}
		} else {
			// ELSE NEW TOPICS
			sendNotification("New topics in your course", "",
					R.id.MOODY_NOTIFICATION_ACTION_TOPIC);

		}
	}

	/**
	 * @param context
	 * @param courseName
	 * @param courseId
	 * @param oldObj
	 * @param newObj
	 * @param i
	 */
	private void checkModules(String topicId, MoodleCourseContent[] oldObj,
			MoodleCourseContent[] newObj, int i) {
		MoodleModule[] oldModule = oldObj[i].getMoodleModules();
		MoodleModule[] newModule = newObj[i].getMoodleModules();
		// CHECK IF THE MODULES AREN'T NULL
		if (oldModule != null && newModule != null) {
			// MODULES LOOP
			if (oldModule.length == newModule.length) {
				for (int j = 0; j < newModule.length; j++) {
					checkContents(topicId, oldModule, newModule, j);
				}
			} else {
				String contentName = "";
				for (int j = 0; j < newModule.length; j++) {
					if (j >= oldModule.length) {
						contentName += newModule[j].getName() + "\n";
					}
				}

				sendNotification("New contents in your course", topicId,
						R.id.MOODY_NOTIFICATION_ACTION_MODULE);

			}
		}
	}

	/**
	 * @param context
	 * @param courseName
	 * @param topicId
	 * @param topicId
	 * @param oldModule
	 * @param newModule
	 * @param j
	 */
	private void checkContents(String topicId, MoodleModule[] oldModule,
			MoodleModule[] newModule, int j) {
		MoodleModuleContent[] oldContent = oldModule[j].getContent();
		MoodleModuleContent[] newContent = newModule[j].getContent();

		// CHECK IF THE CONTENTS AREN'T NULL
		if (oldContent != null && newContent != null) {
			// CONTENTS LOOP
			if (oldContent.length == newContent.length) {

			} else {
				String contentName = "";
				for (int k = 0; k < newContent.length; k++) {
					if (k >= oldContent.length) {
						contentName += newContent[k].getFilename() + "\n";
					}

				}
				sendNotification("New contents in your course", topicId,
						R.id.MOODY_NOTIFICATION_ACTION_MODULE);
			}
		}
	}

}
