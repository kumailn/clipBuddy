package com.kumailn.testapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IFillFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.util.ArrayList;
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
    public static boolean usdButtonClicked = false;
    public static boolean eurButtonClicked = false;
    public static boolean yenButtonClicked = false;
    public static String usdValue = "";
    public static String eurValue= "";
    public static String yenValue = "";
    public static String oldCurrencyValue = "";
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

    LineChart chart;
    TextView xe_text;

    double[] cadToUSD = {0.7960,0.7947,0.7946,0.7950,0.7958,0.7984,0.8019,0.8012,0.8012,0.7999,0.7980,0.7924,0.7978,0.8075,0.8066,0.8066,0.8059,0.8082,0.8188,0.8226,0.8241,0.8226,0.8223,0.8246,0.8221,0.8186,0.8196,0.8191,0.8199};
    double[] cadToEUR = {0.6775,0.6758,0.6756,0.6724,0.6743,0.6761,0.6748,0.6720,0.6719,0.6679,0.6640,0.6660,0.6717,0.6801,0.6800,0.6800,0.6770,0.6788,0.6863,0.6858,0.6847,0.6835,0.6831,0.6891,0.6872,0.6888,0.6898,0.6858,0.6863};
    double[] cadToYEN = {87.1053,86.7879,86.7784,86.5170,86.8616,87.2843,87.5665,87.6022,87.6928,87.3543,87.1234,87.3990,87.9150,88.9885,88.9347,88.9333,88.3738,87.8836,89.2258,89.1636,88.8860,88.7094,88.6609,89.9702,90.4703,90.5865,90.7420,90.7823,90.8905};



    public ChatHeadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Does nothing, not a bounded service
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //Inflate the chatHeadView
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
        xe_text = (TextView)mChatHeadView.findViewById(R.id.xe_tv);
        buttonLL = (LinearLayout) mChatHeadView.findViewById(R.id.button_ll);
        closeButton = (RelativeLayout) mChatHeadView.findViewById(R.id.close_rl);
        final RelativeLayout chatHeadImage = (RelativeLayout) mChatHeadView.findViewById(R.id.icon_rl);

        Animation floatingButton = AnimationUtils.loadAnimation(getBaseContext(), R.anim.animation_open_button);
        chatHeadImage.setAnimation(floatingButton);

        chart = (LineChart) mChatHeadView.findViewById(R.id.chart);

        //OnClick listener for the close button
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
                        //Manually kill the process completely as soon as the animation ends
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                //close the service and remove the chat head from the window
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
        //Receive intent from MyService with instructions on what to do
        //Types = EMAIL, CURRENCY, PHONE
        String data_type = intent.getStringExtra("TYPE");
        String original_value = intent.getStringExtra("ORIGINAL");
        String currency_code = intent.getStringExtra("CODE");
        String email_address = intent.getStringExtra("EMAILADDRESS");
        String phone_number = intent.getStringExtra("PHONENUMBER");
        String converted_value = loadSavedConversion();

        //Action for phone number data type
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
        }

        //Action for Email data type
        else if(data_type.equals("EMAIL")){
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

        }
        //Action for currency data type
        else if(data_type.equals("CURRENCY")){
            //primaryTV.setText("CAD" + " " + converted_value);
            parseJSON(currency_code,(original_value));
            String str = String.valueOf(loadSavedConversion());
            //String str = String.valueOf(oldCurrencyValue);
            str = str.replaceAll("[^\\d.]", "");
            converted_value = str;
            Log.e("CHS: CONV", converted_value);
            secondaryTV.setText(currency_code+" "+ original_value);
            firstTabIV.setBackgroundResource(R.drawable.ic_attach_money_white_24dp);
            secondTabIV.setBackgroundResource(R.drawable.ic_euro_symbol_white_24dp);
            //thirdTabIV.setBackgroundResource(R.drawable.ic_euro_symbol_white_24dp);
            currencyYen.setVisibility(View.VISIBLE);
            thirdTabIV.setVisibility(View.GONE);
            relativetabLayout.setBackgroundResource(R.drawable.button_tabs);
            xe_text.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            relativeLayout.setLayoutParams(rel_btn);

            parseJSON(currency_code,(original_value));

            String finalConverted_value = converted_value;
            firstTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to USD
                    //TODO: FIX THIS USD
                    if (!usdButtonClicked) {
                        String str = String.valueOf(finalConverted_value);
                        str = str.replaceAll("[^\\d.]", "");

                        String conRes = String.valueOf(Double.valueOf(str) * 0.82);
                        double roundOff = Math.round(Double.valueOf(conRes) * 100.0) / 100.0;
                        primaryTV.setText("USD " + String.valueOf(roundOff));
                        usdButtonClicked = true;
                        usdValue = String.valueOf(roundOff);
                    }
                    else{
                        primaryTV.setText("USD " + usdValue);
                    }

                    chart.setVisibility(View.VISIBLE);
                    chart.setViewPortOffsets(0, 0, 0, 0);
                    chart.setBackgroundColor(Color.rgb(104, 241, 175));
                    chart.getDescription().setEnabled(false);
                    chart.setTouchEnabled(true);
                    chart.setDragEnabled(true);
                    chart.setScaleEnabled(true);
                    chart.setPinchZoom(false);
                    chart.setDrawGridBackground(false);
                    chart.setMaxHighlightDistance(300);

                    XAxis x = chart.getXAxis();
                    x.setEnabled(false);

                    YAxis y = chart.getAxisLeft();
                    y.setLabelCount(6, false);
                    y.setTextColor(Color.WHITE);
                    y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                    y.setDrawGridLines(false);
                    y.setAxisLineColor(Color.WHITE);

                    chart.getAxisRight().setEnabled(false);
                    setUSDData(cadToUSD);
                    chart.getLegend().setEnabled(false);

                    chart.animateXY(500, 500);
                    chart.invalidate();
                }
            });

            String finalConverted_value1 = converted_value;
            secondTabIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //to ERU
                    if (!eurButtonClicked) {
                        String str = String.valueOf(finalConverted_value1);
                        str = str.replaceAll("[^\\d.]", "");

                        String conRes = String.valueOf(Double.valueOf(str) * 0.69);
                        primaryTV.setText("EUR " + String.valueOf(roundThis(Double.valueOf(conRes))));
                        eurButtonClicked = true;
                        eurValue = String.valueOf(roundThis(Double.valueOf(conRes)));
                    }
                    else{
                        primaryTV.setText("EUR " + eurValue);

                    }

                    chart.setVisibility(View.VISIBLE);
                    chart.setViewPortOffsets(0, 0, 0, 0);
                    chart.setBackgroundColor(Color.rgb(104, 241, 175));
                    chart.getDescription().setEnabled(false);
                    chart.setTouchEnabled(true);
                    chart.setDragEnabled(true);
                    chart.setScaleEnabled(true);
                    chart.setPinchZoom(false);
                    chart.setDrawGridBackground(false);
                    chart.setMaxHighlightDistance(300);

                    XAxis x = chart.getXAxis();
                    x.setEnabled(false);

                    YAxis y = chart.getAxisLeft();
                    y.setLabelCount(6, false);
                    y.setTextColor(Color.WHITE);
                    y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                    y.setDrawGridLines(false);
                    y.setAxisLineColor(Color.WHITE);

                    chart.getAxisRight().setEnabled(false);
                    //setData(45, 100);
                    setUSDData(cadToEUR);
                    chart.getLegend().setEnabled(false);

                    chart.animateXY(500, 500);
                    chart.invalidate();
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

                    primaryTV.setText("YEN " + String.valueOf(roundThis(Double.valueOf(conRes))));
                }
            });

            currencyYen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //YEN
                    if (!yenButtonClicked) {
                        String str = String.valueOf(finalConverted_value1);
                        str = str.replaceAll("[^\\d.]", "");

                        String conRes = String.valueOf(Double.valueOf(str) * 90.89);
                        primaryTV.setText("YEN " + String.valueOf(roundThis(Double.valueOf(conRes))));
                        yenButtonClicked = true;
                        yenValue = String.valueOf(roundThis(Double.valueOf(conRes)));
                    }
                    else{
                        primaryTV.setText("YEN " +yenValue);
                    }

                    chart.setVisibility(View.VISIBLE);
                    chart.setViewPortOffsets(0, 0, 0, 0);
                    chart.setBackgroundColor(Color.rgb(104, 241, 175));
                    chart.getDescription().setEnabled(false);
                    chart.setTouchEnabled(true);
                    chart.setDragEnabled(true);
                    chart.setScaleEnabled(true);
                    chart.setPinchZoom(false);
                    chart.setDrawGridBackground(false);
                    chart.setMaxHighlightDistance(300);

                    XAxis x = chart.getXAxis();
                    x.setEnabled(false);

                    YAxis y = chart.getAxisLeft();
                    y.setLabelCount(6, false);
                    y.setTextColor(Color.WHITE);
                    y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                    y.setDrawGridLines(false);
                    y.setAxisLineColor(Color.WHITE);

                    chart.getAxisRight().setEnabled(false);
                    //setData(45, 100);
                    setUSDData(cadToYEN);
                    chart.getLegend().setEnabled(false);

                    chart.animateXY(500, 500);
                    chart.invalidate();
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

    //Method to pass intent with email data
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

    //Method to start intent with text message data
    public void passMessagesIntent(String phonenumber){
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:"+phonenumber));
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
        getApplicationContext().startActivity(sendIntent);
        minimizeChatHead();
    }

    //Method to start intent with phone data
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
                                    //Toast.makeText(getApplicationContext(), "ValueCH: " + String.valueOf(conversion), Toast.LENGTH_SHORT).show();
                                    convertResult[0] = String.valueOf(conversion);
                                    saveCoversion(String.valueOf(conversion * 1.22));
                                    //oldCurrencyValue = String.valueOf(conversion * 1.22);
                                    primaryTV.setText("CAD " + String.valueOf(roundThis(conversion * 1.22 )));
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

    //Method to round a number to two decimal places
    public double roundThis(double num){
        return Math.round(num * 100.0) / 100.0;
    }

    //Method to setup interactive graph with data
    private void setUSDData(double[] tempArray){
        float temp = 1;
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i=0; i<tempArray.length;i++){
            float current = (float) tempArray[i];
            temp = temp + 1;
            yVals.add(new Entry(temp,current));
        }
        LineDataSet set1;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)chart.getData().getDataSetByIndex(0);
            set1.setValues(yVals);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "DataSet 1");

            set1.setMode(LineDataSet.Mode.CUBIC_BEZIER);
            set1.setCubicIntensity(0.2f);
            //set1.setDrawFilled(true);
            set1.setDrawCircles(false);
            set1.setLineWidth(1.8f);
            set1.setCircleRadius(4f);
            set1.setCircleColor(Color.WHITE);
            set1.setHighLightColor(Color.rgb(244, 117, 117));
            set1.setColor(Color.WHITE);
            set1.setFillColor(Color.WHITE);
            set1.setFillAlpha(100);
            set1.setDrawHorizontalHighlightIndicator(false);
            set1.setFillFormatter(new IFillFormatter() {
                @Override
                public float getFillLinePosition(ILineDataSet dataSet, LineDataProvider dataProvider) {
                    return -10;
                }
            });

            // create a data object with the datasets
            LineData data = new LineData(set1);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            chart.setData(data);
        }

    }

    public String convertToCurrency(String rawStr){
        String str = String.valueOf(rawStr);
        str = str.replaceAll("[^\\d.]", "");
        return str;


    }

}
