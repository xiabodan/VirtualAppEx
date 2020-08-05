package com.lody.virtual.client.hook.proxies.shortcut;

import android.content.Context;
import android.content.pm.ShortcutInfo;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.helper.utils.VLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import mirror.android.content.pm.IShortcutService;

/**
 * @author Lody
 */
public class ShortcutServiceStub extends BinderInvocationProxy {
    private static final String TAG = ShortcutServiceStub.class.getSimpleName();

    public ShortcutServiceStub() {
        super(IShortcutService.Stub.asInterface, Context.SHORTCUT_SERVICE);
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new fakeResultHandle("getManifestShortcuts", new ArrayList<ShortcutInfo>()));
        addMethodProxy(new fakeResultHandle("getDynamicShortcuts", new ArrayList<ShortcutInfo>()));
        addMethodProxy(new fakeResultHandle("setDynamicShortcuts", true));
        addMethodProxy(new fakeResultHandle("addDynamicShortcuts", true));
        addMethodProxy(new fakeResultHandle("createShortcutResultIntent", null));
        addMethodProxy(new fakeResultHandle("disableShortcuts", null));
        addMethodProxy(new fakeResultHandle("enableShortcuts", null));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getRemainingCallCount"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getRateLimitResetTime"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getIconMaxDimensions"));
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getMaxShortcutCountPerActivity"));
        addMethodProxy(new fakeResultHandle("reportShortcutUsed", null));
        addMethodProxy(new fakeResultHandle("onApplicationActive", null));
        addMethodProxy(new fakeResultHandle("removeDynamicShortcuts", null));
        addMethodProxy(new fakeResultHandle("removeAllDynamicShortcuts", null));
        addMethodProxy(new fakeResultHandle("getPinnedShortcuts", new ArrayList<ShortcutInfo>()));
        addMethodProxy(new fakeResultHandle("updateShortcuts", true));
        addMethodProxy(new fakeResultHandle("isRequestPinItemSupported", false));
        addMethodProxy(new fakeResultHandle("requestPinShortcut", true));
    }

    private class fakeResultHandle extends StaticMethodProxy {
        private final Object fakeResult;

        fakeResultHandle(String methodName, Object result) {
            super(methodName);
            fakeResult = result;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            VLog.v(TAG, method.getName() + " fake result " + fakeResult);
            if (fakeResult instanceof List) {
                return MethodParameterUtils.convertListToParceledListSliceIfNeeded(method, (List) fakeResult);
            } else {
                return fakeResult;
            }
        }
    }
}
