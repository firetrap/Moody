package model;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

public class ObjectSearch {
	String courseId;
	String courseName;
	String topicId;
	String topicName;

	public ObjectSearch(String courseId, String courseName, String topicId, String topicName) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.topicId = topicId;
		this.topicName = topicName;

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
	 * @return String topicName
	 */
	public String getTopicName() {
		return topicName;
	}
}
