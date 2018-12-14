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

import java.util.Timer;
import java.util.TimerTask;

public class RestApiNotificationsService extends Service {
  public static final String APP_PREFERENCES = "settings";
  public static final String COTONTIDEV_REST_API_NOTY_URL = "cotontidev_restapinoty_url";
  private SharedPreferences mSettings;

  private String CFG_url;

  MyBinder binder = new MyBinder();
  Timer timer;
  TimerTask tTask;

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onStartCommand");

      mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
      CFG_url = mSettings.getString(COTONTIDEV_REST_API_NOTY_URL, "");

      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onStartCommand CFG_url" + CFG_url);

      timer = new Timer();
      schedule();
      return START_STICKY;
  }

  @Override
	public void onCreate() {
      super.onCreate();

      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onCreate");

      mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
      CFG_url = mSettings.getString(COTONTIDEV_REST_API_NOTY_URL, "");

      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications onStartCommand CFG_url" + CFG_url);

      timer = new Timer();
      schedule();
	}

  void schedule() {
      if (tTask != null) tTask.cancel();
          tTask = new TimerTask() {
              public void run() {
                  LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications schedule run + CFG_url" + CFG_url);

                  try {
                      HttpURLConnection connect = (HttpURLConnection) new URL(CFG_url).openConnection();
                      connect.setRequestMethod("GET");
                      connect.setRequestProperty("Content-length", "0");
                      connect.setUseCaches(false);
                      connect.setAllowUserInteraction(false);
                      connect.connect();

                      int status = connect.getResponseCode();

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

                              try{
                                  LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications response" + sb.toString());

                                  JSONObject jsonObject = new JSONObject(sb.toString());
                                  if(jsonObject.getString("status").equals("success"))
                                  {
                                    LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications response equals success");
                                    jsonObject = jsonObject.getJSONObject("data");
                                    if(jsonObject.getInt("success") == 1)
                                    {
                                      LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications response equals 1");
                                      JSONArray jsonTmpJson = jsonObject.getJSONArray("noty");
                                      if(jsonTmpJson.length() > 0) {
                                        LOG.d("TRUCKLOG", "TRICKLOG FCM RestApiNotifications response jsonTmpJson.length > 0");
                                        for (int i = 0; i < jsonTmpJson.length(); i++) {
                                          Context context = getApplicationContext();
                                          NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                                          NotificationCompat.Builder mBuilder =
                                                      new NotificationCompat.Builder(context)
                                                              .setSmallIcon(android.R.drawable.ic_dialog_email)//context.getApplicationInfo().icon)
                                                              .setWhen(System.currentTimeMillis())
                                                              .setContentTitle(jsonTmpJson.getJSONObject(i).getString("title"))
                                                              .setTicker("Ticker")
                                                              .setContentText(jsonTmpJson.getJSONObject(i).getString("text"))
                                                              .setNumber(jsonTmpJson.getJSONObject(i).getInt("id"))
                                                              .setAutoCancel(true);

                                          mNotificationManager.notify(jsonTmpJson.getJSONObject(i).getInt("id"), mBuilder.build());
                                        }
                                      }
                                    }
                                  }
                              } catch (JSONException e){
                                  e.printStackTrace();
                              }
                      }
                  } catch (MalformedURLException ex) {
                      ex.printStackTrace();
                  } catch (IOException ex) {
                     // return false;
                  }
              }
          };
          timer.schedule(tTask, 5000, 60000);
  }

  public IBinder onBind(Intent arg0) {
      return binder;
  }

  class MyBinder extends Binder {
    RestApiNotificationsService getService() {
        return RestApiNotificationsService.this;
    }
  }
}
