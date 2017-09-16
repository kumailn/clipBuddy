package com.kumailn.testapp;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.media.audiofx.LoudnessEnhancer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
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

public class MyService extends Service {

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

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        performClipboardCheck();


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
        }


        Toast.makeText(this, "Works", Toast.LENGTH_SHORT).show();

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }

    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb.hasPrimaryClip()) {
            boolean isCurrency = false;
            boolean isPhone = false;
            ClipData cd = cb.getPrimaryClip();
            String clippedString = cd.getItemAt(0).getText().toString();
            Log.e("CLIPBOARD: ", cd.getItemAt(0).getText().toString());
            for (char item : clippedString.toCharArray()){
                //Checks for currency symbols
                if(Character.getType(item) == Character.CURRENCY_SYMBOL){
                    Log.e("CURRENCY_DETECTED: ", String.valueOf(item));
                    isCurrency = true;
                }
                //TODO: Fix this, redundant looping
                else if(clippedString.toUpperCase().contains(" BTC ")){
                    isCurrency = true;
                }
                else if (!isCurrency){
                    // Checks for currency codes
                    for(int i = 0; i < c_codes.size(); i++){
                        if((clippedString.toUpperCase().contains(" " + c_codes.get(i) + " ") || clippedString.toUpperCase().contains(" " + c_codes.get(i)))){
                            //gets 3 Character currency code and gets corresponding symbol
                            Log.e("CODE_DETECTED: ", (c_codes.get(i)) + " " + Currency.getInstance((c_codes.get(i))).getSymbol());
                            isCurrency = true;
                        }
                    }
                }
                //Checking for phone number
                if (!isCurrency){
                  //First check for potential international country code
                  boolean international = false;
                  for (int i = 0; i < clippedString.length(); i++) {
                    if (clippedString.charAt(i) == 43) { // plus sign
                      international = true;
                    }
                  }
                  if (international) {

                    //TODO: Identify potential international area code, if int't code is +1, isInternational = false and break.

                  }
                  if (!international) {
                    //Check that the clipped string contains 10 digits of some sort (US and Canadian numbers)
                    String phoneString = clippedString.replaceAll("[^0-9]", "");
                    if (phoneString.length() == 10) {
                      Log.e("Phone number detected", phoneString);
                      //TODO: Call/add to contacts here
                    }
                  }
                }
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
}
