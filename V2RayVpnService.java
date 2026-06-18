package com.aether.tunnel.services;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class V2RayVpnService extends VpnService implements Runnable {
    private static final String TAG = "V2RayVpnService";
    private Thread mVPNThread;
    private ParcelFileDescriptor mInterface;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if ("START_VPN".equals(action)) {
                String configData = intent.getStringExtra("CONFIG_DATA");
                startVPN(configData);
            } else if ("STOP_VPN".equals(action)) {
                stopVPN();
            }
        }
        return START_STICKY;
    }

    private void startVPN(String configData) {
        stopVPN();
        mVPNThread = new Thread(this, "V2RayVPNThread");
        mVPNThread.start();
    }

    private void stopVPN() {
        if (mVPNThread != null) {
            mVPNThread.interrupt();
            mVPNThread = null;
        }
        try {
            if (mInterface != null) {
                mInterface.close();
                mInterface = null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing VPN Interface", e);
        }
    }

    @Override
    public void run() {
        try {
            Builder builder = new Builder();
            builder.setMtu(1500)
                   .addAddress("26.26.26.1", 24)
                   .addRoute("0.0.0.0", 0)
                   .setSession("AetherTunnel");

            mInterface = builder.establish();
            Log.i(TAG, "VPN Tunnel established successfully.");
            
            // در نسخه کامپایل شده، فایل کانفیگ به باینری xray-core متصل می‌شود
            while (!Thread.interrupted()) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            Log.e(TAG, "VPN Thread Error", e);
        } finally {
            stopVPN();
        }
    }

    @Override
    public void onDestroy() {
        stopVPN();
        super.onDestroy();
    }
}
