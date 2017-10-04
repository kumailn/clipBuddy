package com.kumailn.clipbuddy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class Float extends Service {

    private WindowManager wm;
    private LinearLayout ll;

    public Float() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        ll = new LinearLayout(this);
        LinearLayout.LayoutParams llParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    }
}
