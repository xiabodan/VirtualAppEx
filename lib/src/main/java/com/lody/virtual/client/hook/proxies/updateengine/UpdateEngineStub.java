package com.lody.virtual.client.hook.proxies.updateengine;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;

public class UpdateEngineStub extends BinderInvocationProxy {

    public UpdateEngineStub() {
        super(new GhostUpdateEngineImpl(), "android.os.UpdateEngineService");
    }
}