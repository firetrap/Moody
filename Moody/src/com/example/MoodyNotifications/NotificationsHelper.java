/**
 * 
 */
package com.example.MoodyNotifications;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.example.moody.R;

/**
 * @author Sérgio Andaluz Ramos
 *
 */
public final class NotificationsHelper {
	
	// Atributos de sistema das notificações.
	private static Intent 									intent;
	private static PendingIntent							pendIntent;
	private static NotificationCompat.Builder				notB;
	private static NotificationManager 						notMgr;
	private static Notification 							not;
	private static Bitmap									notIco;
	private static TaskStackBuilder 						stackBuilder;
	
	//Gets dos atributos
	private static Intent 									getIntent() 			{ return intent; }
	private static PendingIntent 							getPendIntent() 		{ return pendIntent; }
	private static Notification								getNot() 				{ return not; }
	private static NotificationManager 						getNotMgr() 			{ return notMgr; }
	private static NotificationCompat.Builder 				getNotB() 				{ return notB; }
	private static Bitmap							 		getNotIco() 			{ return notIco; }
	private static TaskStackBuilder							getStackBuilder() 		{ return stackBuilder; }
	private static int										getLaunchIconCode()		{ return R.drawable.ic_launcher; }
	
	//Metodo que gera a notificação.
	public static final void notify(Context ctx, NotificationMessage msg)
	{		
		
		setSystemNotificationDefaults(ctx);
		setObjectNotification(ctx, msg);
		
		getNotMgr().notify(0, getNot());
		
	}
	
	//Metodo que inicializa os atributos do "objecto notificação".
	private static void setObjectNotification(Context ctx, NotificationMessage msg){
		
		getNotB().setTicker(msg.getTkr());
		getNotB().setContentTitle(msg.getTtl());
		getNotB().setContentText(msg.getMsg());
		getNotB().setSmallIcon(getLaunchIconCode());
		getNotB().setLargeIcon(getNotIco());
		getNotB().setAutoCancel(true);
		getNotB().setContentIntent(getPendIntent());
		
		not = getNotB().build();
		
	}
	
	//Metodo que inicializa os atributos de sistema das notificações
	private static void setSystemNotificationDefaults(Context ctx){
		
		intent			= new Intent(ctx, ctx.getClass());
		stackBuilder 	= TaskStackBuilder.create(ctx);
		
		/*
		 * bug fix
		 *importante estar nesta linha por causa do pendIntent...
		 **/
		getStackBuilder().addParentStack(ctx.getClass());
		getStackBuilder().addNextIntent(getIntent()); 
		
		notB 			= new NotificationCompat.Builder(ctx);
		pendIntent 		= getStackBuilder().getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		notMgr 			= (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		notIco			= BitmapFactory.decodeResource(ctx.getResources(), getLaunchIconCode());
		
	}
}
