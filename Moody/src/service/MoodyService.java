/**
 * 
 */
package service;

import managers.Contents;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * @author firetrap
 * 
 */
public class MoodyService extends Service {

	public MoodyService() {
		// TODO Auto-generated constructor stub
	}

	private boolean isRunning = false;
	Object getContent;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// Announcement about starting
		Toast.makeText(this, "Starting the Demo Service", Toast.LENGTH_SHORT)
				.show();

		// Start a Background thread
		isRunning = true;
		Thread backgroundThread = new Thread(new BackgroundThread());
		backgroundThread.start();

		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Stop the Background thread
		isRunning = false;

		// Announcement about stopping
		Toast.makeText(this, "Stopping the Demo Service", Toast.LENGTH_SHORT)
				.show();
	}

	private class BackgroundThread implements Runnable {
		int counter = 0;

		public void run() {
			try {
				counter = 0;
				while (isRunning) {
					System.out.println("" + counter++);
					new Contents().getAll(getResources(),
							getApplicationContext());

					Thread.sleep(5000);
				}

				System.out.println("Background Thread is finished.........");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
