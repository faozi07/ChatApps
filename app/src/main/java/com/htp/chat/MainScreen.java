package com.htp.chat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.htp.chat.adapter.ChatAdapter;
import com.htp.chat.modal.modChat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainScreen extends AppCompatActivity {

    RecyclerView rvChat;
    EditText eMychat;
    ImageButton ibSend;
    ChatAdapter chatAdapter;
    LinearLayoutManager llm;
    ArrayList<modChat> arrChat = new ArrayList<>();
    SharedPreferences spUser;
    private boolean isExit = false;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference ds = db.collection("chat").document();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Chat Room");
        }

        init();
        action();
    }

    private void init() {
        rvChat = findViewById(R.id.rvChat);
        eMychat = findViewById(R.id.eMyChat);
        ibSend = findViewById(R.id.ibSend);

        spUser = getSharedPreferences(StaticVars.SP_USER, MODE_PRIVATE);
        chatAdapter = new ChatAdapter(this, arrChat);
        llm = new LinearLayoutManager(this);
        rvChat.setLayoutManager(llm);
        rvChat.setHasFixedSize(true);
        rvChat.setAdapter(chatAdapter);
    }

    private void action() {
        ibSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chat = eMychat.getText().toString();
                if (!chat.equals("")) {
                    try {
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }

                        String userId = spUser.getString(StaticVars.SP_USER_ID, "");
                        String nama = spUser.getString(StaticVars.SP_USER_NAME, "");
                        String phone = spUser.getString(StaticVars.SP_USER_PHONE, "");

                        Map<String, Object> userChat = new HashMap<>();
                        userChat.put("userId", userId);
                        userChat.put("nama", nama);
                        userChat.put("phone", phone);
                        userChat.put("chat", chat);
                        userChat.put("date", String.valueOf(new Date()));

                        checkingChat(userChat);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        getData();
    }

    public void getData() {
        db.collection("chat")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.i("Error ", "listen:error", e);
                            return;
                        }

                        for (final DocumentChange dc : snapshots.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                try {
                                    modChat modChat = new modChat();
                                    JSONObject jsonObject = new JSONObject(dc.getDocument().getData());
                                    modChat.setUserId(jsonObject.getString("userId"));
                                    modChat.setName(jsonObject.getString("nama"));
                                    modChat.setPhone(jsonObject.getString("phone"));
                                    modChat.setDescriptionChat(jsonObject.getString("chat"));
                                    Date date = new Date(jsonObject.getString("date"));
                                    modChat.setPostDate(date);

                                    arrChat.add(modChat);
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }

                        Collections.sort(arrChat, new Comparator<modChat>() {
                            @Override
                            public int compare(modChat o1, modChat o2) {
                                return o1.getPostDate().compareTo(o2.getPostDate());
                            }
                        });
                        chatAdapter.notifyDataSetChanged();
                        rvChat.smoothScrollToPosition(arrChat.size());

                    }
                });
    }

    private void checkingChat(final Map<String, Object> userChat) {
        eMychat.setText("");
        db.collection("chat").document()
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.getData() == null) {
                            addNewChat(userChat);
                        } else {
                            updateChat(userChat);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainScreen.this, "Terjadi kesalahan, silakan coba kembali", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addNewChat(Map<String, Object> userChatChat) {
        db.collection("chat").document()
                .set(userChatChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        eMychat.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainScreen.this, "Gagal Kirim Pesan, silahkan coba lagi", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void updateChat(Map<String, Object> userChatChat) {
        db.collection("chat").document()
                .update(userChatChat)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        eMychat.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainScreen.this, "Gagal Kirim Pesan, silahkan coba lagi", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void showDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("Apa benar anda ingin Logout ?");
        alertDialogBuilder
                .setMessage("Klik Ya untuk logout!")
                .setCancelable(false)
                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences.Editor loginEditor = spUser.edit();
                        loginEditor.clear();
                        loginEditor.apply();
                        finish();
                        startActivity(new Intent(MainScreen.this, CreateAccount.class));
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(MainScreen.this, "Tekan sekali lagi untuk keluar", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isExit = false;
                }
            }, 2000);
        } else {
            finish();
        }
    }
}
