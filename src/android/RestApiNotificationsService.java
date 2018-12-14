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

import android.app.Service;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.ComponentName;
import android.content.Intent;

import android.os.Binder;
import android.os.IBinder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RestApiNotificationsService extends Service {
  public static final String APP_PREFERENCES = "settings";
  public static final String COTONTIDEV_REST_API_NOTY_URL = "cotontidev_restapinoty_url";
  private SharedPreferences mSettings;

  private String CFG_url;

  Handler mHandler = new Handler();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onStartCommand");

      mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
      CFG_url = mSettings.getString(COTONTIDEV_REST_API_NOTY_URL, "");

      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onStartCommand CFG_url" + CFG_url);

      mHandler.postDelayed(ToastRunnable, 5000);
      return START_STICKY;
  }

  @Override
	public void onCreate() {
      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onCreate");

      mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
      CFG_url = mSettings.getString(COTONTIDEV_REST_API_NOTY_URL, "");

      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onStartCommand CFG_url" + CFG_url);

		  mHandler.postDelayed(ToastRunnable, 5000);
	}

  @Override
  public IBinder onBind(Intent intent) {
      return null;
  }

  Runnable ToastRunnable = new Runnable() {
      public void run() {
          LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications ToastRunnable run + CFG_url" + CFG_url);

          /*
          JSONArray jsonTmpJson = jsonObject.getJSONArray("orders");
          data = new ArrayList<Map<String, Object>>(jsonTmpJson.length());
          ordersIds = new Integer[jsonTmpJson.length()];
          for (int i = 0; i < jsonTmpJson.length(); i++) {
              ordersIds[i] = jsonTmpJson.getJSONObject(i).getInt("ID");

              m = new HashMap<String, Object>();
              m.put("VODCHECK", jsonTmpJson.getJSONObject(i).getString("VODCHECK"));
              m.put("TITLE", jsonTmpJson.getJSONObject(i).getString("TITLE"));
              m.put("ROUTE", jsonTmpJson.getJSONObject(i).getString("ROUTE"));
              m.put("DATESTART", jsonTmpJson.getJSONObject(i).getString("DATESTART"));
              m.put("COST", jsonTmpJson.getJSONObject(i).getString("COST"));
              data.add(m);
          }
          */

          try {
              HttpURLConnection connect = (HttpURLConnection) new URL(CFG_url).openConnection();
              connect.setRequestMethod("GET");
              connect.setRequestProperty("Content-length", "0");
              connect.setUseCaches(false);
              connect.setAllowUserInteraction(false);
              connect.connect();

              int status = connect.getResponseCode();

              LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications connect to" + CFG_url);

              switch (status) {
                  case 200:
                  case 201:
                      BufferedReader br = new BufferedReader(new InputStreamReader(
                              connect.getInputStream(), "UTF-8"));
                      StringBuilder sb = new StringBuilder();
                      String line;
                      while ((line = br.readLine()) != null) {
                          sb.append(line + "\n");
                      }
                      br.close();

                      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications response" + sb.toString());

                      //JSONObject jsonObject = new JSONObject(sb.toString());

                      Context context = getApplicationContext();
                      NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                      NotificationCompat.Builder mBuilder =
                                  new NotificationCompat.Builder(context)
                                          .setSmallIcon(context.getApplicationInfo().icon)
                                          .setWhen(System.currentTimeMillis())
                                          .setContentTitle("It works!")
                                          .setTicker("Ticker")
                                          .setContentText("Text")
                                          .setNumber(1)
                                          .setAutoCancel(true);

                      mNotificationManager.notify("App Name", 228, mBuilder.build());
              }
          } catch (MalformedURLException ex) {
              ex.printStackTrace();
          } catch (IOException ex) {
              // return false;
          }

          mHandler.postDelayed( ToastRunnable, 10000);
      }
  };
}
