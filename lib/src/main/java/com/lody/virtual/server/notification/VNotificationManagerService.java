package com.lody.virtual.server.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Parcel;
import android.text.TextUtils;

import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.os.VEnvironment;
import com.lody.virtual.server.interfaces.INotificationManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.zip.CheckedOutputStream;

public class VNotificationManagerService implements INotificationManager {
    private static final String VA_DEFAULT_CHANNEL_ID = "va_miscellaneous";
    private static final String VA_DEFAULT_CHANNEL_NAME = "Default Channel";
    private static final int CONFIG_VERSION = 0;

    private static final AtomicReference<VNotificationManagerService> gService = new AtomicReference<>();
    private NotificationManager mNotificationManager;
    static final String TAG = NotificationCompat.class.getSimpleName();
    private final List<String> mDisables = new ArrayList<>();
    //VApp's Notifications
    private final HashMap<String, List<NotificationInfo>> mNotifications = new HashMap<>();
    private Context mContext;

    private final HashMap<String, Record> mRecords = new HashMap<>(); // pkg|vuid => Record
    private final File mConfigFile;

    private VNotificationManagerService(Context context) {
        mContext = context;
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        registerDefaultChannelIfNeeded(context);
        mConfigFile = getConfigFile(mContext);
        loadNotificationConfig();
    }

    private File getConfigFile(Context context) {
        final File notificationConfigFile = VEnvironment.getNotificationFile();
        if (!notificationConfigFile.exists()) {
            try {
                VLog.w(TAG, "config file is not exist, create new file.");
                notificationConfigFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return notificationConfigFile;
    }

    public static void systemReady(Context context) {
        VNotificationManagerService instance = new VNotificationManagerService(context);
        gService.set(instance);
    }

    public static VNotificationManagerService get() {
        return gService.get();
    }

    /***
     * fake notification's id
     *
     * @param id          notification's id
     * @param packageName notification's package
     * @param userId      user
     * @return
     */
    @Override
    public int dealNotificationId(int id, String packageName, String tag, int userId) {
        return id;
    }

    /***
     * fake notification's tag
     *
     * @param id          notification's id
     * @param packageName notification's package
     * @param tag         notification's tag
     * @param userId      user
     * @return
     */
    @Override
    public String dealNotificationTag(int id, String packageName, String tag, int userId) {
        if (TextUtils.equals(mContext.getPackageName(), packageName)) {
            return tag;
        }
        if (tag == null) {
            return packageName + "@" + userId;
        }
        return packageName + ":" + tag + "@" + userId;
    }

    @Override
    public boolean areNotificationsEnabledForPackage(String packageName, int userId) {
        return !mDisables.contains(packageName + ":" + userId);
    }

    @Override
    public void setNotificationsEnabledForPackage(String packageName, boolean enable, int userId) {
        String key = packageName + ":" + userId;
        if (enable) {
            if (mDisables.contains(key)) {
                mDisables.remove(key);
            }
        } else {
            if (!mDisables.contains(key)) {
                mDisables.add(key);
            }
        }
        //TODO: save mDisables ?
    }

    @Override
    public void addNotification(int id, String tag, String packageName, int userId) {
        NotificationInfo notificationInfo = new NotificationInfo(id, tag, packageName, userId);
        synchronized (mNotifications) {
            List<NotificationInfo> list = mNotifications.get(packageName);
            if (list == null) {
                list = new ArrayList<>();
                mNotifications.put(packageName, list);
            }
            if (!list.contains(notificationInfo)) {
                list.add(notificationInfo);
            }
        }
    }

    @Override
    public void cancelAllNotification(String packageName, int userId) {
        List<NotificationInfo> infos = new ArrayList<>();
        synchronized (mNotifications) {
            List<NotificationInfo> list = mNotifications.get(packageName);
            if (list != null) {
                int count = list.size();
                for (int i = count - 1; i >= 0; i--) {
                    NotificationInfo info = list.get(i);
                    if (info.userId == userId) {
                        infos.add(info);
                        list.remove(i);
                    }
                }
            }
        }
        for (NotificationInfo info : infos) {
            VLog.d(TAG, "cancel " + info.tag + " " + info.id);
            mNotificationManager.cancel(info.tag, info.id);
        }
    }

    private static class NotificationInfo {
        int id;
        String tag;
        String packageName;
        int userId;

        NotificationInfo(int id, String tag, String packageName, int userId) {
            this.id = id;
            this.tag = tag;
            this.packageName = packageName;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof NotificationInfo) {
                NotificationInfo that = (NotificationInfo) obj;
                return that.id == id && TextUtils.equals(that.tag, tag)
                        && TextUtils.equals(packageName, that.packageName)
                        && that.userId == userId;
            }
            return super.equals(obj);
        }
    }

    public static void registerDefaultChannelIfNeeded(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }
        final NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null || VirtualCore.get().getMasterTargetSdk() < Build.VERSION_CODES.O) {
            return;
        }
        if (notificationManager.getNotificationChannel(VA_DEFAULT_CHANNEL_ID) == null) {
            final NotificationChannel defaultChannel = new NotificationChannel(VA_DEFAULT_CHANNEL_ID,
                    VA_DEFAULT_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(defaultChannel);
        }
    }

    private static class Record {
        String pkg;
        int vuid;
        HashSet<String> channelIds = new HashSet<>();  // channelIds

