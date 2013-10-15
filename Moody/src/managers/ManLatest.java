package managers;

import java.io.Serializable;

public class ManLatest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String courseId;
	String courseName;
	String topicId;

	public ManLatest(String courseId, String courseName, String topicId) {
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
