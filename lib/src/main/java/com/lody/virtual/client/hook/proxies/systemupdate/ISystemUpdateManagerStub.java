package com.lody.virtual.client.hook.proxies.systemupdate;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;

public class ISystemUpdateManagerStub extends BinderInvocationProxy {

    public ISystemUpdateManagerStub() {
        super(new GhostSystemUpdateManagerImpl(), "system_update");
    }
}