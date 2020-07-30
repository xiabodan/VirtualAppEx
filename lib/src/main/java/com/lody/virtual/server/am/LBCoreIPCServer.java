package com.lody.virtual.server.am;

import android.net.Credentials;
import android.net.LocalServerSocket;
import android.net.LocalSocket;

import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.lody.virtual.helper.Features;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.os.VUserHandle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

public class LBCoreIPCServer extends Thread {
    private static final boolean DEBUG = Features.FEATURE_DEBUG_SUPPORT;
    private static final String TAG = LBCoreIPCServer.class.getSimpleName();

    private static final byte CUR_PROTOCAL_VERSION = 1;
    private static final byte CMD_GETCALLINGUID = 1;

    // error code
    private static final int CMD_ERROR_NO_ERROR = 0;

    private String mSocketName;

    LBCoreIPCServer(String socketName) {
        mSocketName = socketName;
    }

    @Override
    public void run() {
        LocalServerSocket localServerSocket;
        String socketName = mSocketName;
        try {
            localServerSocket = new LocalServerSocket(socketName);
        } catch (IOException e) {
            return;
        }

        VLog.d(TAG, "LocalServerSocket started listen on: " + socketName);

        while (true) {
            LocalSocket clientSocket;
            try {
                clientSocket = localServerSocket.accept();
            } catch (IOException e) {
                break;
            }

            try {
                handleClientNoCheck(clientSocket);
            } catch (Exception ignored) {
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ignored) {
                }
            }
        }

        try {
            localServerSocket.close();
        } catch (IOException ignored) {
        }
    }

    private boolean validatePeerCredentials(LocalSocket clientSocket) {
        try {
            Credentials peerCredentials = clientSocket.getPeerCredentials();
            if (VirtualCore.get().getSystemPid() == peerCredentials.getUid()) {
                return true;
            }
        } catch (IOException ignored) {
        }
        return false;
    }

    private boolean handleClientNoCheck(LocalSocket clientSocket) throws IOException {
        byte[] buffer = new byte[1024];
        if (!validatePeerCredentials(clientSocket)) {
            // return false;
        }
        InputStream is = clientSocket.getInputStream();
        int n = is.read(buffer);
        if (n <= 0) {
            VLog.d(TAG, "Connection closed.");
            return false;
        }

        if (n < 2) { // ver + cmd
            return false;
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
        byte ver = byteBuffer.get();
        byte cmd = byteBuffer.get();

        switch (cmd) {
            case CMD_GETCALLINGUID: {
                return handleGetCallingUid(clientSocket, byteBuffer);
            }
            default: {
                VLog.d(TAG, "unknown cmd : " + cmd);
                return false;
            }
        }
    }

    private boolean handleGetCallingUid(LocalSocket clientSocket, ByteBuffer request) {
        int vuid = request.getInt();
        int callingUid = request.getInt();
        int callingPid = request.getInt();
        int callerPid = request.getInt();
        int fakeUid = getCallingUid(vuid, callingUid, callingPid, callerPid);

        ByteBuffer response = ByteBuffer.allocate(32);
        response.order(ByteOrder.LITTLE_ENDIAN);
        response.put(CUR_PROTOCAL_VERSION);
        response.putInt(CMD_ERROR_NO_ERROR);
        response.putInt(fakeUid); //result

        try {
            OutputStream os = clientSocket.getOutputStream();
            os.write(response.array(), 0, response.position());
            os.close();
            return true;
        } catch (IOException ignored) {
            return false;
        }
    }

    public int getCallingUid(int userId, int callingUid, int callingPid, int callerPid) {
        if (callingPid == callerPid) {
            // return VClientImpl.get().getBaseVUid();
        }
        if (callingPid == VirtualCore.get().getSystemPid()) {
            return android.os.Process.SYSTEM_UID;
        }
        int vuid = VActivityManagerService.get().getUidByPid(callingPid);
        if (vuid != -1) {
            return VUserHandle.getAppId(vuid);
        }
        VLog.w(TAG, "unknown uid: " + callingPid);
        return VClientImpl.get().getBaseVUid();
    }
}
