package com.lody.virtual.client.hook.proxies.devicepolicy;

import android.app.admin.SystemUpdatePolicy;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.MethodProxy;

import java.lang.reflect.Method;

import mirror.android.app.admin.IDevicePolicyManager;

/**
 * Created by wy on 2017/10/20.
 */

public class DevicePolicyManagerStub extends BinderInvocationProxy{
    public DevicePolicyManagerStub() {
        super(IDevicePolicyManager.Stub.asInterface, Context.DEVICE_POLICY_SERVICE);
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new GetStorageEncryptionStatus());
        addMethodProxy(new fakeResultHandle("isAdminActive",  Boolean.valueOf(false)));
        addMethodProxy(new fakeResultHandle("removeActiveAdmin",  null));
        addMethodProxy(new fakeResultHandle("lockNow",  null));
        addMethodProxy(new fakeResultHandle("setPasswordQuality",  null));
        addMethodProxy(new fakeResultHandle("getTrustAgentConfiguration",  null));
        addMethodProxy(new fakeResultHandle("getPasswordQuality",  Integer.valueOf(0)));
        addMethodProxy(new fakeResultHandle("getCameraDisabled",  Boolean.valueOf(false)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addMethodProxy(new getSystemUpdatePolicy());
            addMethodProxy(new fakeResultHandle("notifyPendingSystemUpdate",  null));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addMethodProxy(new fakeResultHandle("getDeviceOwnerComponent",  null));
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N_MR1) {
            addMethodProxy(new packageNameConverter("isCallerApplicationRestrictionsManagingPackage", 0));
            addMethodProxy(new packageNameConverter("enableSystemApp", 1));
            addMethodProxy(new packageNameConverter("enableSystemAppWithIntent", 1));
            addMethodProxy(new packageNameConverter("enforceCanManageCaCerts", 1));
            addMethodProxy(new packageNameConverter("getKeepUninstalledPackages", 1));
            addMethodProxy(new packageNameConverter("getPermissionGrantState", 1));
            addMethodProxy(new packageNameConverter("installCaCert", 1));
            addMethodProxy(new packageNameConverter("installKeyPair", 1));
            addMethodProxy(new packageNameConverter("isApplicationHidden", 1));
            addMethodProxy(new packageNameConverter("isPackageSuspended", 1));
            addMethodProxy(new packageNameConverter("removeKeyPair", 1));
            addMethodProxy(new packageNameConverter("setApplicationHidden", 1));
            addMethodProxy(new packageNameConverter("setApplicationRestrictions", 1));
            addMethodProxy(new packageNameConverter("setKeepUninstalledPackages", 1));
            addMethodProxy(new packageNameConverter("setPackagesSuspended", 1));
            addMethodProxy(new packageNameConverter("setPermissionGrantState", 1));
            addMethodProxy(new packageNameConverter("setPermissionPolicy", 1));
            addMethodProxy(new packageNameConverter("setUninstallBlocked", 1));
            addMethodProxy(new packageNameConverter("uninstallCaCerts", 1));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            addMethodProxy(new fakeResultHandle("isDeviceProvisioned",  Boolean.valueOf(true)));
        }
    }

    private class fakeResultHandle extends MethodProxy {
        private final Object fakeResult;
        fakeResultHandle(String methodName, Object result) {
            super(methodName);
            fakeResult = result;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            return fakeResult;
        }
    }

    private static class packageNameConverter extends MethodProxy {
        private final int pkgIdx;

        public  packageNameConverter(String methodName, int idx) {
            super(methodName);
            pkgIdx = idx;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[pkgIdx] = VirtualCore.get().getHostPkg();
            return method.invoke(who, args);
        }
    }

    private static class GetStorageEncryptionStatus extends MethodProxy {

        @Override
        public String getMethodName() {
            return "getStorageEncryptionStatus";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who, args);
        }
    }

    private static class getSystemUpdatePolicy extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getSystemUpdatePolicy";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            return SystemUpdatePolicy.createPostponeInstallPolicy();
        }
    }
}
