package com.g4.pcomm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.g4.pcomm.RPC.PCommRPC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Talkamynn on 7/26/2017.
 *
 * Displays main view.
 */

public class MainActivity extends AppCompatActivity {

    static final String LOG = "PComm - Main";
    Button BalanceBtn;
    Button CreatePotBtn;
    Button ViewPotsBtn;
    Button ModifProfileBtn;
    Button SettingsBtn;
    Button QuitBtn;

    TextView showUsername;

//    String username;
    String method;

    private PCommRPC pCommRPC = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

//        Intent intent = getIntent();
//        username = intent.getStringExtra("userName");

        BalanceBtn = (Button) findViewById(R.id.BalanceBtn);
        CreatePotBtn = (Button) findViewById(R.id.CreatePotBtn);
        ViewPotsBtn = (Button) findViewById(R.id.ViewPotsBtn);
        ModifProfileBtn = (Button) findViewById(R.id.ModifprofileBtn);
        SettingsBtn = (Button) findViewById(R.id.SettingsBtn);
        QuitBtn = (Button) findViewById(R.id.QuitBtn);

        showUsername = (TextView) findViewById(R.id.textViewUsername);
        showUsername.setText(prefs.getString("userName", "N/A"));

//        Log.d(LOG, "Username catched: " + username);
        Log.d(LOG, "User prefs catched: " +
                prefs.getString("userID", "N/A") + ", " +
                prefs.getString("userName", "N/A") + ", " +
                prefs.getString("userEmail", "N/A") + ", " +
                prefs.getString("firstName", "N/A") + ", " +
                prefs.getString("lastName", "N/A"));

        BalanceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent loginIntent;
//                loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(loginIntent);
//                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.joke_balance), Toast.LENGTH_SHORT).show();
            }
        });

        CreatePotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createPotIntent;
                createPotIntent = new Intent(getApplicationContext(), CreatePotActivity.class);
                startActivity(createPotIntent);
                finish();
//                Toast.makeText(getApplicationContext(), getString(R.string.joke_createpot), Toast.LENGTH_SHORT).show();
            }
        });

        ViewPotsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent;
                loginIntent = new Intent(getApplicationContext(), ConsultPotsActivity.class);
                startActivity(loginIntent);
                finish();
//                Toast.makeText(getApplicationContext(), getString(R.string.joke_viewpots), Toast.LENGTH_SHORT).show();

            }
        });

        ModifProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent loginIntent;
                loginIntent = new Intent(getApplicationContext(), ModifyProfileActivity.class);
                startActivity(loginIntent);
                finish();

//                method = "GetUsers";
//
//                Map<String, String> data = new HashMap<>();
//                data.put("user", getString(R.string.wsUser));
//                data.put("password", getString(R.string.wsPassword));
//                data.put("role", "admin");
//                Log.i(LOG, data.values().toString());
//
//                pCommRPC = new PCommRPC(MainActivity.this);
//                pCommRPC.execute(method, data);
            }
        });

        SettingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent loginIntent;
//                loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(loginIntent);
//                finish();
                Toast.makeText(getApplicationContext(), getString(R.string.joke_settings), Toast.LENGTH_SHORT).show();

            }
        });

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startingIntent;
                startingIntent = new Intent(getApplicationContext(), StartingActivity.class);
                Log.d(LOG, "Quiting activity...");
                startActivity(startingIntent);
                finish();
            }
        });
    }
}
