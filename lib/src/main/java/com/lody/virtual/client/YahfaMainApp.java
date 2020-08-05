package com.lody.virtual.client;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import dalvik.system.DexClassLoader;
import lab.galaxy.yahfa.HookMain;

/**
 * Created by liuruikai756 on 30/03/2017.
 */

public class YahfaMainApp {
    private static final  String TAG = YahfaMainApp.class.getSimpleName();

    @TargetApi(21)
    public static void loadPlugin(Context base, String pluginPath) {
        try {
            /*
            Build and put the demoPlugin apk in sdcard before running the demoApp
             */
            ClassLoader classLoader = base.getClassLoader();
            String plugin = "/sdcard/demoPlugin-debug.apk";
            if (pluginPath != null) {
                plugin = pluginPath;
            }
            DexClassLoader dexClassLoader = new DexClassLoader(plugin,
                    base.getCodeCacheDir().getAbsolutePath(), null, classLoader);
            HookMain.doHookDefault(dexClassLoader, classLoader);
            Log.d(TAG, "Yahfa loadPlugin dexClassLoader:" + dexClassLoader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
