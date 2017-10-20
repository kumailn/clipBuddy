package com.kumailn.clipbuddy;
import android.util.Log;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Class to handle all string parsing
public class StringParser {
    public static Pattern emailRegex  = Pattern.compile("[a-zA-z0-9~!#$%^&*\\-\\_\\.+=]+(\\.[a-zA-z0-9~!#$%^&*\\-\\_\\.+=]+)*@([a-zA-z0-9~!#$%^&*\\-\\_\\.+=]+([a-zA-Z0-9\\-]*)\\.)+[a-zA-z\\-]+");
    public static Pattern verboseEmailRegex = Pattern.compile("(?i)([a-zA-z0-9~!#$%^&*\\-\\_\\.+=]+ *(dot *[a-zA-z0-9~!#$%^&*\\-\\_\\.+=]+)* *at *[a-zA-z0-9~!#$%^&*\\-\\_\\.+=]+ +( *dot *[a-zA-z0-9]+)+)");
    public static Pattern phoneNumberRegex = Pattern.compile("\\s*(?:\\+?(\\d{1,3}))?[-. (]*(\\d{3})[-. )]*(\\d{3})[-. ]*(\\d{4})\\s*");
    public static Pattern urlRegex = Pattern.compile("(https:\\/\\/)?(http:\\/\\/)?(www)?[a-zA-Z0-9\\-]+(\\.[a-zA-Z0-9\\-\\/?=+]+)+");
    public static Pattern leftCurrencySymbolRegex = Pattern.compile("[$€¥£₪₫\u20BD₩֏৲৳৻૱௹฿៛￥￠₺₹₳₱₣] *[0-9\\.]+");
    public static Pattern rightCurrencySymbolRegex = Pattern.compile("[0-9\\,.]+ *[$€¥£₪₫\u20BD₩֏৲৳৻૱௹฿៛￥￠₺₹₳₱₣]");
    public static Pattern leftCurrencyVerboseRegex = Pattern.compile("(?i)(AFN|ALL|DZD|USD|EUR|AOA|XCD|XCD|ARS|AMD|AWG|AUD|EUR|AZN|BSD|BHD|BDT|BBD|BYR|EUR|BZD|XOF|BMD|INR|BOB|BAM|BWP|BRL|USD|BND|BGN|XOF|BIF|KHR|XAF|CAD|CVE|USD|KYD|XAF|XAF|CLP|CNY|COP|KMF|XAF|NZD|CRC|HRK|CUP|ANG|EUR|XOF|DKK|DJF|XCD|DOP|USD|EGP|USD|XAF|ERN|EUR|ETB|FKP|FJD|EUR|EUR|EUR|XPF|XAF|GMD|GEL|EUR|GHS|GIP|EUR|DKK|XCD|EUR|USD|GTQ|GBP|GNF|XOF|GYD|USD|HNL|HUF|ISK|INR|IDR|IRR|IQD|EUR|GBP|ILS|EUR|JMD|JPY|GBP|JOD|KZT|KES|AUD|KWD|KGS|LAK|EUR|LBP|ZAR|LRD|LYD|CHF|EUR|EUR|MOP|MKD|MGA|MWK|MYR|MVR|XOF|EUR|USD|EUR|MRO|MUR|EUR|MXN|USD|MDL|EUR|MNT|EUR|XCD|MAD|MZN|MMK|ZAR|AUD|NPR|EUR|XPF|NZD|NIO|XOF|NGN|NZD|AUD|KPW|USD|NOK|OMR|PKR|USD|USD|PGK|PYG|PEN|PHP|NZD|PLN|EUR|USD|QAR|RON|RUB|RWF|EUR|WST|EUR|SAR|XOF|RSD|SCR|SLL|SGD|ANG|EUR|EUR|SBD|SOS|ZAR|KRW|SSP|EUR|LKR|EUR|SHP|XCD|XCD|EUR|EUR|XCD|SDG|SRD|NOK|SZL|SEK|CHF|SYP|STD|TJS|TZS|THB|USD|XOF|NZD|TOP|TTD|TND|TRY|TMT|USD|AUD|USD|GBP|USD|UGX|UAH|AED|UYU|UZS|VUV|EUR|VEF|VND|XPF|MAD|YER|ZMW|ZWL|EUR){1} *[0-9\\.]+");
    public static Pattern rightCurrencyVerboseRegex = Pattern.compile("[0-9\\.]+ *(?i)(AFN|ALL|DZD|USD|EUR|AOA|XCD|XCD|ARS|AMD|AWG|AUD|EUR|AZN|BSD|BHD|BDT|BBD|BYR|EUR|BZD|XOF|BMD|INR|BOB|BAM|BWP|BRL|USD|BND|BGN|XOF|BIF|KHR|XAF|CAD|CVE|USD|KYD|XAF|XAF|CLP|CNY|COP|KMF|XAF|NZD|CRC|HRK|CUP|ANG|EUR|XOF|DKK|DJF|XCD|DOP|USD|EGP|USD|XAF|ERN|EUR|ETB|FKP|FJD|EUR|EUR|EUR|XPF|XAF|GMD|GEL|EUR|GHS|GIP|EUR|DKK|XCD|EUR|USD|GTQ|GBP|GNF|XOF|GYD|USD|HNL|HUF|ISK|INR|IDR|IRR|IQD|EUR|GBP|ILS|EUR|JMD|JPY|GBP|JOD|KZT|KES|AUD|KWD|KGS|LAK|EUR|LBP|ZAR|LRD|LYD|CHF|EUR|EUR|MOP|MKD|MGA|MWK|MYR|MVR|XOF|EUR|USD|EUR|MRO|MUR|EUR|MXN|USD|MDL|EUR|MNT|EUR|XCD|MAD|MZN|MMK|ZAR|AUD|NPR|EUR|XPF|NZD|NIO|XOF|NGN|NZD|AUD|KPW|USD|NOK|OMR|PKR|USD|USD|PGK|PYG|PEN|PHP|NZD|PLN|EUR|USD|QAR|RON|RUB|RWF|EUR|WST|EUR|SAR|XOF|RSD|SCR|SLL|SGD|ANG|EUR|EUR|SBD|SOS|ZAR|KRW|SSP|EUR|LKR|EUR|SHP|XCD|XCD|EUR|EUR|XCD|SDG|SRD|NOK|SZL|SEK|CHF|SYP|STD|TJS|TZS|THB|USD|XOF|NZD|TOP|TTD|TND|TRY|TMT|USD|AUD|USD|GBP|USD|UGX|UAH|AED|UYU|UZS|VUV|EUR|VEF|VND|XPF|MAD|YER|ZMW|ZWL|EUR){1}");

