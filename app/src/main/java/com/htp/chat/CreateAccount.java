package com.htp.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class CreateAccount extends AppCompatActivity {
    Button btnCreate;
    EditText eNama, ePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Buat Akun");
        }

        btnCreate = findViewById(R.id.btnCreate);
        eNama = findViewById(R.id.eName);
        ePhone = findViewById(R.id.ePhone);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                
                String name = eNama.getText().toString();
                String phone = ePhone.getText().toString();
                if (name.equals("") || phone.equals("")) {
                    Toast.makeText(CreateAccount.this, "Isi data dengan lengkap", Toast.LENGTH_LONG).show();
                } else {
                    SharedPreferences spUser = getSharedPreferences(StaticVars.SP_USER, MODE_PRIVATE);
                    SharedPreferences.Editor createEditor = spUser.edit();
                    createEditor.putString(StaticVars.SP_USER_ID, String.valueOf(new Random().nextInt()));
                    createEditor.putString(StaticVars.SP_USER_NAME, name);
                    createEditor.putString(StaticVars.SP_USER_PHONE, phone);
                    createEditor.apply();
                    
                    if (!spUser.getString(StaticVars.SP_USER_NAME,"").equals("")) {
                        startActivity(new Intent(CreateAccount.this, MainScreen.class));
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}