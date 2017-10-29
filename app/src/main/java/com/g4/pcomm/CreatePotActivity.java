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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.g4.pcomm.RPC.PCommRPC;
import com.g4.pcomm.utils.Tools;

import java.util.HashMap;

/**
 * Created by Talkamynn on 02/10/2017.
 */

public class CreatePotActivity extends AppCompatActivity {

    static final String LOG = "PComm - CreatePot";
    // Instantiate Tools class
    static Tools toolbox = new Tools();

    RelativeLayout mainPartLayout;
    RelativeLayout mainElemLayout;

    Button CreatePotBtn;
    Button NewPartBtn;
    Button NewTBfundElemBtn;
    Button QuitBtn;
    Button[] deleteElemBtns;

    EditText ETPotName;
    EditText ETNewPart;
    EditText ETNewTBFundElem;

    HashMap<String, Float> partList = new HashMap<String, Float>();
    HashMap<String, Float> elemList = new HashMap<String, Float>();

    String userID;
    String userName;
    String method;
    String newPartName;
    String potName;

    int lastPartElem;
    int lastTBFundElem;

    private PCommRPC pCommRPC = null;

    BroadcastReceiver CreatePotReceiver;
    BroadcastReceiver CheckUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpot);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final SharedPreferences prefs = getSharedPreferences(getString(R.string.pcommPrefsFile), 0);

