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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Pattern;
import org.json.JSONException;
import org.json.JSONObject;
import static com.kumailn.clipbuddy.MainActivity.defaultMethod;

public class MyService extends Service {
    public static int num_clips;
    String[] convertResult = new String[1];
    public static String parseConvertResult;

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

        Toast.makeText(this, "Welcome to Clip Buddy!", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }
    private void performClipboardCheck() {
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if ((cb.hasPrimaryClip())) {
            ClipData cd = cb.getPrimaryClip();

            String clippedString = cd.getItemAt(0).getText().toString();
            clippedString = String.copyValueOf(clippedString.toCharArray());
            String[] words = clippedString.split(" ");
            Log.e("CLIPBOARD: ", cd.getItemAt(0).getText().toString());

            if(!(StringParser.checkForCurrencySymbol(clippedString).equals("false"))){
                String currencySymbolString = StringParser.checkForCurrencySymbol(clippedString);
                String currencyCode = StringParser.convertCurrencySymboltoCode(StringParser.extractCurrencySymbolOrCode(currencySymbolString));
                String currencyValue = StringParser.currencyFormater(currencySymbolString, true);
                Log.e(currencyValue, currencyCode + " " + StringParser.extractCurrencySymbolOrCode(currencySymbolString));
                intentFactory("CURRENCY", currencyValue, currencyCode);
            }
            else if(!(StringParser.checkForVerboseCurrency(clippedString).equals("false"))){
                String currencyCodeString = StringParser.checkForVerboseCurrency(clippedString);
                String currencyCode = StringParser.extractCurrencySymbolOrCode(currencyCodeString);
                String currencyValue = StringParser.currencyFormater(currencyCodeString, true);

                intentFactory("CURRENCY", currencyValue,     currencyCode.toUpperCase());
            }

            else if(!(StringParser.checkForStandardEmail(clippedString).equals("false"))){
                String standardEmailString = StringParser.checkForStandardEmail(clippedString);

                intentFactory("EMAIL", standardEmailString, "");
            }

            else if(!(StringParser.checkForVerboseEmail(clippedString).equals("false"))){
                String verboseEmailString = StringParser.checkForVerboseEmail(clippedString);
                String standardEmailString = StringParser.verboseEmailtoStandardEmail(verboseEmailString);

                intentFactory("EMAIL", standardEmailString, "");
            }

            else if(!(StringParser.checkForPhoneNumber(clippedString).equals("false"))){
                String phoneNumberString = StringParser.checkForPhoneNumber(clippedString);
                String standardPhoneNumer = StringParser.formatPhoneNumber(phoneNumberString);
                intentFactory("PHONE", standardPhoneNumer, "");
            }

            else if(!(StringParser.checkForURL(clippedString).equals("false"))){
                String urlString = StringParser.checkForURL(clippedString);
                intentFactory("WEB", urlString, "");
            }

            else{
                Log.e("Nothing found", "");
            }

            num_clips += 1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Service shutting down", Toast.LENGTH_SHORT).show();
    }

    public void intentFactory(String type, String relevantValue, String currencyCode){
        //Type = CURRENCY | PHONE | EMAIL | WEB
        Intent ii = new Intent(getApplicationContext(), ChatHeadService.class);
        ii.putExtra("TYPE", type);
        switch(type){
            case("CURRENCY"):
                ii.putExtra("CODE", currencyCode);
                ii.putExtra("ORIGINAL", relevantValue);
            case("PHONE"):
                ii.putExtra("PHONENUMBER", relevantValue);
            case("EMAIL"):
                ii.putExtra("EMAILADDRESS", relevantValue);
            case("WEB"):
                ii.putExtra("URL", relevantValue);
        }
        if (num_clips > 0){
            startService(ii);
        }
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
        //Log.e(jsonURL, "");
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
