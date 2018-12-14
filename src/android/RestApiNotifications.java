package ru.cotontidev.restapinotifications;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.apache.cordova.LOG;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.Intent;

/**
 * Cotontidev RestApiNotifications cordova plugin
 */
public class RestApiNotifications extends CordovaPlugin {
    public static final String APP_PREFERENCES = "settings";
    public static final String COTONTIDEV_REST_API_NOTY_URL = "cotontidev_restapinoty_url";
    private SharedPreferences mSettings;

    private String CFG_url;
    private JSONObject CFG_postdata;

    //@Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications execute");
        CFG_url = args.getString(0);
        CFG_postdata  = args.getJSONObject(1);

        LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications CFG_url " + CFG_url);
        LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications CFG_postdata " + CFG_postdata.toString());

        if (action.equals("init")) {
            this.init(callbackContext);
            return true;
        }
        return false;
    }

    private void init(CallbackContext callbackContext) {

        Context context = cordova.getActivity().getApplicationContext();

        mSettings = context.getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(COTONTIDEV_REST_API_NOTY_URL, CFG_url);
        editor.apply();

        Intent service = new Intent(context, RestApiNotificationsService.class);
        context.startService(service);

        callbackContext.success("success");
        //callbackContext.error("Expected one non-empty string argument.");
    }
}
