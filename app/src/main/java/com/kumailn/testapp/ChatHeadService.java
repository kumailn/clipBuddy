package com.kumailn.testapp;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.width;
import static com.kumailn.testapp.R.attr.height;

public class ChatHeadService extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;
    private boolean isActivited = false;


    public ChatHeadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mChatHeadView = LayoutInflater.from(this).inflate(R.layout.chat_head, null);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 100;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        RelativeLayout relativeLayout = (RelativeLayout) mChatHeadView.findViewById(R.id.container_rl);
        TextView primaryTV = (TextView) mChatHeadView.findViewById(R.id.primary_tv);
        TextView secondaryTV = (TextView) mChatHeadView.findViewById(R.id.secondary_tv);
        LinearLayout buttonLL = (LinearLayout) mChatHeadView.findViewById(R.id.button_ll);
        RelativeLayout closeButton = (RelativeLayout) mChatHeadView.findViewById(R.id.close_rl);
        final RelativeLayout chatHeadImage = (RelativeLayout) mChatHeadView.findViewById(R.id.icon_rl);

        Animation floatingButton = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_open_button);
        chatHeadImage.setAnimation(floatingButton);


        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation expandOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_close);
                if (relativeLayout.getVisibility() == View.VISIBLE) {
                    relativeLayout.startAnimation(expandOut);
                }
                closeButton.startAnimation(expandOut);
                chatHeadImage.startAnimation(expandOut);

                expandOut.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        stopSelf();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });


                //close the service and remove the chat head from the window
                //stopSelf();
            }
        });

        chatHeadImage.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {


                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                    case MotionEvent.ACTION_DOWN:

                        break;

                    case MotionEvent.ACTION_UP:
                        if (!isActivited){
                            primaryTV.setVisibility(View.VISIBLE);
                            secondaryTV.setVisibility(View.VISIBLE);
                            buttonLL.setVisibility(View.VISIBLE);
                            relativeLayout.setVisibility(View.VISIBLE);

                            Animation expandIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation);
                            relativeLayout.startAnimation(expandIn);
                        } else {
                            Animation expandOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_close);
                            relativeLayout.startAnimation(expandOut);
                            primaryTV.setVisibility(View.GONE);
                            secondaryTV.setVisibility(View.GONE);
                            buttonLL.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.GONE);
                        }

                        isActivited = !isActivited;
                        break;

                    case MotionEvent.ACTION_MOVE:

                        break;
                }
                return true;
            }

        });

    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        //Types = EMAIL, CURRENCY, PHONE
        String data_type = intent.getStringExtra("TYPE");
        String original_value = intent.getStringExtra("ORIGINAL");
        String converted_value = intent.getStringExtra("CONVERTED");
        String currency_code = intent.getStringExtra("CODE");
        String email_address = intent.getStringExtra("EMAILADDRESS");
        String phone_number = intent.getStringExtra("PHONENUMBER");


        return START_NOT_STICKY;
    }


        @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
    }
}
