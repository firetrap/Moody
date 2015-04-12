package model;

import java.io.Serializable;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

public class ObjectLatest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String courseId;
	String courseName;
	String topicId;
	String newContent;

	public ObjectLatest(String courseId, String courseName, String topicId,
			String newContent) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.topicId = topicId;
		this.newContent = newContent;
	}

	/**
	 * @return String courseId
	 */
	public String getCourseId() {
		return courseId;
	}

	/**
	 * @return String courseName
	 */
	public String getCourseName() {
		return courseName;
	}

	/**
	 * @return String topicId
	 */
	public String getTopicId() {
		return topicId;
	}

	/**
	 * @return String newContent
	 */
	public String getNewContent() {
		return newContent;
	}
}
