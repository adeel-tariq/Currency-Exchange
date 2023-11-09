package com.example.currencycoverter.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import retrofit2.HttpException;

public class Utils {

    /**
     * checks for error type
     *
     * @param error throwable exception upon communication with server in presenter
     * @return string message or resource ID of message to be alerted to user
     */
    public static String errorType(Throwable error) {

        if (error instanceof SocketTimeoutException) {
            return "Problem connecting to server. Please check your internet connection and try again.";

        } else if (error instanceof IOException) {
            return "Problem connecting to server. Please check your internet connection and try again.";

        } else if (error instanceof IllegalStateException) {
            return "Problem fetching data, please update application and try again.";

        } else if (error instanceof JSONException) {
            return "Server error. Please try again later.";

        } else if (error instanceof HttpException) {
            Log.i("info", "HttpException");

            String responseBody = null;
            try {
                responseBody = Objects.requireNonNull(((HttpException) error).response().errorBody()).string();
                Log.i("info", "responseBody: " + responseBody + " statusCode " + ((HttpException) error).code());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            int statusCode = ((HttpException) error).code();
            if (statusCode >= 400 && statusCode < 500) {
                if (statusCode == 400) {
                    String message = getErrors(responseBody);
                    if (message.equalsIgnoreCase("Token is not active.")
                            || message.equalsIgnoreCase("Token invalid")
                            || message.equalsIgnoreCase("Token not provided")
                            || message.equalsIgnoreCase("User session not found")
                            || message.equalsIgnoreCase("null")
                            || message.equalsIgnoreCase("Unauthorised")
                            || message.equalsIgnoreCase("Your request is not authorized as token is invalid!")) {
                        return "-0";
                    } else {
                        return getErrors(responseBody);
                    }
                } else if (statusCode == 422) {
                    return getErrors(responseBody);
                } else if (statusCode == 401) {
                    String message = getErrors(responseBody);
                    if (message.contains("Token is not active.")
                            || message.contains("Token invalid")
                            || message.contains("Token not provided")
                            || message.contains("User session not found")
                            || message.equals("null")
                            || message.equals("Your request is not authorized as token is invalid!")) {
                        return "-0";
                    } else {
                        return getErrors(responseBody);
                    }
                } else if (statusCode == 405) {
                    return "Problem connecting to server. Please try again later.";
                } else if (statusCode == 404) {
                    return getErrors(responseBody);
                } else if (statusCode == 403) {
                    return getErrors(responseBody);
                } else if (statusCode == 417) {
                    return getErrors(responseBody);
                }
            } else {
                return "Problem connecting to server. Please try again later.";
            }
        }
        return "";
    }

    /**
     * retrieve message from response body
     *
     * @param responseBody json response body received by server
     * @return string message present in json
     */
    private static String getMessage(String responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody);
            if (jsonObject.has("response")) {
                return jsonObject.getJSONObject("response").getString("message").trim();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return "";
    }

    /**
     * retrieve messages from response body
     *
     * @param responseBody json response body received by server
     * @return string message/s present in json
     */
    private static String getErrors(String responseBody) {
        try {
            StringBuilder errors = new StringBuilder();
            JSONObject jsonObject = new JSONObject(responseBody);
            if (jsonObject.has("response")) {
                JSONObject responseObj = jsonObject.getJSONObject("response");
                if (responseObj.has("message")) {
                    if (responseObj.get("message") instanceof JSONArray) {
                        JSONArray messageObj = responseObj.getJSONArray("message");
                        for (int i = 0; i < messageObj.length(); i++) {
                            errors.append("- ").append(messageObj.get(i)).append("\n");
                        }
                    } else {
                        return responseObj.getString("message").trim();
                    }
                }
            } else if (jsonObject.has("message")) {
                return jsonObject.getString("message").trim();
            }
            return errors.toString().trim();
        } catch (JSONException e) {
            e.printStackTrace();
            return "Something went wrong, please try again later.";
        }
    }

    /**
     * noConnectionDialog.
     *
     * @param context the context
     */
    public static boolean connectionStatusOk(Context context) {

        boolean isConnected = false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            for (NetworkInfo networkInfo : info) {
                if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                    isConnected = true;
                }
            }
        }
        if (!isConnected) {
            info(context, "No Internet Connection", 4);
        }
        return isConnected;
    }

    public static double getRoundedValue(double value, int decimalPoint) {
        return BigDecimal.valueOf(value).setScale(decimalPoint, RoundingMode.HALF_UP).doubleValue();
    }

    public static void info(Context context, String message, int type) {
        if (type == 1)
            Toasty.success(context, message, Toasty.LENGTH_SHORT, true).show();
        else if (type == 2)
            Toasty.info(context, message, Toasty.LENGTH_SHORT, true).show();
        else if (type == 3)
            Toasty.warning(context, message, Toasty.LENGTH_SHORT, true).show();
        else if (type == 4)
            Toasty.error(context, message, Toasty.LENGTH_SHORT, true).show();
    }

    public static String getToday() {
        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MONTH, -1);
        Date date;
        date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getOneDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date date;
        date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getOneMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date date;
        date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    public static String getOneYear() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);
        Date date;
        date = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

}
