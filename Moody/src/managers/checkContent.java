package managers;

import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
import service.ServiceNotifications;

import com.android.moody.R;

import android.content.Context;

public class checkContent {
	ManDataStore data;
	Context context;
	String courseName;
	String courseId;
	ServiceNotifications notification;

	public checkContent(Context context, String courseName, String courseId) {
		this.context = context;
		this.courseName = courseName;
		this.courseId = courseId;
		this.data = new ManDataStore(context);
		this.notification = new ServiceNotifications(context, courseName,
				courseId);
	}

	public void hasNewContent(Object newCourse, String fileName) {

		MoodleCourseContent[] oldObj = (MoodleCourseContent[]) data
				.getData(fileName);
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
			notification.sendNotification("New topics in your course", "",
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

				notification.sendNotification("New contents in your course", topicId,
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
				notification.sendNotification("New contents in your course", topicId,
						R.id.MOODY_NOTIFICATION_ACTION_MODULE);
			}
		}
	}

}
