package com.lody.virtual.client.hook.proxies.am;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;

import mirror.android.app.IActivityTaskManager;

/**
 * @author xiabo
 */
@Inject(MethodProxies.class)
public class ActivityTaskManagerStub extends BinderInvocationProxy {

    public ActivityTaskManagerStub() {
        super(IActivityTaskManager.Stub.asInterface, "activity_task");
    }

    @Override
    public void inject() throws Throwable {
        super.inject();
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
    }
}
