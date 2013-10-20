package model;

public class ObjectSearch {
	String courseId;
	String courseName;
	String topicId;

	public ObjectSearch(String courseId, String courseName, String topicId) {
		this.courseId = courseId;
		this.courseName = courseName;
		this.topicId = topicId;

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

}
