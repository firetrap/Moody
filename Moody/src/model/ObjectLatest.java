package model;

import java.io.Serializable;

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
