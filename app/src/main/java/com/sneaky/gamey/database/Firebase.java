package com.sneaky.gamey.database;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sneaky.gamey.App;

import org.json.JSONObject;

public class Firebase {
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private String id;

    public Firebase() {
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference();
        id = getID();
    }

    public static void sendData(JSONObject json) {

    }

    public String getID() {
        TelephonyManager telephonyManager = (TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }
}
