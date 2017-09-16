package com.kumailn.testapp;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.media.audiofx.LoudnessEnhancer;
import android.net.http.RequestQueue;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xe.xecdApiClient.config.XecdApiConfigBean;
import com.xe.xecdApiClient.exception.XecdApiException;
import com.xe.xecdApiClient.model.ConvertFromResponse;
import com.xe.xecdApiClient.service.XecdApiService;
import com.xe.xecdApiClient.service.XecdApiServiceFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;


import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {

    public static boolean currencySymbolDetected;
    public static boolean currencyCodeDetected;

    private XecdApiService apiService;

    ArrayList<String> c_codes = new ArrayList<String>(
            Arrays.asList("AFN", "ALL", "DZD", "USD", "EUR", "AOA", "XCD", "XCD", "ARS", "AMD", "AWG", "AUD", "EUR", "AZN", "BSD", "BHD", "BDT", "BBD", "BYR", "EUR", "BZD", "XOF", "BMD", "INR", "BOB", "BAM", "BWP",  "BRL",  "USD", "BND", "BGN", "XOF", "BIF", "KHR", "XAF", "CAD", "CVE", "USD", "KYD", "XAF", "XAF", "CLP", "CNY",   "COP", "KMF", "XAF",  "NZD", "CRC", "HRK", "CUP", "ANG", "EUR",  "XOF", "DKK", "DJF", "XCD", "DOP", "USD", "EGP", "USD", "XAF", "ERN", "EUR", "ETB", "FKP",  "FJD", "EUR", "EUR", "EUR", "XPF",  "XAF", "GMD", "GEL", "EUR", "GHS", "GIP", "EUR", "DKK", "XCD", "EUR", "USD", "GTQ", "GBP", "GNF", "XOF", "GYD", "USD",  "HNL",  "HUF", "ISK", "INR", "IDR", "IRR", "IQD", "EUR", "GBP", "ILS", "EUR", "JMD", "JPY", "GBP", "JOD", "KZT", "KES", "AUD", "KWD", "KGS", "LAK", "EUR", "LBP", "ZAR", "LRD", "LYD", "CHF", "EUR", "EUR", "MOP", "MKD", "MGA", "MWK", "MYR", "MVR", "XOF", "EUR", "USD", "EUR", "MRO", "MUR", "EUR", "MXN", "USD", "MDL", "EUR", "MNT", "EUR", "XCD", "MAD", "MZN", "MMK", "ZAR", "AUD", "NPR", "EUR", "XPF", "NZD", "NIO", "XOF", "NGN", "NZD", "AUD", "KPW", "USD", "NOK", "OMR", "PKR", "USD",  "USD", "PGK", "PYG", "PEN", "PHP", "NZD", "PLN", "EUR", "USD", "QAR", "RON", "RUB", "RWF", "EUR", "WST", "EUR", "SAR", "XOF", "RSD", "SCR", "SLL", "SGD", "ANG", "EUR", "EUR", "SBD", "SOS", "ZAR",  "KRW", "SSP", "EUR", "LKR", "EUR", "SHP", "XCD", "XCD", "EUR", "EUR", "XCD", "SDG", "SRD", "NOK", "SZL", "SEK", "CHF", "SYP", "STD",  "TJS", "TZS", "THB", "USD", "XOF", "NZD", "TOP", "TTD", "TND", "TRY", "TMT", "USD", "AUD",  "USD", "GBP", "USD", "UGX", "UAH", "AED", "UYU", "UZS", "VUV", "EUR", "VEF", "VND", "XPF", "MAD", "YER", "ZMW", "ZWL", "EUR"));
    private final String tag = "[[ClipboardWatcherService]] ";
    private ClipboardManager.OnPrimaryClipChangedListener listener = new ClipboardManager.OnPrimaryClipChangedListener() {
        public void onPrimaryClipChanged() {
            performClipboardCheck();
        }
    };


    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ClipboardManager) getSystemService(CLIPBOARD_SERVICE)).addPrimaryClipChangedListener(listener);
        parseJSON(21, 32);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        performClipboardCheck();

