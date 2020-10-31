package com.sneaky.gamey.database;

import android.content.Context;
import android.telephony.TelephonyManager;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sneaky.gamey.App;

public class Firebase {
    private static FirebaseDatabase mDatabase;
    private static DatabaseReference mDatabaseReference;
    private static String id;

    public static void initFirebase() {
        Firebase.mDatabase = FirebaseDatabase.getInstance();
        Firebase.mDatabaseReference = mDatabase.getReference();
        Firebase.id = Firebase.getID();
    }

    public static void sendData(Object object) {

    }

    public static String getID() {
        TelephonyManager telephonyManager = (TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();
    }
}
