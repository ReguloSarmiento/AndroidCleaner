package com.clear.mastercleaner;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppDeleted extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		 String pack = intent.getDataString();
		 for(int i=0; i < ListAppActivity.apps.size(); i++){
			 if(ListAppActivity.apps.get(i).getPname().equals(pack.replace("package:", ""))){
				 ListAppActivity.apps.get(i).setDelete(true);
			 }
		 }
		
	     Intent broadcast = new Intent();
		 broadcast.setAction("QUITAR");
		 context.sendBroadcast(broadcast);
	}

}
