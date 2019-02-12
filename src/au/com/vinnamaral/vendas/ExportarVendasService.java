package au.com.vinnamaral.vendas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ExportarVendasService extends Service implements Runnable {
	
	int totalDB, totalReplicado;

	public void onCreate() {
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
	    totalDB = (Integer) intent.getExtras().get("totalDB");
	    totalReplicado = (Integer) intent.getExtras().get("totalReplicado"); 
	    new Thread(ExportarVendasService.this).start();
	}

	@Override
	public void run() {

		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		Notification nt = null;

		if (totalDB == totalReplicado) {
			nt = new Notification(R.drawable.ic_launcher, "Status Replicação", System.currentTimeMillis());

			nt.flags |= Notification.FLAG_AUTO_CANCEL;

			PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this.getApplicationContext(), MainActivity.class), 0);

			nt.setLatestEventInfo(this, "Status Replicação", "A replicação foi feita com sucesso, total: " + totalReplicado, p);
		} else {
			nt = new Notification(R.drawable.ic_launcher, "Status Replicação", System.currentTimeMillis());

			nt.flags |= Notification.FLAG_AUTO_CANCEL;

			PendingIntent p = PendingIntent.getActivity(this, 0, new Intent(this.getApplicationContext(), MainActivity.class), 0);

			nt.setLatestEventInfo(this, "Status Replicação", "A replicação não foi feita com sucesso, total: " + totalReplicado + " de " + totalDB, p);
		}

		nt.vibrate = new long[] { 100, 2000, 1000, 2000 };

		notificationManager.notify((int) Math.round(Math.random()), nt);

		stopService(new Intent("vendas.iniciar_servico"));
	}

}
