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
 * Created by Talkamynn on 19/10/2017.
 */

public class PotDetailsActivity extends AppCompatActivity {

    static final String LOG = "PComm - PotDetails";

    PCommRPC pCommRPC = null;

    LinearLayout LLElemList;

    Button QuitBtn;

    String method;
    String potId;
    String creator;

    TextView showUsername;

    BroadcastReceiver getDetailsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potdetails);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

        LLElemList = (LinearLayout) findViewById(R.id.LLSVElemList);

        Intent intent = getIntent();

        potId = intent.getStringExtra("pot_id");
        creator = intent.getStringExtra("creator");

        QuitBtn = (Button) findViewById(R.id.QuitBtn);

        showUsername = (TextView) findViewById(R.id.textViewUsername);
        showUsername.setText(prefs.getString("userName", "N/A"));

        registerReceiver(getDetailsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                unregisterReceiver(getDetailsReceiver);
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    HashMap<String, HashMap<String, String>> data = (HashMap<String, HashMap<String, String>>)intent.getSerializableExtra("data");
                    Log.d(LOG, "Successfully fetch pot details: " + data);
                    createElems(data);
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
        }, new IntentFilter("GetPotDetails"));

        getPotDetails();

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    unregisterReceiver(getDetailsReceiver);
                } catch (Exception e) {
                    Log.e(LOG, "Unknown exception: " + e);
                }
                Intent startingIntent;
                startingIntent = new Intent(getApplicationContext(), ConsultPotsActivity.class);
                Log.d(LOG, "Quiting activity...");
                startActivity(startingIntent);
                finish();
            }
        });
    }

    private void getPotDetails() {
        method = "GetPotDetails";

        Map<String, String> data = new HashMap<>();
        data.put("user", getString(R.string.wsUser));
        data.put("password", getString(R.string.wsPassword));
        data.put("pot_id", potId);
        Log.i(LOG, data.values().toString());

        pCommRPC = new PCommRPC(PotDetailsActivity.this);
        pCommRPC.execute(method, data);
    }

    private void createElems(HashMap<String, HashMap<String, String>> data) {

        int elemsNum = data.size();
        String elemName;
        String elemValue;

        for (int i = 1; i < elemsNum; i++) {
            elemName = data.get("entity_" + i).get("element_name");
            elemValue = data.get("entity_" + i).get("element_value");
            Log.d(LOG, "Element name: " + elemName);

            LinearLayout newPot = new LinearLayout(getApplicationContext());
            newPot.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
            Button potName = new Button(getApplicationContext());
            EditText potValue = new EditText(getApplicationContext());

            potName.setText(elemName);
            potName.setGravity(Gravity.CENTER_HORIZONTAL);
            potName.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 2f));

            potValue.setText(elemValue);
            potValue.setGravity(Gravity.CENTER_HORIZONTAL);
            potValue.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 3f));

            newPot.addView(potName);
            newPot.addView(potValue);

            LLElemList.addView(newPot);
        }
    }
}
