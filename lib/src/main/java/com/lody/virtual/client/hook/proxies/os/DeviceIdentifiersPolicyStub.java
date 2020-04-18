package com.lody.virtual.client.hook.proxies.os;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.ReplaceCallingPkgMethodProxy;

import mirror.android.os.IDeviceIdentifiersPolicyService;

public class DeviceIdentifiersPolicyStub extends BinderInvocationProxy {

    public DeviceIdentifiersPolicyStub() {
        super(IDeviceIdentifiersPolicyService.Stub.asInterface, "device_identifiers");
    }

    @Override
    public void inject() throws Throwable {
        super.inject();
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        addMethodProxy(new ReplaceCallingPkgMethodProxy("getSerialForPackage"));
    }
}
