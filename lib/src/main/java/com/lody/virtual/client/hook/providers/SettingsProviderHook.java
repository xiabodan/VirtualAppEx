package com.lody.virtual.client.hook.providers;

import android.os.Build;
import android.os.Bundle;

import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.hook.base.MethodBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lody
 */

public class SettingsProviderHook extends ExternalProviderHook {

    private static final String TAG = SettingsProviderHook.class.getSimpleName();

    private static final int METHOD_GET = 0;
    private static final int METHOD_PUT = 1;

    public static final int DB_INVALID = -1;
    public static final int DB_SYSTEM = 0;
    public static final int DB_SECURE = 1;
    public static final int DB_GLOBAL = 2;
    public static final int DB_CONFIG = 3;
    public static final String NAMESPACE_TEXTCLASSIFIER = "textclassifier";
    public static final String NAMESPACE_RUNTIME = "runtime";
    public static final List<String> PUBLIC_NAMESPACES = Arrays.asList(NAMESPACE_TEXTCLASSIFIER, NAMESPACE_RUNTIME);

    private static final Map<String, String> PRE_SET_VALUES = new HashMap<>();

    static {
        PRE_SET_VALUES.put("user_setup_complete", "1");
        PRE_SET_VALUES.put("install_non_market_apps", "0");
    }


    public SettingsProviderHook(Object base) {
        super(base);
    }

    static int parseDatabase(String method) {
        if (method.contains("secure")) {
            return DB_SECURE;
        } else if (method.contains("system")) {
            return DB_SYSTEM;
        } else if (method.contains("global")) {
            return DB_GLOBAL;
        } else if (method.contains("config")) {
            return  DB_CONFIG;
        }
        return DB_INVALID;
    }

    private static int getMethodType(String method) {
        if (method.startsWith("GET_")) {
            return METHOD_GET;
        }
        if (method.startsWith("PUT_")) {
            return METHOD_PUT;
        }
        return -1;
    }

    private static boolean isSecureMethod(String method) {
        return method.endsWith("secure");
    }


    @Override
    public Bundle call(MethodBox methodBox, String method, String arg, Bundle extras) throws InvocationTargetException {
        if (!VClientImpl.get().isBound()) {
            return methodBox.call();
        }
        final int methodType = getMethodType(method);
        final int dbIdx = parseDatabase(method);
        if (dbIdx == DB_CONFIG && arg instanceof String) {  // force fake config
            final String namespace = arg.split("/")[0];
            if (methodType == METHOD_PUT || !PUBLIC_NAMESPACES.contains(namespace)) {
                return new Bundle();
            }
        }
        if (METHOD_GET == methodType) {
            String presetValue = PRE_SET_VALUES.get(arg);
            if (presetValue != null) {
                return wrapBundle(arg, presetValue);
            }
            if ("android_id".equals(arg)) {
                return wrapBundle("android_id", VClientImpl.get().getDeviceInfo().androidId);
            }
        }
        if (METHOD_PUT == methodType) {
            if (isSecureMethod(method)) {
                return null;
            }
        }
        try {
            return methodBox.call();
        } catch (InvocationTargetException e) {
            if (e.getCause() instanceof SecurityException) {
                return null;
            }
            throw e;
        }
    }

    private Bundle wrapBundle(String name, String value) {
        Bundle bundle = new Bundle();
        if (Build.VERSION.SDK_INT >= 24) {
            bundle.putString("name", name);
            bundle.putString("value", value);
        } else {
            bundle.putString(name, value);
        }
        return bundle;
    }

    @Override
    protected void processArgs(Method method, Object... args) {
        super.processArgs(method, args);
    }
}