        public Record(String pkg, int vuid) {
            this.pkg = pkg;
            this.vuid = vuid;
        }

        public Record(Parcel source) {
            this.pkg = source.readString();
            this.vuid = source.readInt();
            final int size = source.readInt();
            for (int i = 0; i < size; i++) {
                channelIds.add(source.readString());
            }
        }

        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.pkg);
            dest.writeInt(this.vuid);
            final int size = channelIds.size();
            dest.writeInt(size);
            for (String channelId : channelIds) {
                dest.writeString(channelId);
            }
        }

        @Override
        public String toString() {
            return pkg + " " + vuid + " " + Arrays.toString(channelIds.toArray());
        }
    }

    private String recordKey(String pkg, int vuid) {
        return pkg + "|" + vuid;
    }

    private Record getRecord(String pkg, int vuid) {
        final String key = recordKey(pkg, vuid);
        synchronized (mRecords) {
            return mRecords.get(key);
        }
    }

    private Record getOrCreateRecord(String pkg, int vuid) {
        final String key = recordKey(pkg, vuid);
        synchronized (mRecords) {
            Record r = mRecords.get(key);
            if (r == null) {
                r = new Record(pkg, vuid);
                mRecords.put(key, r);
            }
            return r;
        }
    }

    void dumpAll() {
        Iterator iterator = mRecords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Record> entry = (Map.Entry)iterator.next();
            VLog.v(TAG, entry.getKey() + " " + entry.getValue().toString());
        }
    }

    @Override
    public List<String> getNotificationChannels(String pkg, int vuid) {
        Record r = getRecord(pkg, vuid);
        if (r == null) {
            return null;
        }
        List<String> channelIds = new ArrayList<>();
        synchronized (mRecords) {
            channelIds.addAll(r.channelIds);
        }
        return channelIds;
    }

    @Override
    public void createNotificationChannel(String pkg, int vuid, String channelId) {
        if (pkg == null || channelId == null) {
            VLog.w(TAG, "create channel fail null package or channelId");
            return;
        }
        Record record = getOrCreateRecord(pkg, vuid);
        if (record == null) {
            VLog.w(TAG, "create channel fail of Invalid package");
            return;
        }
        if (NotificationChannel.DEFAULT_CHANNEL_ID.equals(channelId)) {
            VLog.w(TAG, "create channel fail of Reserved id");
            return;
        }
        boolean existing = record.channelIds.contains(channelId);
        if (!existing) {
            synchronized (mRecords) {
                record.channelIds.add(channelId);
                VLog.d(TAG, "create channel " + pkg + "|" + vuid + " channelId " + channelId);
                syncConfigLocked(); // sync db
            }
        }
    }

    public void removePackageChannels(int vuid, String pkg) {
        final String key = recordKey(pkg, vuid);
        synchronized (mRecords) {
            Record record = mRecords.remove(key);
            if (record != null) {
                VLog.i(TAG, "removePackageChannels " + key);
                syncConfigLocked();
            }
        }
    }

    @Override
    public void deleteNotificationChannel(String pkg, int vuid, String channelId) {
        Record r = getRecord(pkg, vuid);
        if (r == null) {
            return;
        }
        synchronized (mRecords) {
            boolean existing = r.channelIds.contains(channelId);
            if (existing) {
                VLog.d(TAG, "delete channel " + pkg + "|" + vuid + " channelId " + channelId);
                r.channelIds.remove(channelId);
            }
            if (r.channelIds.isEmpty()) {
                mRecords.remove(recordKey(pkg, vuid));
            }
            if (existing) {
                syncConfigLocked();  // sync db
            }
        }
    }

    private void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (IOException e) {
            VLog.w(TAG, "Failed to close resource", e);
        }
    }

    private void loadNotificationConfig() {
        if (!mConfigFile.exists()) {
            return;
        }
        int readlen = 0;
        final byte[] bytes = new byte[(int)mConfigFile.length()];
        FileInputStream is = null;
        try {
            is = new FileInputStream(mConfigFile);
            readlen = is.read(bytes);
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                closeQuietly(is);
            }
        }
        if (bytes == null || bytes.length != readlen) {
            mConfigFile.delete();
            return;
        }

        Parcel parcel = Parcel.obtain();
        parcel.unmarshall(bytes, 0, bytes.length);
        parcel.setDataPosition(0);
        int version = parcel.readInt();
        if (version != CONFIG_VERSION) {
            VLog.d(TAG, "loadConfig: version mismatch %d vs %d", version, CONFIG_VERSION);
        }

        synchronized (mRecords) {
            try {
                final int size = parcel.readInt();
                for (int i = 0; i < size; i++) {
                    String key = parcel.readString();  // pkg|vuid
                    Record record = new Record(parcel);
                    mRecords.put(key, record);
                }
            } catch (Exception e) {
                mConfigFile.delete();
                syncConfigLocked();
            }
        }
        parcel.recycle();
    }

    private void syncConfigLocked() {
        Parcel parcel = Parcel.obtain();
        parcel.writeInt(CONFIG_VERSION);
        parcel.writeInt(mRecords.size());
        Iterator iterator = mRecords.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Record> entry = (Map.Entry)iterator.next();
            parcel.writeString(entry.getKey());
            entry.getValue().writeToParcel(parcel, 0);
        }

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(mConfigFile);
            os.write(parcel.marshall());
            os.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                closeQuietly(os);
            }
        }
        parcel.recycle();
    }
}
