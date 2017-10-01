package com.kumailn.clipbuddy;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.xe.xecdApiClient.service.XecdApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Currency;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import static com.kumailn.clipbuddy.MainActivity.defaultMethod;

public class MyService extends Service {
    public static int num_clips;
    public static boolean currencySymbolDetected;
    public static boolean currencyCodeDetected;
    public static boolean currencyDetected;
    public static boolean emailDetected;
    public static boolean phoneDetected;
    public static boolean webDetected;
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_URL_REGEX =
            Pattern.compile("[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)", Pattern.CASE_INSENSITIVE);
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
        }
*/

        Toast.makeText(this, "Welcome to Clip Buddy!", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if ((cb.hasPrimaryClip())) {
            //Switch all values to false/empty on each copy
            currencyCodeDetected = false;
            currencySymbolDetected = false;
            currencyDetected = false;
            emailDetected = false;
            phoneDetected = false;
            webDetected = false;
            String[] code_array = {"$", "€", "¥", "£"};
            String currencySymbol = "";
            String currencyCode = "";
            String email = "";
            String phoneNum = "";
            String phoneSend = "";
            String url = "";
            ClipData cd = cb.getPrimaryClip();

            String clippedString = cd.getItemAt(0).getText().toString();
            clippedString = String.copyValueOf(clippedString.toCharArray());
            String[] words = clippedString.split(" ");
            Log.e("CLIPBOARD: ", cd.getItemAt(0).getText().toString());

            //Detect if the clipped string has a currency code or symbol
            for (char item: clippedString.toCharArray()) {
                for (String code : code_array) {
                    if (String.valueOf(item).equals(code)) {
                        Log.e("Smbo ldetected", code);
                        currencySymbol = String.valueOf(item);
                        currencySymbolDetected = true;
                        currencyDetected = true;
                        switch (currencySymbol) {
                            case "$":
                                currencyCode = "USD";
                                break;
                            case "€":
                                currencyCode = "EUR";
                                break;
                            case "¥":
                                currencyCode = "JPY"; //rip chinese yuan
                                break;
                            case "£":
                                currencyCode = "GBP";
                                break;
                            case "﷼":
                                currencyCode = "IRR";
                                break;
                            case "₪":
                                currencyCode = "ILS";
                                break;
                            case "₫":
                                currencyCode = "VND";
                                break;
                            case "\u20BD":
                                currencyCode = "RUB";
                                break;
                            case "₩":
                                currencyCode = "KRW";
                                break;
                            default:
                                currencySymbolDetected = false; //symbol was found, but it's ambiguous/more than 1 possibility
                                currencyDetected = false;
                                break;
                        }
                        break;
                    }

                }
            }
                //Checks for currency codes first
            if (!currencyDetected) {
                for (int i = 0; i < c_codes.size(); i++) {
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
            if (clippedString.contains("@") || (clippedString.contains("at") && clippedString.contains("dot"))) {
                int atIndex = 0;
                //TODO: change dotIndices to a linkedList to allow for unlimited number of dots in an email
                int firstDotIndex = 0;
                int lastDotIndex = 0;
                for (int i = 0; i < words.length; i++) {
                    if (words[i].contains("@")) {
                        email = words[i];
                        break;
                    } else if (words[i].equals("at")) {
                        atIndex = i;
                    } else if (words[i].equals("dot")) {
                        if (firstDotIndex == 0) {
                            firstDotIndex = i;
                        } else {
                            lastDotIndex = i;
                        }
                    }
                }

                if (!clippedString.contains("@")) {
                    //if the email doesn't have an "@", but rather has words that need to be parsed
                    int firstWord;
                    int lastWord = 0;
                    if (atIndex < firstDotIndex) {
                        lastWord = firstDotIndex + 1;
                    } else {
                        lastWord = lastDotIndex + 1;
                    }

                    firstWord = (atIndex < firstDotIndex ? atIndex-1 : firstDotIndex-1 );
                    StringBuilder parseEmail = new StringBuilder();
                    for (int i = firstWord; i <= lastWord ; i++) {
                        if (words[i].equals("dot")) {
                            parseEmail.append(".");
                        } else if (words[i].equals("at")) {
                            parseEmail.append("@");
                        } else {
                            parseEmail.append(words[i]);
                        }
                    }
                    email = parseEmail.toString();
                }
                int i = 0;
                while (!Character.isAlphabetic(email.charAt(email.length() - i -1)) &&
                        !Character.isDigit(email.charAt(email.length() - i -1))) {
                    i++;
                }
                email = email.substring(0,email.length()-i);
                Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(email);
                if (matcher.find()) {
                    emailDetected = true;
                }
            }
            else if (!emailDetected) {
                for (String part : words) {
                    Matcher matcher = VALID_URL_REGEX.matcher(part);
                    if (matcher.find()) {
                        webDetected = true;
                        url = part;
                        break;
                    }
                }
                //TODO: further cleanse the matched URL, http/https, www, etc?
                //Matched URL may have any or none of those
            }
            //Check for phone number
            else if (!webDetected) {
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
                }
                if (phoneSend.length() == 10) {  //probably a local canadian/us number?
                    phoneNum = String.format("(%s) %s-%s", phoneSend.substring(0,3), phoneSend.substring(3,6),
                            phoneSend.substring(6,10));
                } else {
                    phoneNum = phoneSend;
                }
            }

            if (currencyDetected) {
                String currencyValue = clippedString.replaceAll(" ", "");
                currencyValue = currencyValue.replaceAll("[^\\d.]", "");
                if(currencySymbolDetected){
                    String resultConversion = parseJSON(currencyCode, currencyValue);
                    Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                    ii.putExtra("TYPE", "CURRENCY");
                    ii.putExtra("ORIGINAL", currencyValue);
                    //ii.putExtra("CONVERTED", String.valueOf(roundOff));
                    ii.putExtra("CODE", currencyCode);

                    if (num_clips > 0){
                        startService(ii);
                    }

                }
                else if(currencyCodeDetected){
                    // If code is available
                    Log.e("(C)VALUE: ", currencyValue);
                    String cc = currencyCode.replaceAll(" ", "");
                    Log.e("CODEIS: ", cc);
                    String resultConversion = parseJSON(currencyCode, currencyValue);
                    double roundOff = 0;
                    try{
                        roundOff = Math.round(Double.valueOf(resultConversion) * 100.0) / 100.0;
                    }catch (Exception e){}
                    Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                    ii.putExtra("TYPE", "CURRENCY");
                    ii.putExtra("ORIGINAL", currencyValue);
                    //ii.putExtra("CONVERTED", String.valueOf(roundOff));
                    ii.putExtra("CONVERTED", "NONE");
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
            else if(emailDetected) {
                Log.e("(E)VALUE: ", email);
                Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                ii.putExtra("TYPE", "EMAIL");
                ii.putExtra("EMAILADDRESS", email);
                if (num_clips > 0){
                    startService(ii);
                    stopService(new Intent(this, MyService.class));
                }
            }
            else if (webDetected) {
                Log.e("(W)VALUE: ", url);
                Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
                ii.putExtra("TYPE", "WEB");
                ii.putExtra("URL", url);
                if (num_clips > 0) {
                    startService(ii);
                }
            }
            else if (phoneDetected) {
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

    public int loadNumericInstance(){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        int myMethod = sharedPreferences.getInt("NUM", defaultMethod);
        return (myMethod);
    }

    public void saveNumericInstance(int newNum){
        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("NUM", newNum);
        editor.commit();
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
                                    conversion = conversion * 1.22;
                                    //Toast.makeText(getApplicationContext(), "Value: " + String.valueOf(conversion), Toast.LENGTH_SHORT).show();
                                    convertResult[0] = String.valueOf(conversion);
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
        return parseConvertResult;
    }
}
