package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyStartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("service", "O alarme iniciou o serviço");

		Intent service = new Intent(context, ServiceBackground.class);
		context.startService(service);
	}
}