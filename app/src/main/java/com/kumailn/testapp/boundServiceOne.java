package com.kumailn.testapp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class boundServiceOne extends Service {
    public boundServiceOne() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
