package com.lody.virtual.client.hook.proxies.content;

import android.content.SyncAdapterType;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;

import java.lang.reflect.Method;
import java.util.Collections;

import mirror.android.content.IContentService;

/**
 * @author Lody
 *
 * @see IContentService
 */
public class ContentServiceStub extends BinderInvocationProxy {

    public ContentServiceStub() {
        super(IContentService.Stub.asInterface, "content");
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        if (VirtualCore.get().isVAppProcess()) {
            // TODO: Need Fix The Features
            addMethodProxy(new fakeResultHandle("registerContentObserver", null));
            addMethodProxy(new fakeResultHandle("unregisterContentObserver", null));
            addMethodProxy(new fakeResultHandle("notifyChange", null));
            addMethodProxy(new fakeResultHandle("requestSync", null));
            addMethodProxy(new fakeResultHandle("sync", null));
            addMethodProxy(new fakeResultHandle("syncAsUser", null));
            addMethodProxy(new fakeResultHandle("cancelSync", null));
            addMethodProxy(new fakeResultHandle("cancelSyncAsUser", null));
            addMethodProxy(new fakeResultHandle("cancelRequest", null));
            addMethodProxy(new fakeResultHandle("getSyncAutomatically", false));
            addMethodProxy(new fakeResultHandle("getSyncAutomaticallyAsUser", false));
            addMethodProxy(new fakeResultHandle("setSyncAutomatically", null));
            addMethodProxy(new fakeResultHandle("setSyncAutomaticallyAsUser", null));
            addMethodProxy(new fakeResultHandle("getPeriodicSyncs", Collections.emptyList()));
            addMethodProxy(new fakeResultHandle("addPeriodicSync", null));
            addMethodProxy(new fakeResultHandle("removePeriodicSync", null));
            addMethodProxy(new fakeResultHandle("getIsSyncable", -1));
            addMethodProxy(new fakeResultHandle("getIsSyncableAsUser", -1));
            addMethodProxy(new fakeResultHandle("setIsSyncable", null));
            addMethodProxy(new fakeResultHandle("setIsSyncableAsUser", null));
            addMethodProxy(new fakeResultHandle("isSyncActive", false));
            addMethodProxy(new fakeResultHandle("getCurrentSyncs", Collections.emptyList()));
            addMethodProxy(new fakeResultHandle("getCurrentSyncsAsUser", Collections.emptyList()));
            addMethodProxy(new fakeResultHandle("isSyncPending", false));
            addMethodProxy(new fakeResultHandle("isSyncPendingAsUser", false));
            addMethodProxy(new fakeResultHandle("addStatusChangeListener", null));
            addMethodProxy(new fakeResultHandle("removeStatusChangeListener", null));
            addMethodProxy(new fakeResultHandle("setMasterSyncAutomatically", null));
            addMethodProxy(new fakeResultHandle("setMasterSyncAutomaticallyAsUser", null));
            addMethodProxy(new fakeResultHandle("getMasterSyncAutomatically", false));
            addMethodProxy(new fakeResultHandle("getMasterSyncAutomaticallyAsUser", false));
            addMethodProxy(new fakeResultHandle("getSyncAdapterPackagesForAuthorityAsUser", new String[0]));
            addMethodProxy(new fakeResultHandle("getSyncAdapterTypes", new SyncAdapterType[0]));
            addMethodProxy(new fakeResultHandle("getSyncAdapterTypesAsUser", new SyncAdapterType[0]));
            addMethodProxy(new fakeResultHandle("getSyncStatus", null));
            addMethodProxy(new fakeResultHandle("getSyncStatusAsUser", null));
            addMethodProxy(new fakeResultHandle("putCache", null));
            addMethodProxy(new fakeResultHandle("getCache", null));
            addMethodProxy(new fakeResultHandle("resetTodayStats", null));
            addMethodProxy(new fakeResultHandle("onDbCorruption", null));
        }
    }

    private class fakeResultHandle extends StaticMethodProxy {
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
}
