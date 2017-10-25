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

import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

/**
 * Created by groche on 7/26/2017.
 */

public class ModifyProfileActivity extends AppCompatActivity {

    static final String LOG = "PComm - Main";

    private PCommRPC pCommRPC = null;
    private Tools toolBox = new Tools();

    Button ModifprofileBtn;
    Button CancelBtn;

    EditText ETFirstName;
    EditText ETLastName;
    EditText ETUserName;
    EditText ETEmail;
    EditText ETPassword;
    EditText ETRePassword;

    String userId;
    String firstName;
    String lastName;
    String userName;
    String email;
    String password;

    String newFirstName;
    String newLastName;
    String newUserName;
    String newEmail;
    String newPassword;
    String newRePassword;

    String method;

    BroadcastReceiver freeDataReceiver;
    BroadcastReceiver modifyProfileReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifyprofile);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

        userId = prefs.getString("userID", "N/A");
        firstName = prefs.getString("firstName", "N/A");
        lastName = prefs.getString("lastName", "N/A");
        userName = prefs.getString("userName", "N/A");
        email = prefs.getString("userEmail", "N/A");
        password = prefs.getString("password", "N/A");

        ETFirstName = (EditText) findViewById(R.id.editTextFirstname);
        ETLastName = (EditText) findViewById(R.id.editTextLastName);
        ETUserName = (EditText) findViewById(R.id.editTextUsername);
        ETEmail = (EditText) findViewById(R.id.editTextEmail);
        ETPassword = (EditText) findViewById(R.id.editTextPassword);
        ETRePassword = (EditText) findViewById(R.id.editTextRePassword);

        ETFirstName.setText(firstName);
        ETLastName.setText(lastName);
        ETUserName.setText(userName);
        ETEmail.setText(email);

        ModifprofileBtn = (Button) findViewById(R.id.ModifProfileBtn);
        CancelBtn = (Button) findViewById(R.id.CancelBtn);

        registerReceiver(freeDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    unregisterReceiver(freeDataReceiver);
                    Log.d(LOG, "User name and email are available, go head with sign in process.");
//                    signIn();
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
        }, new IntentFilter("CheckFreeData"));

        ModifprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                method = "ModifyProfile";

                newFirstName = ETFirstName.getText().toString();
                newLastName = ETLastName.getText().toString();
                newUserName = ETUserName.getText().toString();
                newEmail = ETEmail.getText().toString();
                newPassword = ETPassword.getText().toString();
                newRePassword = ETRePassword.getText().toString();

                if (!newPassword.equals(password)) {
                    if (newPassword.equals("") || newRePassword.equals("")) { // Check if password and repassword exist
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordsempty), Toast.LENGTH_SHORT).show();
                    } else if (!newPassword.equals(newRePassword)) { // Check if password and repassword are equal
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordsnotequal), Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 8) { // Check if password is larger than 7 chars
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordlength), Toast.LENGTH_SHORT).show();
                    } else { // Check if selected user name and email are available
                        method = "CheckFreeData";
                        SecretKey secret;
                        byte[] array;
                        String encryptedEmail;

                        HashMap<String, String> data = new HashMap<>();
                        data.put("user", getString(R.string.wsUser));
                        data.put("password", getString(R.string.wsPassword));
                        if (!newUserName.equals(userName)) {
                            data.put("user_name", newUserName);
                        }
                        if (!newEmail.equals(email)) {
                            try {
                                secret = toolBox.generateKey(getString(R.string.secret));
                                array = toolBox.encryptMsg(newEmail, secret);
                                encryptedEmail = Base64.encode(array);
                                data.put("email_address", encryptedEmail);

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordlength), Toast.LENGTH_LONG).show();
                                Log.d(LOG, "Error during data encryption: " + e);
                            }
                        }
                        Log.i(LOG, data.values().toString());

                        pCommRPC = new PCommRPC(ModifyProfileActivity.this);
                        pCommRPC.execute(method, data);
                    }
                }

                Map<String, String> data = new HashMap<>();
                data.put("user", getString(R.string.wsUser));
                data.put("password", getString(R.string.wsPassword));
                if (!newFirstName.equals(firstName)) {
                    data.put("first_name", newUserName);
                }
                if (!newLastName.equals(lastName)) {
                    data.put("last_name", newLastName);
                }
                if (!newUserName.equals(userName)) {
                    data.put("user_name", newUserName);
                }
                if (!newEmail.equals(email)) {
                    data.put("email_address", newEmail);
                }
                if (!newPassword.equals(password)) {
                    data.put("user_password", newPassword);
                }
                Log.i(LOG, data.values().toString());

                pCommRPC = new PCommRPC(ModifyProfileActivity.this);
                pCommRPC.execute(method, data);

//                Intent loginIntent;
//                loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(loginIntent);
//                finish();

                Toast.makeText(getApplicationContext(), getString(R.string.joke_modifprofile), Toast.LENGTH_SHORT).show();
            }
        });

        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    unregisterReceiver(freeDataReceiver);
                } catch (Exception e) {
                    Log.e(LOG, "Unknown exception: " + e);
                }
                Intent loginIntent;
                loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(LOG, "Quiting activity...");
                startActivity(loginIntent);
                finish();
            }
        });
    }
}
