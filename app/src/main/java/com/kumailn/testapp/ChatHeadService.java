package com.kumailn.testapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.IntDef;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.concurrent.TimeUnit;

import static android.R.attr.data;
import static android.R.attr.fingerprintAuthDrawable;
import static android.R.attr.textColorTertiary;
import static android.R.attr.width;
import static com.kumailn.testapp.R.attr.height;

public class ChatHeadService extends Service {
    private WindowManager mWindowManager;
    private View mChatHeadView;
    private boolean isActivited = false;
    String[] convertResult = new String[1];
    public static String currencyCodeTwo;
    public static String parseConvertResult;
    public static String defaultMethod = "";
    TextView primaryTV;
    TextView secondaryTV;
    ImageView firstTabIV;
    ImageView secondTabIV;
    ImageView thirdTabIV;
    RelativeLayout relativetabLayout;
    RelativeLayout relativeLayout;
    LinearLayout buttonLL;
    RelativeLayout closeButton;
    TextView currencyYen;
    String temp = "";


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
        params.y = 0;

        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mChatHeadView, params);

        relativeLayout = (RelativeLayout) mChatHeadView.findViewById(R.id.container_rl);
        primaryTV = (TextView) mChatHeadView.findViewById(R.id.primary_tv);
        secondaryTV = (TextView) mChatHeadView.findViewById(R.id.secondary_tv);
        firstTabIV = (ImageView) mChatHeadView.findViewById(R.id.tab1);
        secondTabIV = (ImageView) mChatHeadView.findViewById(R.id.tab2);
        thirdTabIV = (ImageView) mChatHeadView.findViewById(R.id.tab3);
        relativetabLayout = (RelativeLayout) mChatHeadView.findViewById(R.id.tab3_rl);
        currencyYen = (TextView) mChatHeadView.findViewById(R.id.tab3_tv);

        buttonLL = (LinearLayout) mChatHeadView.findViewById(R.id.button_ll);
        closeButton = (RelativeLayout) mChatHeadView.findViewById(R.id.close_rl);
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

                        //stopSelf();
                        android.os.Process.killProcess(android.os.Process.myPid());

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
                            closeButton.setVisibility(View.VISIBLE);

                            Animation expandIn = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation);
                            relativeLayout.startAnimation(expandIn);
                        } else {
                            Animation expandOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_close);
                            relativeLayout.startAnimation(expandOut);
                            primaryTV.setVisibility(View.GONE);
                            secondaryTV.setVisibility(View.GONE);
                            buttonLL.setVisibility(View.GONE);
                            relativeLayout.setVisibility(View.GONE);
                            closeButton.setVisibility(View.GONE);
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
/*
        String converted_value = intent.getStringExtra("CONVERTED");
*/
        String currency_code = intent.getStringExtra("CODE");
        String email_address = intent.getStringExtra("EMAILADDRESS");
        String phone_number = intent.getStringExtra("PHONENUMBER");
        String converted_value = loadSavedConversion();

      /*  try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/


        if (data_type.equals("PHONE")){
            primaryTV.setText(phone_number);
            secondaryTV.setText("");
            firstTabIV.setBackgroundResource(R.drawable.ic_phone_white_24dp);
            secondTabIV.setBackgroundResource(R.drawable.ic_message_white_24dp);
            thirdTabIV.setBackgroundResource(R.drawable.ic_person_white_24dp);
            relativetabLayout.setBackgroundResource(R.drawable.button_tabs);
            currencyYen.setVisibility(View.GONE);
            thirdTabIV.setVisibility(View.VISIBLE);

            firstTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passPhoneIntent(phone_number);

                }
            });

            secondTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passMessagesIntent(phone_number);
                }
            });

            thirdTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passContactPhoneIntent(phone_number);
                }
            });
        }  else if(data_type.equals("EMAIL")){
            primaryTV.setText(email_address);
            secondaryTV.setText("");
            firstTabIV.setBackgroundResource(R.drawable.ic_email_white_24dp);
            secondTabIV.setBackgroundResource(R.drawable.ic_person_white_24dp);
            thirdTabIV.setBackgroundResource(R.drawable.ic_phone_white_24dp);
            currencyYen.setVisibility(View.GONE);
            thirdTabIV.setVisibility(View.VISIBLE);
            thirdTabIV.setEnabled(false);
            relativetabLayout.setBackgroundResource(R.drawable.buttons_tabs_disabled);

            firstTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passEmailIntent(email_address);

                }
            });

            secondTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    passContactEmailIntent(email_address);
                }
            });

        } else if(data_type.equals("CURRENCY")){
            //primaryTV.setText("CAD" + " " + converted_value);
            parseJSON(currency_code,(original_value));
            String str = String.valueOf(primaryTV.getText());
            str = str.replaceAll("[^\\d.]", "");
            converted_value = str;
            secondaryTV.setText(currency_code+" "+ original_value);
            firstTabIV.setBackgroundResource(R.drawable.ic_attach_money_white_24dp);
            secondTabIV.setBackgroundResource(R.drawable.ic_euro_symbol_white_24dp);
            //thirdTabIV.setBackgroundResource(R.drawable.ic_euro_symbol_white_24dp);
            currencyYen.setVisibility(View.VISIBLE);
            thirdTabIV.setVisibility(View.GONE);
            relativetabLayout.setBackgroundResource(R.drawable.button_tabs);

            String finalConverted_value = converted_value;
            firstTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to USD
                    //TODO: FIX THIS USD
                    String str = String.valueOf(finalConverted_value);
                    str = str.replaceAll("[^\\d.]", "");

                    String conRes = String.valueOf(Double.valueOf(str) * 0.82);
                    double roundOff = Math.round(Double.valueOf(conRes) * 100.0) / 100.0;
                    primaryTV.setText("USD " + String.valueOf(roundOff));
                }
            });

            String finalConverted_value1 = converted_value;
            secondTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to ERU
                    //TODO: FIX THIS USD
                    String str = String.valueOf(finalConverted_value1);
                    str = str.replaceAll("[^\\d.]", "");

                    String conRes = String.valueOf(Double.valueOf(str) * 0.69);
                    double roundOff = Math.round(Double.valueOf(conRes) * 100.0) / 100.0;
                    primaryTV.setText("EUR " + String.valueOf(roundOff));
                }
            });

            String finalConverted_value2 = converted_value;
            thirdTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //YEN
                    String str = String.valueOf(finalConverted_value2);
                    str = str.replaceAll("[^\\d.]", "");

                    String conRes = String.valueOf(Double.valueOf(str) * 90.89);
                    double roundOff = Math.round(Double.valueOf(conRes) * 100.0) / 100.0;
                    primaryTV.setText("YEN " + String.valueOf(roundOff));
                }
            });
        }



        return START_NOT_STICKY;
    }


        @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatHeadView != null) mWindowManager.removeView(mChatHeadView);
            Log.e("CHATSERVICE", "DESTROYED");
    }

    public void passContactPhoneIntent(String phoneNumber){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        if (intent.resolveActivity(getPackageManager()) != null) {
            this.startActivity(intent);
        }
        minimizeChatHead();
    }

    public void passContactEmailIntent(String email){
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        if (intent.resolveActivity(getPackageManager()) != null) {
            getApplicationContext().startActivity(intent);
        }
        minimizeChatHead();
    }

    public void passMessagesIntent(String phonenumber){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+phonenumber));
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        getApplicationContext().startActivity(sendIntent);
        minimizeChatHead();
    }

    public void passPhoneIntent(String phonenumber){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phonenumber));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        getApplicationContext().startActivity(intent);
        minimizeChatHead();
    }

    public void passEmailIntent(String emailAddress){
        Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, "");
        intent.setData(Uri.parse("mailto:" + emailAddress)); // or just "mailto:" for blank
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        getBaseContext().startActivity(intent);
        minimizeChatHead();
    }

    private void minimizeChatHead(){
        Animation expandOut = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_close);
        relativeLayout.startAnimation(expandOut);
        primaryTV.setVisibility(View.GONE);
        secondaryTV.setVisibility(View.GONE);
        buttonLL.setVisibility(View.GONE);
        relativeLayout.setVisibility(View.GONE);
        closeButton.setVisibility(View.GONE);

        isActivited = false;

    }


    com.android.volley.RequestQueue requestQueue;
    public String parseJSON(String code, String number){
        requestQueue = Volley.newRequestQueue(this);
        String jsonURL = "https://api.myjson.com/bins/1gjfk5";
        Log.e(jsonURL, "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        double conversion = 1;
                        try {
                            String aaa = response.getJSONArray("to").getJSONObject(0).getString("mid");

                            //JSONArray jsonArray = response.getJSONArray("name");
                            for (int i = 0; i < 400; i++){
                                String abc = response.getJSONArray("to").getJSONObject(i).getString("mid");
                                convertResult[0] = abc;
                                if(response.getJSONArray("to").getJSONObject(i).getString("quotecurrency").equals(code)){
                                    Log.e("VALUEFOUND: ", abc);
                                    conversion = Double.parseDouble(number) / Double.parseDouble(abc);
                                    temp = String.valueOf(conversion);
                                    Toast.makeText(getApplicationContext(), "ValueCH: " + String.valueOf(conversion), Toast.LENGTH_SHORT).show();
                                    convertResult[0] = String.valueOf(conversion);
                                    saveCoversion(String.valueOf(conversion));

                                    primaryTV.setText("CAD " + String.valueOf(roundThis(conversion)));
                                    Log.e("TEST_VAL: ", convertResult[0]);
                                    parseConvertResult = convertResult[0];
                                    break;
                                }
                            }
                            convertResult[0] = String.valueOf(conversion);
                            //Toast.makeText(MainActivity.this, "JSON WORKS", Toast.LENGTH_SHORT).show();
                            Log.e("JSONVOLLEY", aaa);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
        //Log.e("CHS: ", parseConvertResult);
        return "";
    }

    public String loadSavedConversion(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        String myMethod = sharedPreferences.getString("CON", defaultMethod);
        return (myMethod);
    }

    public void saveCoversion(String newNum){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CON", newNum);
        editor.commit();
    }

    public double roundThis(double num){
        return Math.round(num * 100.0) / 100.0;
    }

}
