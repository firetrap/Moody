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
	String newContent;

	public ManLatest(String courseId, String courseName, String topicId,
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
