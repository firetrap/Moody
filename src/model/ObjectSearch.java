package model;

public class ObjectSearch {
	String courseId;
	String courseName;
	String topicId;
	String topicName;

	public ObjectSearch(String courseId, String courseName, String topicId,
			String topicName) {
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
