package com.g4.pcomm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.g4.pcomm.RPC.PCommRPC;
import com.g4.pcomm.utils.Tools;
import com.g4.pcomm.utils.base64.Base64;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

/**
 * Created by Talkamynn on 7/26/2017.
 *
 * Displays login view, handles credentials and runs async task to process logging.
 */

public class LoginActivity extends AppCompatActivity {

    static final String LOG = "PComm - Logging";
    Tools toolbox = new Tools();

    Button LoginBtn;
    Button QuitBtn;
    EditText editTextUsername;
    EditText editTextPassword;

    String method;
    String password;
    // User prefs
    String userID;
    String firstName;
    String lastName;
    String userName;
    String userEmail;
    String passwordHash;

    private PCommRPC pCommRPC = null;

    BroadcastReceiver loginReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

        LoginBtn = (Button) findViewById(R.id.LoginBtn);
        QuitBtn = (Button) findViewById(R.id.CancelBtn);
        editTextUsername = (EditText) findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        registerReceiver(loginReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    unregisterReceiver(loginReceiver);
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    prefEditor.clear();
                    String rawData = intent.getExtras().get("data").toString();
                    HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>)intent.getSerializableExtra("data");
                    Log.d(LOG, "Raw data: " + rawData);
//                    HashMap<String, HashMap<String, String>> data = toolbox.stringToMap(rawData);
                    Log.d(LOG, "Successfully logged in: " + data);
                    try {
                        SecretKey secret = toolbox.generateKey(getString(R.string.secret));
                        String encryptedUserEmail = data.get("entity_1").get("email_address");
                        Log.d(LOG, "Encrypted email: " + encryptedUserEmail);
                        byte[] code = Base64.decode(encryptedUserEmail);
                        Log.d(LOG, "Byte array: " + Arrays.toString(code));

                        userEmail = toolbox.decryptMsg(code, secret);
                        Log.d(LOG, "Decrypted email: " + userEmail);
                        userID = data.get("entity_1").get("id");
                        firstName = data.get("entity_1").get("first_name");
                        lastName = data.get("entity_1").get("last_name");
                        prefEditor.putString("userID", userID);
                        prefEditor.putString("userName", userName);
                        prefEditor.putString("userEmail", userEmail);
                        prefEditor.putString("firstName", firstName);
                        prefEditor.putString("lastName", lastName);
                        prefEditor.putString("password", passwordHash);
                        prefEditor.apply();

                        Intent mainIntent;
                        mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                        mainIntent.putExtra("userName", userName);
                        startActivity(mainIntent);
                        finish();
                    } catch(Exception e) {
                        String error = "Unable to decode user email: " + e;
                        Log.e(LOG, error);
                        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String error = intent.getExtras().get("faultString").toString();
                    if (!status.equals("000")) {
                        try {
                            int resIDD = R.string.class.getField("wsError_" + error).getInt(null);
                            error = getString(resIDD);
                        } catch (Exception ex) {
                            error = ex.toString();
                        }
                    }
                    Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                }
            }
        }, new IntentFilter("LogIn"));

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    unregisterReceiver(loginReceiver);
                }catch (Exception e) {
                    Log.e(LOG, "Unknown exception: " + e);
                }
                Intent startingIntent;
                startingIntent = new Intent(getApplicationContext(), StartingActivity.class);
                Log.d(LOG, "Quiting activity...");
                startActivity(startingIntent);
                finish();
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                method = "LogIn";
                userName = editTextUsername.getText().toString();
                password = editTextPassword.getText().toString();
                Log.i(LOG, "Username catched: " + userName);
                Log.i(LOG, "Password catched: " + password);

                if (userName.equals("") || password.equals("")){

                    Toast.makeText(getApplicationContext(), getString(R.string.toast_nocredentials), Toast.LENGTH_LONG).show();
                } else {

                    passwordHash = toolbox.MD5(password);

                    Map<String, String> data = new HashMap<>();
                    data.put("user", getString(R.string.wsUser));
                    data.put("password", getString(R.string.wsPassword));
                    data.put("user_name", userName);
                    data.put("user_password", passwordHash);
                    Log.i(LOG, data.values().toString());

                    pCommRPC = new PCommRPC(LoginActivity.this);
                    pCommRPC.execute(method, data);
                }
            }
        });
    }
}
