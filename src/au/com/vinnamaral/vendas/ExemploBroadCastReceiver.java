package au.com.vinnamaral.vendas;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

public class ExemploBroadCastReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		Log.d("ExemploBroadCastReceiver", "1");
		
		if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){	
			Bundle bundle = intent.getExtras();
			
			Log.d("ExemploBroadCastReceiver", "2");

			if (bundle != null) {
				Log.d("ExemploBroadCastReceiver", "3");
				Object[] pdus = (Object[]) bundle.get("pdus");
				
				final SmsMessage[] messages = new SmsMessage[pdus.length];
				
				for (int i = 0; i < pdus.length; i++) {
					Log.d("ExemploBroadCastReceiver", "4");
					messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
				}

				if (messages.length > -1) {
					Log.d("ExemploBroadCastReceiver", "5");
					if (messages[0].getMessageBody().equals("replicar")) {
						Log.d("ExemploBroadCastReceiver", "6");
						Toast.makeText(context, "SMS Chegou e replicação será inicializada!", Toast.LENGTH_LONG).show();
						Intent it = new Intent("vendas.iniciar_servico");
						context.startService(it);
					}
				}
			}
		}
	}

}