    public static String checkForURL(String inputString){
        ArrayList<String> searchResults = new ArrayList<String>();
        Matcher matcher = urlRegex.matcher(inputString);

        while(matcher.find()){
            searchResults.add(matcher.group().trim());
            Log.e("URLFound: " , matcher.group());
        }
        if(searchResults.size() >= 1){
            return searchResults.get(0);
        }
        return "false";
    }

    public static String checkForCurrencySymbol(String inputString){
        ArrayList<String> searchResultsLeft = new ArrayList<String>();
        ArrayList<String> searchResultsRight = new ArrayList<String>();

        Matcher left = leftCurrencySymbolRegex.matcher(inputString);
        Matcher right = rightCurrencySymbolRegex.matcher(inputString);

        while(left.find()){
            searchResultsLeft.add(left.group().trim());
            Log.e("LeftCurrencyFound: " , left.group());
        }
        while(right.find()){
            searchResultsLeft.add(right.group().trim());
            Log.e("RightCurrencyFound: " , right.group());
        }

        if(searchResultsLeft.size() >= 1){
            return searchResultsLeft.get(0);
        }
        else if(searchResultsRight.size() >= 1){
            return searchResultsRight.get(0);
        }
        return "false";
    }

    public static String checkForVerboseCurrency(String inputString){
        ArrayList<String> searchResultsLeft = new ArrayList<String>();
        ArrayList<String> searchResultsRight = new ArrayList<String>();

        Matcher left = leftCurrencyVerboseRegex.matcher(inputString);
        Matcher right = rightCurrencyVerboseRegex.matcher(inputString);

        while(left.find()){
            searchResultsLeft.add(left.group().trim());
            Log.e("LeftvCurrencyFound: " , left.group());
        }
        while(right.find()){
            searchResultsLeft.add(right.group().trim());
            Log.e("RightvCurrencyFound: " , right.group());
        }

        if(searchResultsLeft.size() >= 1){
            return searchResultsLeft.get(0);
        }
        else if(searchResultsRight.size() >= 1){
            return searchResultsRight.get(0);
        }
        return "false";
    }

