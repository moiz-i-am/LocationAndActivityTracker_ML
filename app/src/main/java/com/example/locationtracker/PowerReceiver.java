package com.example.locationtracker;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.content.ContextCompat;

public class PowerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){


            Intent service = new Intent(context, MyService.class);
            context.startService(service);
        }

        if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {



            Log.d("TAG", "Screen Unlocked");
//            context.startService(new Intent(context, GetDataService.class));

            if (!isMyServiceRunning(MyService.class, context)){

                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {

                    context.startService(new Intent(context, MyService.class));
                }else {

                    Intent serviceIntent = new Intent(context, MyService.class);
                    ContextCompat.startForegroundService(context, serviceIntent);
                }
            }

        }
        /*Device is shutting down. This is broadcast when the device
         * is being shut down (completely turned off, not sleeping)
         * */
        if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {

            Log.d("TAG", "Phone SHUTDOWN");

        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {

            Log.d("TAG", "Screen Off");

        }
    }

//    public IntentFilter getFilter(){
//        final IntentFilter filter = new IntentFilter();
//        filter.addAction(Intent.ACTION_SCREEN_OFF);
//        filter.addAction(Intent.ACTION_SCREEN_ON);
//        return filter;
//    }

    private boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager)context. getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i("Service already","running");
                return true;
            }
        }
        Log.i("Service not","running");
        return false;
    }
}
