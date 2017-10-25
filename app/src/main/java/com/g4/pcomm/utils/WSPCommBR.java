package com.g4.pcomm.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Talkamynn on 30/09/2017.
 */

public class WSPCommBR extends BroadcastReceiver {

    private static String LOG = "PComm - WSPCommBR";

    @Override
    public void onReceive(Context context, Intent intent) {
//        String status = intent.getExtras().get("faultCode").toString();
//        if (status.equals("OK")) {
//            Log.d(LOG, "User name and email are available, go head with sign in process.");
//            this.signin();
//        } else {
//            String error = intent.getExtras().get("faultString").toString();
//            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
//        }
    }
}
