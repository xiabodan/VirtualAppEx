package com.lody.virtual.client.hook.proxies.crossprofile;

import android.content.Context;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.helper.utils.VLog;

import java.lang.reflect.Method;

import mirror.android.content.pm.ICrossProfileApps;

/**
 * @author xiabo
 * @see android.content.pm.CrossProfileApps
 */

public class ICrossProfileAppsStub extends BinderInvocationProxy {
    private static final String TAG = "ICrossProfileAppsStub";

    public ICrossProfileAppsStub() {
        super(ICrossProfileApps.Stub.asInterface, Context.CROSS_PROFILE_APPS_SERVICE);
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new MyCrossProfileApps("getTargetUserProfiles"));
        addMethodProxy(new MyCrossProfileApps("startActivityAsUser"));
    }

    class MyCrossProfileApps extends StaticMethodProxy {
        public MyCrossProfileApps(String name) {
            super(name);
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            VLog.d(TAG, "MyCrossProfileApps " + method.getName());
            int callingPackageIndex = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, String.class, 0);
            if (callingPackageIndex >= 0) {
                String pkg = (String) args[callingPackageIndex];
                args[callingPackageIndex] = VirtualCore.get().getHostPkg();
            }
            return super.call(who, method, args);
        }
    }
}
