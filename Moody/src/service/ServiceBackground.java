/**
 * 
 */
package service;

import managers.ManContents;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * @author firetrap
 * 
 */
public class ServiceBackground extends Service {
	Alarm alarm = new Alarm();

	public ServiceBackground() {
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
		// Starts the alarm
		// alarm.SetAlarm(getApplicationContext());

		// Announcement about starting
		Toast.makeText(this, "Starting the Service", Toast.LENGTH_SHORT).show();

		// Start a Background thread
		isRunning = true;
		Thread backgroundThread = new Thread(new BackgroundThread());
		backgroundThread.start();

		// We want this service will continue running until all work it's done,
		// so return START_NOT_STICKY.
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Stop the Background thread
		isRunning = false;

	}

	private class BackgroundThread implements Runnable {

		public void run() {
			try {
				while (isRunning) {
					new ManContents().getAll(getResources(),
							getApplicationContext());
					isRunning = false;
					// Thread.sleep(5000);
				}
				stopSelf();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
