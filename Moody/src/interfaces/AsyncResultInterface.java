package interfaces;

public interface AsyncResultInterface {

	void coursesAsyncTaskResult(Object result);

	void userAsyncTaskResult(Object result);

	abstract void courseContentsAsyncTaskResult(Object result);

	void createContactsAsyncTaskResult(Object result);

	void deleteContactsAsyncTaskResult(Object result);

	void blockContactsAsyncTaskResult(Object result);

	void unblockContactsAsyncTaskResult(Object result);

	void getContactsAsyncTaskResult(Object result);

	void sendInstanteMessageAsyncTaskResult(Object result);

	void pictureAsyncTaskResult(Object result);

}
