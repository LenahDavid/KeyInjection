package com.example.myapplication;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;


import com.usdk.apiservice.BuildConfig;
import com.usdk.apiservice.aidl.DeviceServiceData;
import com.usdk.apiservice.aidl.UDeviceService;


import timber.log.Timber;


public class AarApplication extends Application {
    private String USDK_ACTION_NAME = "com.usdk.apiservice";
    private String USDK_PACKAGE_NAME = "com.usdk.apiservice";


    public static UDeviceService deviceService;
    public static Application application;


//    public static DeviceService getDeviceService(){return deviceService;}

  @Override
    public void onCreate() {
        super.onCreate();
        AarApplication.application = this;

        DeviceHelper.me().init(this);
        DeviceHelper.me().bindService();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } /** add logger for prod/release **/
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        DeviceHelper.me().unregister();
        DeviceHelper.me().unbindService();
    }


    /**
     * Bind sdk service.
     */
    private void bindSdkDeviceService() {
        Intent intent = new Intent();
        intent.setAction(USDK_ACTION_NAME);
        intent.setPackage(USDK_PACKAGE_NAME);

        Timber.d("binding sdk device service...");
        boolean flag = AarApplication.this.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if (!flag) {
            Timber.d("SDK service binding failed.");

        }
        Timber.d("SDK service binding successfully.");
    }

    /**
     * Service connection.
     */
    private ServiceConnection serviceConnection  = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Timber.d("SDK service disconnected.");
        }

        @Override
        public void onServiceConnected(ComponentName name , IBinder service ) {
            Timber.d("SDK service connected.");
            try {
                deviceService = UDeviceService.Stub.asInterface(service);

                Bundle param = new Bundle();
                param.putBoolean(DeviceServiceData.USE_EPAY_MODULE, true);
                deviceService.register(param, new Binder());

                Bundle logOption = new Bundle();
                logOption.putBoolean(DeviceServiceData.COMMON_LOG, true);
                deviceService.debugLog(logOption);
                Timber.d(
                        "SDK deviceService initiated version:" + deviceService.getVersion()
                                .toString() + "."
                );
            } catch (RemoteException e) {
                e.printStackTrace();
                throw new RuntimeException("SDK deviceService initiating failed.", e);
            }

            try {
                linkToDeath(service);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }


        private void linkToDeath(IBinder service) throws RemoteException {
            service.linkToDeath(new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    Timber.d("SDK service is dead. Reconnecting...");
                    bindSdkDeviceService();
                }
            }, 0);

        }
    };



}
