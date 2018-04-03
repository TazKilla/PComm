package com.g4.pcomm.RPC;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.g4.pcomm.R;
import com.g4.pcomm.utils.Tools;
import com.g4.pcomm.utils.axmlrpc.XMLRPCClient;
import com.g4.pcomm.utils.axmlrpc.XMLRPCException;
import com.g4.pcomm.utils.axmlrpc.XMLRPCServerException;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Talkamynn on 13/09/2017.
 *
 * Contacts web services with input values and send back response, optionally data.
 *
 * Object contains two different parameters:
 *      params[0] String Method name for web service
 *      params[1] HashMap Method parameters
 *
 * Will send a broadcast containing:
 *      faultCode String Response status
 *      faultString String Error detail (if status is NOK)
 */

public class PCommRPC extends AsyncTask<Object, Integer, Boolean> {

    private static final String LOG = "PComm - PCommRPC";
//    Object used to store data from DB
    private HashMap<String, HashMap<String, String>> resultData;

    private ProgressDialog pDialog = null;

    private Context mContext;

    private String error = "";
    private String methodName = "";

    public PCommRPC (Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        // At start time, display dialog box
        if (mContext != null) {
            pDialog = new ProgressDialog(mContext);
            pDialog.setMessage(mContext.getString(R.string.message_processrequest));
            pDialog.show();
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (mContext != null) {
            Intent intent = new Intent(methodName);
            if (result) {
                pDialog.dismiss();
                intent.putExtra("faultCode", "OK");
                intent.putExtra("data", resultData);
                mContext.sendBroadcast(intent);
            } else {
                pDialog.dismiss();
                intent.putExtra("faultCode", "NOK");
                intent.putExtra("faultString", error);
                mContext.sendBroadcast(intent);
            }
        }
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        // Create object which will receive data
        Object[] response = new Object[1];
        methodName = params[0].toString();

        // Remote procedure call via HTTP (heavy process)
        try {
            Log.d(LOG, "Client creation...");
//            XMLRPCClient client = new XMLRPCClient(new URL("http://64.137.240.47/webservices/pcws_server.php"), XMLRPCClient.FLAGS_FORWARD);
            XMLRPCClient client = new XMLRPCClient(new URL("http://10.0.2.2/PComm_API/pcws_server.php"), XMLRPCClient.FLAGS_FORWARD);
            Log.d(LOG, "Client created.");

            Log.d(LOG, "Start to call WS...");
            Log.d(LOG, "Method: " + methodName + ", params: " + params[1]);
            response[0] = client.call(methodName, params[1]);
            String resp = Arrays.deepToString(response);
            Log.d(LOG, "Response to String = " + resp);

            Tools toolBox = new Tools();
            resultData = toolBox.stringToMap(resp);
            Log.d(LOG, "Formatted response: " + resultData.toString());

            if (resultData.get("status").get("faultCode").equals("OK")) {
                return true;
            } else {
                error = resultData.get("status").get("faultString");
                return false;
            }
        } catch(XMLRPCServerException ex) {
            // The server throw an error.
            error = "Server error: " + ex;
            Log.e(LOG, error);
            return false;
        } catch(XMLRPCException ex) {
            // An error occurred in the client.
            error = "Client error: " + ex;
            Log.e(LOG, error);
            return false;
        } catch(Exception ex) {
            // Any other exception
            error = "Unknown error: " + ex;
            Log.e(LOG, error);
            return false;
        }
    }

    @Override
    protected void onCancelled() {
        if (mContext != null) {
            Toast.makeText(mContext, "Login cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}