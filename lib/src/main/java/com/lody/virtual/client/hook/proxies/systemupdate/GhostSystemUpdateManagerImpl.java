package com.lody.virtual.client.hook.proxies.systemupdate;

import android.os.Bundle;
import android.os.ISystemUpdateManager;
import android.os.PersistableBundle;
import android.os.RemoteException;

import com.lody.virtual.helper.utils.VLog;

public class GhostSystemUpdateManagerImpl extends ISystemUpdateManager.Stub {
    private static final String TAG = "GhostSystemUpdateManagerImpl";

    @Override
    public void updateSystemUpdateInfo(PersistableBundle data) throws RemoteException {
        VLog.v(TAG, "updateSystemUpdateInfo ghost interface " + data);
    }

    @Override
    public Bundle retrieveSystemUpdateInfo() throws RemoteException {
        VLog.v(TAG, "updateSystemUpdateInfo ghost interface");
        return new Bundle();
    }
}
