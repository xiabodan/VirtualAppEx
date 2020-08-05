package com.lody.virtual.client.hook.proxies.updateengine;

import android.os.IUpdateEngine;
import android.os.IUpdateEngineCallback;

import com.lody.virtual.helper.utils.VLog;

import java.util.Arrays;

public class GhostUpdateEngineImpl extends IUpdateEngine.Stub {
    private static final String TAG = "GhostUpdateEngineImpl";

    @Override
    public void applyPayload(String url, long payload_offset, long payload_size, String[] headerKeyValuePairs) {
        VLog.v(TAG, "ghost applyPayload " + url
                + " payload_offset " + payload_offset
                + " payload_size " + payload_size
                + " headerKeyValuePairs " + Arrays.toString(headerKeyValuePairs)
        );
    }

    @Override
    public boolean bind(IUpdateEngineCallback callback) {
        VLog.v(TAG, "ghost bind " + callback);
        return false;
    }

    @Override
    public boolean unbind(IUpdateEngineCallback callback) {
        VLog.v(TAG, "ghost unbind " + callback);
        return false;
    }

    @Override
    public void suspend() {
        VLog.v(TAG, "ghost suspend");
    }

    @Override
    public void resume() {
        VLog.v(TAG, "ghost resume");
    }

    @Override
    public void cancel() {
        VLog.v(TAG, "ghost cancel");
    }

    @Override
    public void resetStatus() {
        VLog.v(TAG, "ghost resetStatus");
    }

    @Override
    public boolean verifyPayloadApplicable(String metadataFilename) {
        VLog.v(TAG, "ghost verifyPayloadApplicable " + metadataFilename);
        return false;
    }
}
