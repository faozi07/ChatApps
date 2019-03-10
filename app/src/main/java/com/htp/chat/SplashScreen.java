package com.htp.chat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                cekDataUser();
            }
        }, 1000);
    }

    private void cekDataUser() {
        SharedPreferences spLogin = getSharedPreferences(StaticVars.SP_USER, MODE_PRIVATE);
        if (spLogin.getString(StaticVars.SP_USER_NAME, "").equals("")) {
            startActivity(new Intent(SplashScreen.this, CreateAccount.class));
        }
        else {
            startActivity(new Intent(SplashScreen.this, MainScreen.class));
        }
    }
}
