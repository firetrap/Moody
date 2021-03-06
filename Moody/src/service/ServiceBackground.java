package service;

import managers.ManContents;
import managers.ManSession;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * License: This program is free software; you can redistribute it and/or modify
 * it under the terms of the dual licensing in the root of the project
 * This program is distributed in the hope that it will be
 * useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Dual Licence
 * for more details. Fábio Barreiros - Moody Founder
 */

/**
 * @author firetrap
 * 
 */
public class ServiceBackground extends Service {
	ServiceAlarm alarm = new ServiceAlarm();

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
//		android.os.Debug.waitForDebugger();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		if (new ManSession(getApplicationContext()).isLoggedIn()) {

			super.onStartCommand(intent, flags, startId);
			// Starts the alarm
			alarm.setAlarm(getApplicationContext());

			// Announcement about starting
			Log.d("MoodyService", "Service Started");

			// Start a Background thread
			isRunning = true;
			Thread backgroundThread = new Thread(new BackgroundThread());
			backgroundThread.start();
		}

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// Stop the Background thread
		isRunning = false;

	}

	private class BackgroundThread implements Runnable {

		@Override
		public void run() {
			try {
				while (isRunning) {
					Log.d("MoodyService", "entrou na thread");
					new ManContents(getApplicationContext()).refresh();
					isRunning = false;
				}
				stopSelf();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
