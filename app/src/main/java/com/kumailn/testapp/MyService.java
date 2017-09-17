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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Currency;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

public class MyService extends Service {
    public static int num_clips;
    public static boolean currencySymbolDetected;
    public static boolean currencyCodeDetected;
    public static boolean currencyDetected;
    public static boolean emailDetected;
    public static boolean phoneDetected;
    String[] convertResult = new String[1];
    public static String currencyCodeTwo;
    public static String parseConvertResult;

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
        num_clips = 0;
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


        Toast.makeText(this, "Welcome to Clip Buddy!", Toast.LENGTH_SHORT).show();

        return START_STICKY;
        //return super.onStartCommand(intent, flags, startId);
    }
        //test git
    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb.hasPrimaryClip()) {
            //Switch all values to false/empty on each copy
            currencyCodeDetected = false;
            currencySymbolDetected = false;
            currencyDetected = false;
            emailDetected = false;
            phoneDetected = false;
            String currencySymbol = "";
            String currencyCode = "";
            String email = "";
            String phoneNum = "";
            String phoneSend = "";
            ClipData cd = cb.getPrimaryClip();
            String clippedString = cd.getItemAt(0).getText().toString();
            Log.e("CLIPBOARD: ", cd.getItemAt(0).getText().toString());

            //Detect if the clipped string has a currency symbol
            for (char item : clippedString.toCharArray()){
                //Checks for currency symbols
                if(Character.getType(item) == Character.CURRENCY_SYMBOL){
                    Log.e("CURRENCY_DETECTED: ", String.valueOf(item));
                    currencySymbol = String.valueOf(item);
                    //TODO: Andrew - add currencies
                    if(currencySymbol.equals("$")){
                        currencyCode = "USD";
                    }
                    currencySymbolDetected = true;
                    currencyDetected = true;
                }
                //TODO: Fix this, redundant looping
                else{
                    // Checks for currency codes
                    for(int i = 0; i < c_codes.size(); i++){
                        if((clippedString.toUpperCase().contains(" " + c_codes.get(i) + " ")) || (clippedString.toUpperCase().contains(" " + c_codes.get(i))) || (clippedString.toUpperCase().contains(c_codes.get(i) + " "))){
                            //gets 3 Character currency code and gets corresponding symbol
                            Log.e("CODE_DETECTED: ", (c_codes.get(i)) + " " + Currency.getInstance((c_codes.get(i))).getSymbol());
                            currencyCodeDetected = true;
                            currencyDetected = true;

                            currencyCodeTwo = Currency.getInstance((c_codes.get(i))).getSymbol();
                            currencyCode = (c_codes.get(i));
                        }
                    }
                }
                //Checks and (sort of) validates email
                if (clippedString.contains("@")) {
                    String[] parts = clippedString.split(" ");
                    for (String word : parts) {
                        if (word.contains("@")) {
                            email = word;
                            break;
                        }
                    }
                    if (email.substring(email.indexOf("@"), email.length()).contains(".")) {
                        emailDetected = true;
                    }
                }
                //Check for phone number
                else {
                //TODO: look at finding out where the call is going to? (international codes)
                //Takes the first block of non-letters. and looks for a sub-block of 7<n<15 numbers
                    phoneSend = clippedString.replaceAll(" ", "");
                    int i = 0;
                    while (i < phoneSend.length() && !Character.isDigit(phoneSend.charAt(i))) i++;
                    int j = i;
                    while (j < phoneSend.length() && !Character.isAlphabetic(phoneSend.charAt(j))) j++;
                    phoneSend = phoneSend.substring(i, j).replaceAll("[^\\d]", "");
                    if (phoneSend.length() > 7 && phoneSend.length() < 15) {
                        phoneDetected = true;
                    } else {
                        break;
                    }
                    if (phoneSend.length() == 10) {  //probably a local canadian/us number?
                        phoneNum = String.format("(%s) %s-%s", phoneSend.substring(0,3), phoneSend.substring(3,6),
                                phoneSend.substring(6,10));
                    } else {
                        phoneNum = phoneSend;
                    }
                }
            }
            if (currencyDetected) {
                String currencyValue = clippedString.replaceAll(" ", "");
                currencyValue = currencyValue.replaceAll("[^\\d.]", "");
                if(currencySymbolDetected){
                    Log.e("(S)VALUE: ", currencyValue);

                }
                else{
                    // If code is available
                    Log.e("(C)VALUE: ", currencyValue);
                    String cc = currencyCode.replaceAll(" ", "");
                    Log.e("CODEIS: ", cc);
                    String resultConversion = parseJSON(cc, currencyValue);
                    Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                    ii.putExtra("TYPE", "CURRENCY");
                    ii.putExtra("ORIGINAL", currencyValue);
                    ii.putExtra("CONVERTED", resultConversion);
                    ii.putExtra("CODE", cc);

                    if (num_clips > 0){
                        startService(ii);
                    }
                    try{
                        Log.e("CONVERTED: ", resultConversion);
                    }
                    catch (Exception e){}


                }

            }
            if(emailDetected) {
                Log.e("(E)VALUE: ", email);
                Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                ii.putExtra("TYPE", "EMAIL");
                ii.putExtra("EMAILADDRESS", email);
                if (num_clips > 0){
                    startService(ii);
                }
            }
            if (phoneDetected) {
                //phoneNum is the nicely formatted string to display on the screen for 10 digit numbers
                //e.g. (123) 456-7890
                //phoneSend is the string 1234567890 to send to android
                Log.e("(P)VALUE: ", phoneNum);
                Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                ii.putExtra("TYPE", "PHONE");
                ii.putExtra("PHONENUMBER", phoneNum);
                if (num_clips > 0){
                    startService(ii);
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

            num_clips += 1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service shutting down", Toast.LENGTH_SHORT).show();
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
                                if(response.getJSONArray("to").getJSONObject(i).getString("quotecurrency").equals("CAD")){
                                    Log.e("VALUEFOUND: ", abc);
                                    conversion = Double.parseDouble(number) / Double.parseDouble(abc);
                                    Toast.makeText(getApplicationContext(), "Value: " + String.valueOf(conversion), Toast.LENGTH_SHORT).show();
                                    convertResult[0] = String.valueOf(conversion);
                                    Log.e("TEST_VAL: ", convertResult[0]);
                                    parseConvertResult = String.valueOf(conversion);
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
        return parseConvertResult;
    }
}
