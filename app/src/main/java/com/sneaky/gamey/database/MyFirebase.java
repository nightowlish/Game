package com.sneaky.gamey.database;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MyFirebase {
    private static DatabaseReference mDatabase;
    private static String id;
    public static boolean finished = false;

    public static void initFirebase() {
        MyFirebase.id = MyFirebase.getID();
        MyFirebase.mDatabase = FirebaseDatabase.getInstance().getReference(MyFirebase.id);
        /*MyFirebase.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                Log.d("asdf", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("asdf", "Failed to read value.", error.toException());
            }
        });*/
        writeNewUser();
    }

    public static void sendData(String type, Object object) {
        MyFirebase.mDatabase.child(type).setValue(object);

    }

    public static String getID() {
        /*TelephonyManager telephonyManager = (TelephonyManager) App.getContext().getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getSubscriberId();*/
        Long tsLong = System.currentTimeMillis();
        return tsLong.toString();
    }

    private static void writeNewUser() {
        MyFirebase.sendData("register", MyFirebase.id);
        MyFirebase.finished = true;
    }
}
