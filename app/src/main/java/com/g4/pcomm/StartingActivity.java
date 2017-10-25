package com.g4.pcomm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.IntDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class StartingActivity extends AppCompatActivity {

    Button LoginBtn;
    Button SigninBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        LoginBtn = (Button) findViewById(R.id.LoginBtn);
        SigninBtn = (Button) findViewById(R.id.CreatePotBtn);

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent;
                loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

        SigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent;
                loginIntent = new Intent(getApplicationContext(), SigninActivity.class);
                startActivity(loginIntent);
                finish();
//                Toast.makeText(getApplicationContext(), getString(R.string.joke_signin), Toast.LENGTH_LONG).show();
            }
        });
    }
}
