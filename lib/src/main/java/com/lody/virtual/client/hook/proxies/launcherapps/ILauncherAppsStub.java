package com.lody.virtual.client.hook.proxies.launcherapps;

import android.content.Context;
import android.os.Build;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;

import mirror.android.content.pm.ILauncherApps;

/**
 * @author Lody
 */
public class ILauncherAppsStub extends BinderInvocationProxy {

    public ILauncherAppsStub() {
        super(ILauncherApps.Stub.asInterface, Context.LAUNCHER_APPS_SERVICE);
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addMethodProxy(new ReplaceCallingPkgMethodProxy("addOnAppsChangedListener"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getShortcuts"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("pinShortcuts"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("startShortcut"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getShortcutIconResId"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getShortcutIconFd"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("hasShortcutHostPermission"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getLauncherActivities"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("resolveActivity"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("startActivityAsUser"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("showAppDetailsAsUser"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("isPackageEnabled"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("isActivityEnabled"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getApplicationInfo"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getShortcutConfigActivities"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getShortcutConfigActivityIntent"));
            }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            addMethodProxy(new ReplaceCallingPkgMethodProxy("startSessionDetailsActivityAsUser"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getAppUsageLimit"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("registerPackageInstallerCallback"));
            addMethodProxy(new ReplaceCallingPkgMethodProxy("getAllSessions"));
        }
    }
}