    public static String checkForStandardEmail(String inputString){
        ArrayList<String> searchResults = new ArrayList<String>();
        Matcher matcher = emailRegex.matcher(inputString);

        while(matcher.find()){
            searchResults.add(matcher.group().trim().toLowerCase());
            Log.e("EmailFound: " , matcher.group().toLowerCase());
        }
        if(searchResults.size() >= 1){
            return searchResults.get(0);
        }
        return "false";
    }

    public static String checkForVerboseEmail(String inputString){
        ArrayList<String> searchResults = new ArrayList<String>();
        Matcher matcher = verboseEmailRegex.matcher(inputString);

        while(matcher.find()){
            searchResults.add(matcher.group().trim().toLowerCase());
            Log.e("verboseEmailFound: " , matcher.group().toLowerCase());
        }

        if(searchResults.size() >= 1){
            return searchResults.get(0);
        }
        return "false";
    }

    public static String checkForPhoneNumber(String inputString){
        ArrayList<String> searchResults = new ArrayList<String>();
        Matcher matcher = phoneNumberRegex.matcher(inputString);

        while(matcher.find()){
            searchResults.add(matcher.group().trim());
            Log.e("PhoneNumberFound: " , matcher.group());
        }
        if(searchResults.size() >= 1){
            return searchResults.get(0);
        }
        return "false";
    }

    public static String verboseEmailtoStandardEmail(String inputEmail){
        String email = (inputEmail);
        email = email.replaceAll("dot", ".");
        email = email.replaceAll("at", "@");
        email = email.trim();
        email = email.replaceAll(" ", "");
        return email;
    }

    public static String formatPhoneNumber(String inputNumber){
        String number = (inputNumber);
        number = number.replaceAll(" ", "");
        number = number.replaceAll("[^0-9]", "");

        return number;
    }

    public static String currencyFormater(String inputString, Boolean removeCurrency){
        String currencyString = (inputString);
        currencyString = currencyString.replaceAll(" ", "");
        if(removeCurrency){
            currencyString = currencyString.replaceAll("[^0-9\\.]", "");
        }
        return currencyString;
    }

    public static String extractCurrencySymbolOrCode(String inputString){
        inputString = inputString.replaceAll("[0-9\\.]", "");
        return inputString;
    }

    public static String convertCurrencySymboltoCode(String inputString){
        switch(inputString.trim()){
            case("$"):
                return "USD";
            case("€"):
                return "EUR";
            case("¥"):
                //RIP chinese Yuan
                return "YEN";
            case("£"):
                return "GBP";
            case("₪"):
                return "ILS";
            case("₫"):
                return "VND";
            case("\u20BD"):
                return "RUB";
            case("₩"):
                return "KRW";
            case("֏"):
                return "AMD";
            case("৲"):
                return "BDT";
            case("৳"):
                return "BDT";
            case("฿"):
                return "THB";
            case("￥"):
                return "YEN";
            case("￠"):
                return "USD";
            case("₺"):
                return "TRY";
            case("₹"):
                return "INR";
            case("₱"):
                return "PHP";
        }
        return "false";
    }

    public static String countryToCurrencyCode(String countryName){
        switch(countryName){
            case "Canada":
                return "CAD";
            case "United States":
                return "USD";
            case "Pakistan":
                return "PKR";
            case "India":
                return "INR";
            case "Thailand":
                return "THB";
            case "Vietnam":
                return "VND";
            case "Portugal":
                return "EUR";
            case "Spain":
                return "EUR";
            case "United Kingdom":
                return "GBP";
            case "Ireland":
                return "EUR";
            case "Netherlands":
                return "EUR";
            case "Denmark":
                return "DKK";
            case "Italy":
                return "EUR";
            case "Switzerland":
                return "CHF";
            case "Poland":
                return "PLN";
            case "Norway":
                return "NOK";
            case "Belgium":
                return "EUR";
            case "France":
                return "EUR";
            case "Sweden":
                return "SEK";
            case "Austria":
                return "ATS";
            case "Germany":
                return "EUR";
            case "Finland":
                return "EUR";
            case "Bangladesh":
                return "BDT";
            case "Buthan":
                return "BTN";
            case "China":
                return "CNY";
            case "Russia":
                return "RUB";
            case "Singapore":
                return "SGD";
            default:
                return "Error";


        }
    }
}