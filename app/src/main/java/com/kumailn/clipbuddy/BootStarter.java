package com.kumailn.clipbuddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootStarter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i2 = new Intent(context, MyService.class);
        Log.e("MyService: ", "Started on reboot");
        context.startService(i2);
    }
}
