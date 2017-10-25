package com.g4.pcomm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.g4.pcomm.RPC.PCommRPC;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Talkamynn on 14/10/2017.
 */

public class ConsultPotsActivity extends AppCompatActivity {

    static final String LOG = "PComm - ConsultPots";

    LinearLayout LLPotList;

    Button QuitBtn;

    String method;

    TextView showUsername;
    String userId;

    int lastPotId;

    private PCommRPC pCommRPC = null;
    private BroadcastReceiver getPotsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultpots);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

        LLPotList = (LinearLayout) findViewById(R.id.LLSVPotList);

        QuitBtn = (Button) findViewById(R.id.QuitBtn);

        showUsername = (TextView) findViewById(R.id.textViewUsername);
        showUsername.setText(prefs.getString("userName", "N/A"));
        userId = prefs.getString("userID", "-1");

        lastPotId = R.id.TVAppTitle;

        registerReceiver(getPotsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(getPotsReceiver);
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>)intent.getSerializableExtra("data");
                    Log.d(LOG, "Successfully fetch data: " + data);
                    createPotsLink(data);
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
        }, new IntentFilter("GetPotsFromUserId"));

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    unregisterReceiver(getPotsReceiver);
                } catch (Exception e) {
                    Log.e(LOG, "Unknown exception: " + e);
                }
                Intent startingIntent;
                startingIntent = new Intent(getApplicationContext(), MainActivity.class);
                Log.d(LOG, "Quiting activity...");
                startActivity(startingIntent);
                finish();
            }
        });

        getPots(userId);
    }

    private void getPots(String userId) {
        method = "GetPotsFromUserId";

        Map<String, String> data = new HashMap<>();
        data.put("user", getString(R.string.wsUser));
        data.put("password", getString(R.string.wsPassword));
        data.put("user_id", userId);
        Log.i(LOG, data.values().toString());

        pCommRPC = new PCommRPC(ConsultPotsActivity.this);
        pCommRPC.execute(method, data);
    }

    private void createPotsLink(HashMap<String, HashMap<String, String>> data) {

        int potsNum = data.size();

        for (int i = 1; i < potsNum; i++) {
            String potName = data.get("entity_" + i).get("pot_name");
            final String potId = data.get("entity_" + i).get("pot_id");
            final String creator = data.get("entity_" + i).get("creator");
            Log.d(LOG, "Pot name: " + potName);

            Button newPot = new Button(getApplicationContext());
            newPot.setText(potName);
            newPot.setGravity(Gravity.CENTER_HORIZONTAL);
            newPot.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

            newPot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent startingIntent;
                    startingIntent = new Intent(getApplicationContext(), PotDetailsActivity.class);
                    startingIntent.putExtra("pot_id", potId);
                    startingIntent.putExtra("creator", creator);
                    Log.d(LOG, "Quiting activity...");
                    startActivity(startingIntent);
                    finish();
                }
            });

            LLPotList.addView(newPot);
        }
    }
}
