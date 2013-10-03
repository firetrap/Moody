package service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StartServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("MoodyService", "O alarme iniciou o serviço");

		Intent service = new Intent(context, ServiceBackground.class);
		context.startService(service);
	}

}