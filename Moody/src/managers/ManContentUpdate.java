package managers;

import java.util.LinkedList;

import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
import service.ServiceNotifications;
import android.content.Context;

import com.android.moody.R;

public class ManContentUpdate {
	ManDataStore data;
	Context context;
	String courseName;
	String courseId;
	ServiceNotifications notification;

	public ManContentUpdate(Context context, String courseName, String courseId) {
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
			if (new ManFavorites(context).isFavorite(Long.parseLong(courseId))) {
				notification.sendNotification("New topics in your course", "",
						R.id.MOODY_NOTIFICATION_ACTION_TOPIC);
			}

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
				if (new ManFavorites(context).isFavorite(Long
						.parseLong(courseId))) {
					notification.sendNotification(
							"New contents in your course", topicId,
							R.id.MOODY_NOTIFICATION_ACTION_MODULE);
				}
				setLatest(topicId);

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
				if (new ManFavorites(context).isFavorite(Long
						.parseLong(courseId))) {
					notification.sendNotification(
							"New contents in your course", topicId,
							R.id.MOODY_NOTIFICATION_ACTION_MODULE);
				}
				setLatest(topicId);
			}
		}
	}

	/**
	 * @param topicId
	 */
	private void setLatest(String topicId) {
		// Store the latest contents
		LinkedList<ManLatest> latestList;
		if (!data.isInCache("Latest")) {
			latestList = new LinkedList<ManLatest>();
			latestList.add(new ManLatest(courseId, courseName, topicId));
			new ManDataStore(context).storeData(latestList, "Latest");
		} else {
			latestList = (LinkedList<ManLatest>) data.getData("Latest");
			latestList.add(new ManLatest(courseId, courseName, topicId));
			new ManDataStore(context).storeData(latestList, "Latest");
		}
	}

}
