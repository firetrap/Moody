package managers;

import java.util.ArrayList;

import model.ModConstants;
import model.ObjectSearch;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import restPackage.MoodleServices;
import android.content.ContentResolver;
import android.content.Context;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

public class ManSearch {
	Context							context;
	ObjectSearch					search;
	private String					courseId;
	private String					courseName;
	private String					topicId;
	private String					topicName;

	private ArrayList<ObjectSearch>	result	= new ArrayList<ObjectSearch>();

	ContentResolver					cr;
	private ManSession				session;
	private String					userId;
	private ManDataStore			data;

	/**
	 * @param context
	 */
	public ManSearch(Context context) {
		this.context = context;
		// shared pref
		session = new ManSession(context);
		// Logged user id
		userId = session.getValues(ModConstants.KEY_ID, null);
		// Cache data store
		this.data = new ManDataStore(context);

	}

	/**
	 * @param query
	 */
	public void doMySearch(String query) {
		String fileName = MoodleServices.CORE_ENROL_GET_USERS_COURSES.name() + userId;

		// Get all user courses in cache
		MoodleCourse[] coursesArray = (MoodleCourse[]) data.getData(fileName);

		for (int i = 0; i < coursesArray.length; i++) {
			courseId = Long.toString(coursesArray[i].getId());
			courseName = coursesArray[i].getFullname();
			// If everything is normal the contents are already cached by the
			// main activity.
			String contentsFileName = MoodleServices.CORE_COURSE_GET_CONTENTS.name() + courseId + userId;
			MoodleCourseContent[] courseContent = (MoodleCourseContent[]) data.getData(contentsFileName);

			searchContents(courseContent, query);
		}
	}

	/**
	 * @param courseContent
	 * @param query
	 */
	private void searchContents(MoodleCourseContent[] courseContent, String query) {

		if (courseContent != null) {

			for (int i = 0; i < courseContent.length; i++) {
				topicId = Long.toString(courseContent[i].getId());
				topicName = courseContent[i].getName();
				if (courseContent[i].getName().contains(query) || courseContent[i].getSummary().contains(query)) {
					search = new ObjectSearch(courseId, courseName, topicId, topicName);
					result.add(search);

				}

				MoodleModule[] moodleModules = courseContent[i].getMoodleModules();
				searchModules(moodleModules, query);

			}
		}

	}

	/**
	 * @param moodleModules
	 * @param query
	 */
	private void searchModules(MoodleModule[] moodleModules, String query) {

		if (moodleModules != null) {

			for (int i = 0; i < moodleModules.length; i++) {

				if (moodleModules[i].getName().contains(query)) {
					search = new ObjectSearch(courseId, courseName, topicId, topicName);
					result.add(search);

				}

			}
		}

	}

	/**
	 * @return ArrayList<ObjectSearch> result
	 *
	 *         if is empty it will return null
	 */
	public ArrayList<ObjectSearch> getResults() {
		if (!result.isEmpty())
			return result;
		else
			return null;
	}

}