//        Intent intent = getIntent();
//        username = intent.getStringExtra("userName");

        mainPartLayout = (RelativeLayout) findViewById(R.id.RLAllPart);
        mainElemLayout = (RelativeLayout) findViewById(R.id.RLAllElem);
        lastPartElem = R.id.LLNewPart;
        lastTBFundElem = R.id.LLNewElem;

        CreatePotBtn = (Button) findViewById(R.id.CreatePotBtn);
        QuitBtn = (Button) findViewById(R.id.CancelBtn);
        NewPartBtn = (Button) findViewById(R.id.addNewPartBtn);
        NewTBfundElemBtn = (Button) findViewById(R.id.addNewElemBtn);

        ETPotName = (EditText) findViewById(R.id.editTextPotname);
        ETNewPart = (EditText) findViewById(R.id.editTextNewPart);
        ETNewTBFundElem = (EditText) findViewById(R.id.editTextNewElem);

        userID = prefs.getString("userID", "N/A");

        registerReceiver(CreatePotReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    Log.d(LOG, "Create pot successfully done.");
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_successcreatepot), Toast.LENGTH_LONG).show();
                    unregisterReceiver(CreatePotReceiver);
                    unregisterReceiver(CheckUserName);
                    Intent mainIntent;
                    mainIntent = new Intent(getApplicationContext(), ConsultPotsActivity.class);
                    mainIntent.putExtra("userName", userName);
                    Log.d(LOG, "Quiting activity...");
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
        }, new IntentFilter("CreatePot"));

        registerReceiver(CheckUserName = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String status = intent.getExtras().get("faultCode").toString();
                if (status.equals("OK")) {
                    Log.d(LOG, "User name found in database.");

                    RelativeLayout newElem = createUIElement(newPartName, 1);
                    newElem.setId(View.generateViewId());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.BELOW, lastPartElem);
                    mainPartLayout.addView(newElem, params);
                    ETNewPart.setText("");

                    lastPartElem = newElem.getId();
                    Log.d(LOG, "Nouveau participant ajouté: " + partList);
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
        }, new IntentFilter("CheckUserName"));

        NewPartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ETNewPart.getText().toString().equals("")) {
                    newPartName = ETNewPart.getText().toString();
                    method = "CheckUserName";

                    HashMap<String, Object> data = new HashMap<>();
                    data.put("user", getString(R.string.wsUser));
                    data.put("password", getString(R.string.wsPassword));
                    data.put("user_name", newPartName);
                    Log.i(LOG, data.values().toString());

                    pCommRPC = new PCommRPC(CreatePotActivity.this);
                    pCommRPC.execute(method, data);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_partnameempty), Toast.LENGTH_LONG).show();
                }
            }
        });

        NewTBfundElemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!ETNewTBFundElem.getText().toString().equals("")) {
                    String newTBFundElemName = ETNewTBFundElem.getText().toString();
                    RelativeLayout newElem = createUIElement(newTBFundElemName, 2);
                    newElem.setId(View.generateViewId());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                    params.addRule(RelativeLayout.BELOW, lastTBFundElem);
                    mainElemLayout.addView(newElem, params);
                    ETNewTBFundElem.setText("");

                    lastTBFundElem = newElem.getId();
                    Log.d(LOG, "Nouvel élément ajouté: " + elemList);
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_tbfundelemnameempty), Toast.LENGTH_LONG).show();
                }
            }
        });

        CreatePotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                potName = ETPotName.getText().toString();

                if (potName.equals("")) {
                    Toast.makeText(getApplicationContext(), getString(R.string.toast_nopotname), Toast.LENGTH_LONG).show();
                } else {

                    if (partList.size() > 0 && elemList.size() > 0) {
                        method = "CreatePot";

                        HashMap<String, Object> data = new HashMap<>();
                        data.put("user", getString(R.string.wsUser));
                        data.put("password", getString(R.string.wsPassword));
                        data.put("user_id", userID);
                        data.put("user_amount", 100.0);
                        data.put("pot_name", ETPotName.getText().toString());
                        data.put("part_list", partList);
                        data.put("elem_list", elemList);
                        Log.i(LOG, data.values().toString());

                        pCommRPC = new PCommRPC(CreatePotActivity.this);
                        pCommRPC.execute(method, data);

                        Log.d(LOG, "Nouveau pot créé");
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.toast_potrequirement), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        QuitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unregisterReceiver(CreatePotReceiver);
                unregisterReceiver(CheckUserName);
                Intent mainIntent;
                mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                mainIntent.putExtra("userName", userName);
                Log.d(LOG, "Quiting activity...");
                startActivity(mainIntent);
                finish();
            }
        });
    }

    /**
     * When user want to add participant or element to be fund,
     * the activity send data and get back new element to display
     *
     * @param elemName String The element name, to display on the new element
     * @param elemType int The element type, to know which array to update
     *
     * @return RelativeLayout The new element to display
     */
    private RelativeLayout createUIElement(String elemName, int elemType) {

        final RelativeLayout newElem = new RelativeLayout(getApplicationContext());
        newElem.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        EditText newElemName = new EditText(getApplicationContext());
        newElemName.setId(View.generateViewId());
        LinearLayout newElemOptions = new LinearLayout(getApplicationContext());
        EditText newElemAmount = new EditText(getApplicationContext());
        newElemAmount.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        Button deleteNewElem = new Button(getApplicationContext());
        deleteNewElem.setBackgroundTintList(getResources().getColorStateList(R.color.colorWhite));
        deleteNewElem.setTextSize(24);
        deleteNewElem.setTextColor(getResources().getColor(R.color.colorBlack));
        deleteNewElem.setText("-");

        newElemName.setText(elemName);
        newElemName.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        newElemAmount.setText(getResources().getString(R.string.defaultelemvalue));
        float value = Float.valueOf(getResources().getString(R.string.defaultelemvalue));
        if (elemType == 1) {
            partList.put(elemName, value);
        } else if (elemType == 2) {
            elemList.put(elemName, value);
        }
        newElemAmount.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT, 1f));
        deleteNewElem.setLayoutParams(new TableLayout.LayoutParams(0, TableLayout.LayoutParams.MATCH_PARENT, 4f));

        deleteNewElem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainPartLayout.removeView(newElem);
            }
        });

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

        newElem.addView(newElemName);

        layoutParams.addRule(RelativeLayout.BELOW, newElemName.getId());

        newElemOptions.addView(newElemAmount);
        newElemOptions.addView(deleteNewElem);

        newElem.addView(newElemOptions, layoutParams);

        return newElem;
    }
}
