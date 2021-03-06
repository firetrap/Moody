package managers;

import java.util.LinkedList;

import model.ObjectLatest;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleModuleContent;
import service.ServiceNotifications;
import android.content.Context;

import com.firetrap.moody.R;

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
public class ManContentUpdate {
	ManDataStore data;
	Context context;
	String courseName;
	String courseId;
	ServiceNotifications notification;

	/**
	 * @param context
	 * @param courseName
	 * @param courseId
	 */
	public ManContentUpdate(Context context, String courseName, String courseId) {
		this.context = context;
		this.courseName = courseName;
		this.courseId = courseId;
		this.data = new ManDataStore(context);
		this.notification = new ServiceNotifications(context, courseName,
				courseId);
	}

	/**
	 * @param newCourse
	 * @param fileName
	 */
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
		// if (oldObj.length == newObj.length) {
		// TOPICS LOOP
		for (int i = 0; i < newObj.length; i++) {
			// CHECK IF THE TOPIC AREN'T NULL
			if (newObj[i] != null && oldObj[i] != null) {
				checkModules(Long.toString(newObj[i].getId()), oldObj, newObj,
						i);
			}
		}
		// } else {
		// // ELSE NEW TOPICS
		// if (new ManFavorites(context).isFavorite(Long.parseLong(courseId))) {
		// notification.sendNotification("New topics in your course", "",
		// R.id.MOODY_NOTIFICATION_ACTION_TOPIC);
		// }
		//
		// }
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
				setLatest(topicId, contentName);

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
				setLatest(topicId, contentName);
			}
		}
	}

	/**
	 * @param topicId
	 */
	@SuppressWarnings("unchecked")
	private void setLatest(String topicId, String newContent) {
		// Store the latest contents
		LinkedList<ObjectLatest> latestList;
		if (!data.isInCache("Latest")) {
			latestList = new LinkedList<ObjectLatest>();
			latestList.add(new ObjectLatest(courseId, courseName, topicId,
					newContent));
			new ManDataStore(context).storeData(latestList, "Latest");
		} else {
			latestList = (LinkedList<ObjectLatest>) data.getData("Latest");
			latestList.add(new ObjectLatest(courseId, courseName, topicId,
					newContent));
			new ManDataStore(context).storeData(latestList, "Latest");
		}
	}

}
