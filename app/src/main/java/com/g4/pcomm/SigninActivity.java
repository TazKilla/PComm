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
import java.util.Locale;

import javax.crypto.SecretKey;

/**
 * Created by Talkamynn on 7/26/2017.
 *
 * Displays sign in view, handles data and runs async tasks to process signing.
 */

public class SigninActivity extends AppCompatActivity {

    static final String LOG = "PComm - Signing";
    // Instantiate Tools class
    static Tools toolbox = new Tools();

    Button SignInBtn;
    Button QuitBtn;

    EditText ETFirstName;
    EditText ETLastName;
    EditText ETUserName;
    EditText ETEmail;
    EditText ETPassword;
    EditText ETRePassword;

    String userID;
    String firstName;
    String lastName;
    String userName;
    String email;
    String password;
    String rePassword;
    String passwordHash;

    String method;
    String encryptedEmail;

    private PCommRPC pCommRPC = null;
    private String locale = Locale.getDefault().toString();

    BroadcastReceiver freeDataReceiver;
    BroadcastReceiver signInReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

        SignInBtn = (Button) findViewById(R.id.CreatePotBtn);
        QuitBtn = (Button) findViewById(R.id.CancelBtn);

        ETFirstName = (EditText) findViewById(R.id.editTextFirstname);
        ETLastName = (EditText) findViewById(R.id.editTextLastName);
        ETUserName = (EditText) findViewById(R.id.editTextUsername);
        ETEmail = (EditText) findViewById(R.id.editTextEmail);
        ETPassword = (EditText) findViewById(R.id.editTextPassword);
        ETRePassword = (EditText) findViewById(R.id.editTextRePassword);

        registerReceiver(freeDataReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    Log.d(LOG, "User name and email are available, go head with sign in process.");
                    signIn();
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

        registerReceiver(signInReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    unregisterReceiver(freeDataReceiver);
                    unregisterReceiver(signInReceiver);
                    HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>)intent.getSerializableExtra("data");
                    SharedPreferences.Editor prefEditor = prefs.edit();
                    prefEditor.clear();
                    userID = data.get("status").get("userID");
                    prefEditor.putString("userID", userID);
                    prefEditor.putString("userName", userName);
                    prefEditor.putString("userEmail", email);
                    prefEditor.putString("firstName", firstName);
                    prefEditor.putString("lastName", lastName);
                    prefEditor.putString("password", passwordHash);
                    prefEditor.apply();

                    Toast.makeText(getApplicationContext(), getString(R.string.toast_successsignin), Toast.LENGTH_LONG).show();
                    Log.d(LOG, "Successfully signing in, redirect to main view.");

                    Intent mainIntent;
                    mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                    mainIntent.putExtra("userName", userName);
                    startActivity(mainIntent);
                    finish();
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
        }, new IntentFilter("SignIn"));

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    unregisterReceiver(freeDataReceiver);
                    unregisterReceiver(signInReceiver);
                } catch (Exception e) {
                    Log.e(LOG, "Unknown exception: " + e);
                }
                Intent startingIntent;
                startingIntent = new Intent(getApplicationContext(), StartingActivity.class);
                Log.d(LOG, "Quiting activity...");
                startActivity(startingIntent);
                finish();
            }
        });

        SignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firstName = ETFirstName.getText().toString();
                lastName = ETLastName.getText().toString();
                userName = ETUserName.getText().toString();
                email = ETEmail.getText().toString();
                password = ETPassword.getText().toString();
                rePassword = ETRePassword.getText().toString();

                // Hash user password for security/privacy
                passwordHash = toolbox.MD5(password);
                // Encrypt user email address for security/privacy
                SecretKey secret;
                byte[] array;

                if (password.equals("") || rePassword.equals("")) { // Check if password and repassword exist
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordsempty), Toast.LENGTH_SHORT).show();
                } else if (!password.equals(rePassword)) { // Check if password and repassword are equal
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordsnotequal), Toast.LENGTH_SHORT).show();
                } else if (password.length() < 8) { // Check if password is larger than 7 chars
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordlength), Toast.LENGTH_SHORT).show();
                } else { // Check if selected user name and email are available
                    method = "CheckFreeData";

                    try {

                        secret = toolbox.generateKey(getString(R.string.secret));
                        array = toolbox.encryptMsg(email, secret);
                        Log.d(LOG, "Byte array: " + Arrays.toString(array));
                        encryptedEmail = Base64.encode(array);
                        Log.d(LOG, "Stringified array: " + encryptedEmail);
                        array = Base64.decode(encryptedEmail);
                        Log.d(LOG, "Rebuilded array: " + Arrays.toString(array));
                        Log.d(LOG, "Decrypted email: " + toolbox.decryptMsg(array, secret));

                        HashMap<String, String> data = new HashMap<>();
                        data.put("user", getString(R.string.wsUser));
                        data.put("password", getString(R.string.wsPassword));
                        data.put("user_name", userName);
                        data.put("email_address", encryptedEmail);
                        Log.i(LOG, data.values().toString());

                    pCommRPC = new PCommRPC(SigninActivity.this);
                    pCommRPC.execute(method, data);

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_passwordlength), Toast.LENGTH_LONG).show();
                        Log.d(LOG, "Error during data encryption: " + e);
                    }
                }
            }
        });
    }

    /**
     * If user name and email are free on database,
     * calls web service to proceed signin operation,
     * by sending user information.
     */
    private void signIn() {
        // Web service method to call
        method = "SignIn";

        HashMap<String, String> data = new HashMap<>();
        data.put("user", getString(R.string.wsUser));
        data.put("password", getString(R.string.wsPassword));
        data.put("first_name", firstName);
        data.put("last_name", lastName);
        data.put("user_name", userName);
        data.put("email_address", encryptedEmail);
        data.put("user_password", passwordHash);
        if (locale.equals("fr_FR") || locale.equals("fr_CA")) {
            data.put("currency", "Euro");
        } else {
            data.put("currency", "USDollar");
        }
        data.put("id_role", "2");
        Log.i(LOG, data.values().toString());

        pCommRPC = new PCommRPC(SigninActivity.this);
        pCommRPC.execute(method, data);
    }
}