/*
        XecdApiConfigBean config = new XecdApiConfigBean();
        config.setAccountId("instituteofbusiness&technology450638369");
        config.setApiKey("48pnpljs0s6qqjcifmc2dap24q");
        apiService = XecdApiServiceFactory.createXecdAPIService(config);

        Double ii = Double.parseDouble("100");
        try {
            apiService.convertFrom("USD", "CAD", ii, false);
            ConvertFromResponse aaa = apiService.convertFrom("USD", "CAD", ii, false);
            String iii = aaa.getTerms();

            Log.e("XE_RESULT: ", iii);

        } catch (XecdApiException e) {
            e.printStackTrace();
        }*/


        Toast.makeText(this, "Works", Toast.LENGTH_SHORT).show();

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb.hasPrimaryClip()) {
            currencyCodeDetected = false;
            currencySymbolDetected = false;
            String currencySymbol = "";
            String currencyCode = "";
            ClipData cd = cb.getPrimaryClip();
            String clippedString = cd.getItemAt(0).getText().toString();
            Log.e("CLIPBOARD: ", cd.getItemAt(0).getText().toString());
            for (char item : clippedString.toCharArray()){
                //Checks for currency symbols
                if(Character.getType(item) == Character.CURRENCY_SYMBOL){
                    Log.e("CURRENCY_DETECTED: ", String.valueOf(item));
                    currencySymbol = String.valueOf(item);
                    currencySymbolDetected = true;
                }
                //TODO: Fix this, redundant looping
                else if(clippedString.toUpperCase().contains(" BTC ")){

                }
                else{
                    // Checks for currency codes
                    for(int i = 0; i < c_codes.size(); i++){
                        if((clippedString.toUpperCase().contains(" " + c_codes.get(i) + " "))){
                            //gets 3 Character currency code and gets corresponding symbol
                            Log.e("CODE_DETECTED: ", (c_codes.get(i)) + " " + Currency.getInstance((c_codes.get(i))).getSymbol());
                            currencyCodeDetected = true;
                            currencyCode = Currency.getInstance((c_codes.get(i))).getSymbol();
                        }
                    }
                }
            }

            if(currencySymbolDetected == true){
                String currencyValue = clippedString.replaceAll(" ", "");;
                currencyValue = currencyValue.replaceAll("[^\\d.]", "");
                Log.e("(S)VALUE: ", currencyValue);
            }
            else if(currencyCodeDetected == true){
                String currencyValue = clippedString.replaceAll(" ", "");
                currencyValue = currencyValue.replaceAll("[^\\d.]", "");
                Log.e("(C)VALUE: ", currencyValue);
            }
            if (cd.getDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                try {
                    //Toast.makeText(getApplicationContext(), cd.getItemAt(0).getText().toString(), Toast.LENGTH_SHORT).show();
                    //Log.e("CLIPBOARD: ", cd.getItemAt(0).getText().toString());
                    int a = 1;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service shutting down", Toast.LENGTH_SHORT).show();
    }

    com.android.volley.RequestQueue requestQueue;
    public void parseJSON(double lat, double lon){
        requestQueue = Volley.newRequestQueue(this);
        String jsonURL = "https://api.myjson.com/bins/1gjfk5";
        Log.e(String.valueOf(lat),String.valueOf(lon));
        Log.e(jsonURL, "");
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String aaa = response.getJSONArray("to").getJSONObject(0).getString("mid");

                            //JSONArray jsonArray = response.getJSONArray("name");
                            for (int i = 0; i < 400; i++){
                                String abc = response.getJSONArray("to").getJSONObject(i).getString("mid");
                                if(response.getJSONArray("to").getJSONObject(i).getString("quotecurrency").equals("CAD")){
                                    Log.e("VALUEFOUND: ", abc);
                                    break;
                                }
                            }
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

    }
}
