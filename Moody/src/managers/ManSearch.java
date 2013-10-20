package managers;

import java.util.ArrayList;

import model.ObjectSearch;
import restPackage.MoodleCourse;
import restPackage.MoodleCourseContent;
import restPackage.MoodleModule;
import android.content.ContentResolver;
import android.content.Context;

public class ManSearch {
	Context context;
	ManContents contents;
	ObjectSearch search;
	private String courseId;
	private String courseName;
	private String topicId;

	private ArrayList<ObjectSearch> result = new ArrayList<ObjectSearch>();

	ContentResolver cr;

	public ManSearch(Context context) {
		this.context = context;
		this.contents = new ManContents(context);

	}

	public void doMySearch(String query) {

		// Get all user courses
		MoodleCourse[] coursesArray = contents.getCourses();

		for (int i = 0; i < coursesArray.length; i++) {
			courseId = Long.toString(coursesArray[i].getId());
			courseName = coursesArray[i].getFullname();
			MoodleCourseContent[] courseContent = contents.getContent(Long
					.toString(coursesArray[i].getId()));

			searchContents(courseContent, query);
		}
	}

	private void searchContents(MoodleCourseContent[] courseContent,
			String query) {

		if (courseContent != null) {

			for (int i = 0; i < courseContent.length; i++) {
				topicId = Long.toString(courseContent[i].getId());
				if (courseContent[i].getName().contains(query)
						|| courseContent[i].getSummary().contains(query)) {
					search = new ObjectSearch(courseId, courseName, topicId);
					result.add(search);

				}

				MoodleModule[] moodleModules = courseContent[i]
						.getMoodleModules();
				searchModules(moodleModules, query);

			}
		}

	}

	private void searchModules(MoodleModule[] moodleModules, String query) {

		if (moodleModules != null) {

			for (int i = 0; i < moodleModules.length; i++) {

				if (moodleModules[i].getName().contains(query)) {
					search = new ObjectSearch(courseId, courseName, topicId);
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
