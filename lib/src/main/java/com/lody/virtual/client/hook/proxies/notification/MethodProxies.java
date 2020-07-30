package com.lody.virtual.client.hook.proxies.notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.os.Build;
import android.os.UserHandle;

import com.lody.virtual.client.VClientImpl;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.ipc.VNotificationManager;
import com.lody.virtual.helper.utils.ArrayUtils;
import com.lody.virtual.helper.utils.VLog;
import com.lody.virtual.os.VUserHandle;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import mirror.android.content.pm.ParceledListSlice;

/**
 * @author Lody
 */

@SuppressWarnings("unused")
class MethodProxies {
    private static final String TAG = "NotificationManagerStub";
    static class EnqueueNotification extends MethodProxy {

        @Override
        public String getMethodName() {
            return "enqueueNotification";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            if(true){
                return 0;
            }
            if(Build.MODEL.contains("vivo") || Build.MODEL.contains("ZTE")){
                return 0;
            }
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int id = (int) args[idIndex];
            id = VNotificationManager.get().dealNotificationId(id, pkg, null, getAppUserId());
            args[idIndex] = id;
            Notification notification = (Notification) args[notificationIndex];
            if (!VNotificationManager.get().dealNotification(id, notification, pkg)) {
                return 0;
            }
            VNotificationManager.get().addNotification(id, null, pkg, getAppUserId());
            args[0] = getHostPkg();
            return method.invoke(who, args);
        }
    }

    /* package */ static class EnqueueNotificationWithTag extends MethodProxy {

        @Override
        public String getMethodName() {
            return "enqueueNotificationWithTag";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            if(true){
                return 0;
            }
            if(Build.MODEL.contains("vivo") || Build.MODEL.contains("ZTE")){
                return 0;
            }
            int notificationIndex = ArrayUtils.indexOfFirst(args, Notification.class);
            int idIndex = ArrayUtils.indexOfFirst(args, Integer.class);
            int tagIndex = (Build.VERSION.SDK_INT >= 18 ? 2 : 1);
            int id = (int) args[idIndex];
            String tag = (String) args[tagIndex];

            id = VNotificationManager.get().dealNotificationId(id, pkg, tag, getAppUserId());
            tag = VNotificationManager.get().dealNotificationTag(id, pkg, tag, getAppUserId());
            args[idIndex] = id;
            args[tagIndex] = tag;
            //key(tag,id)
            Notification notification = (Notification) args[notificationIndex];
            if (!VNotificationManager.get().dealNotification(id, notification, pkg)) {
                return 0;
            }
            VNotificationManager.get().addNotification(id, tag, pkg, getAppUserId());
            args[0] = getHostPkg();
            if (Build.VERSION.SDK_INT >= 18 && args[1] instanceof String) {
                args[1] = getHostPkg();
            }
            return method.invoke(who, args);
        }
    }

    /* package */ static class EnqueueNotificationWithTagPriority extends EnqueueNotificationWithTag {

        @Override
        public String getMethodName() {
            return "enqueueNotificationWithTagPriority";
        }
    }

    /* package */ static class CancelNotificationWithTag extends MethodProxy {

        @Override
        public String getMethodName() {
            return "cancelNotificationWithTag";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = MethodParameterUtils.replaceFirstAppPkg(args);
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            if(true){
                return 0;
            }
            if(Build.MODEL.contains("vivo") || Build.MODEL.contains("ZTE")){
                return 0;
            }
            String tag = (String) args[1];
            int id = (int) args[2];
            id = VNotificationManager.get().dealNotificationId(id, pkg, tag, getAppUserId());
            tag = VNotificationManager.get().dealNotificationTag(id, pkg, tag, getAppUserId());

            args[1] = tag;
            args[2] = id;
            return method.invoke(who, args);
        }
    }

    /**
     * @author 陈磊.
     */
    /* package */ static class CancelAllNotifications extends MethodProxy {

        @Override
        public String getMethodName() {
            return "cancelAllNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = MethodParameterUtils.replaceFirstAppPkg(args);
            if (VirtualCore.get().isAppInstalled(pkg)) {
                VNotificationManager.get().cancelAllNotification(pkg, getAppUserId());
                return 0;
            }
            return method.invoke(who, args);
        }
    }

    static class AreNotificationsEnabledForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "areNotificationsEnabledForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            return VNotificationManager.get().areNotificationsEnabledForPackage(pkg, getAppUserId());
        }
    }

    static class SetNotificationsEnabledForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationsEnabledForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            String pkg = (String) args[0];
            if (getHostPkg().equals(pkg)) {
                return method.invoke(who, args);
            }
            int enableIndex = ArrayUtils.indexOfFirst(args, Boolean.class);
            boolean enable = (boolean) args[enableIndex];
            VNotificationManager.get().setNotificationsEnabledForPackage(pkg, enable, getAppUserId());
            return 0;
        }
    }

    static class  CreateNotificationChannelGroups extends MethodProxy {
        @Override
        public String getMethodName() {
            return "createNotificationChannelGroups";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class  shouldInterceptSound extends MethodProxy {
        @Override
        public String getMethodName() {
            return "shouldInterceptSound";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class  CreateNotificationChannelsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "createNotificationChannelsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class OnlyHasDefaultChannel extends MethodProxy {
        @Override
        public String getMethodName() {
            return "onlyHasDefaultChannel";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class GetActiveNotifications extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getActiveNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class GetHistoricalNotifications extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getHistoricalNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class CancelNotificationFromListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "cancelNotificationFromListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class SetInterruptionFilter extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setInterruptionFilter";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class IsNotificationPolicyAccessGranted extends MethodProxy {
        @Override
        public String getMethodName() {
            return "isNotificationPolicyAccessGranted";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class IsNotificationPolicyAccessGrantedForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "isNotificationPolicyAccessGrantedForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class GetAppActiveNotifications extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getAppActiveNotifications";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class SetNotificationPolicyAccessGranted extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationPolicyAccessGranted";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class RemoveAutomaticZenRules extends MethodProxy {
        @Override
        public String getMethodName() {
            return "removeAutomaticZenRules";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class SetNotificationPolicy extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setNotificationPolicy";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class NotifyConditions extends MethodProxy {
        @Override
        public String getMethodName() {
            return "notifyConditions";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class GetNotificationChannelGroupsFromPrivilegedListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelGroupsFromPrivilegedListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannelsFromPrivilegedListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelsFromPrivilegedListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  UpdateNotificationChannelFromPrivilegedListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "updateNotificationChannelFromPrivilegedListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannelGroupsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelGroupsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannel extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannel";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                args[2] = VirtualCore.get().getHostPkg();
            }
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannelsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetActiveNotificationsFromListener extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getActiveNotificationsFromListener";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNumNotificationChannelsForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNumNotificationChannelsForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  DeleteNotificationChannel extends MethodProxy {
        @Override
        public String getMethodName() {
            return "deleteNotificationChannel";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  DeleteNotificationChannelGroup extends MethodProxy {
        @Override
        public String getMethodName() {
            return "deleteNotificationChannelGroup";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  ClearData extends MethodProxy {
        @Override
        public String getMethodName() {
            return "clearData";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  EnqueueToast extends MethodProxy {
        @Override
        public String getMethodName() {
            return "enqueueToast";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  CancelToast extends MethodProxy {
        @Override
        public String getMethodName() {
            return "cancelToast";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  SetShowBadge extends MethodProxy {
        @Override
        public String getMethodName() {
            return "setShowBadge";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  CanShowBadge extends MethodProxy {
        @Override
        public String getMethodName() {
            return "canShowBadge";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  AreNotificationsEnabled extends MethodProxy {
        @Override
        public String getMethodName() {
            return "areNotificationsEnabled";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class  removeEdgeNotification extends MethodProxy {
        @Override
        public String getMethodName() {
            return "removeEdgeNotification";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class  GetPackageImportance extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getPackageImportance";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }
    }

    static class  UpdateNotificationChannelForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "updateNotificationChannelForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetNotificationChannelForPackage extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getNotificationChannelForPackage";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    static class  GetDeletedChannelCount extends MethodProxy {
        @Override
        public String getMethodName() {
            return "getDeletedChannelCount";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            args[0] = VirtualCore.get().getHostPkg();
            return method.invoke(who,args);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    static class  getNotificationChannelGroup extends MethodProxy {
        ThreadLocal<String> mPackageName = new ThreadLocal<String>();

        @Override
        public String getMethodName() {
            return "getNotificationChannelGroup";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            mPackageName.set(null);
            final int index = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, String.class, 0);
            if (index >= 0) {
                String pkg = (String) args[index];
                mPackageName.set(pkg);
                args[index] = VirtualCore.get().getHostPkg();
            }
            // MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }

        @Override
        public Object afterCall(Object who, Method method, Object[] args, Object result) throws Throwable {
            final int userId = VUserHandle.getUserId(VClientImpl.get().getVUid());
            if ((result instanceof NotificationChannelGroup)) {
                List<String> channelIds = VNotificationManager.get().getNotificationChannels(mPackageName.get(), userId);
                if (channelIds == null) {
                    return null;
                } else {
                    VLog.v(TAG, "getNotificationChannelGroup channelIds " + Arrays.toString(channelIds.toArray()));
                    Set<String> channelIdsSet = new HashSet<>(channelIds);
                    final NotificationChannelGroup group = (NotificationChannelGroup) result;
                    final List<NotificationChannel> channels = group.getChannels();
                    Iterator<NotificationChannel> iterator1 = channels.iterator();
                    while (iterator1.hasNext()) {
                        NotificationChannel channel = iterator1.next();
                        if (!channelIdsSet.contains(channel.getId())) {
                            iterator1.remove();
                        }
                    }
                }
            }
            return super.afterCall(who, method, args, result);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    static class  getNotificationChannelGroups extends MethodProxy {
        ThreadLocal<String> mPackageName = new ThreadLocal<String>();

        @Override
        public String getMethodName() {
            return "getNotificationChannelGroups";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            mPackageName.set(null);
            final int index = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, String.class, 0);
            if (index >= 0) {
                String pkg = (String) args[index];
                mPackageName.set(pkg);
                args[index] = VirtualCore.get().getHostPkg();
            }
            return method.invoke(who,args);
        }

        @Override
        public Object afterCall(Object who, Method method, Object[] args, Object result) throws Throwable {
            final int userId = VUserHandle.getUserId(VClientImpl.get().getVUid());
            if (ParceledListSlice.isInstance(result)) {
                List<String> channelIds = VNotificationManager.get().getNotificationChannels(mPackageName.get(), userId);
                if (channelIds == null) {
                    return MethodParameterUtils.convertListToParceledListSliceIfNeeded(method, Collections.emptyList());
                } else {
                    VLog.v(TAG, "getNotificationChannelGroups channelIds " + Arrays.toString(channelIds.toArray()));
                    Set<String> channelIdsSet = new HashSet<>(channelIds);
                    final List<NotificationChannelGroup> groups = ParceledListSlice.getList(result);
                    Iterator<NotificationChannelGroup> iterator = groups.iterator();
                    while (iterator.hasNext()) {
                        NotificationChannelGroup group = iterator.next();
                        final List<NotificationChannel> channels = group.getChannels();
                        Iterator<NotificationChannel> iterator1 = channels.iterator();
                        while (iterator1.hasNext()) {
                            NotificationChannel channel = iterator1.next();
                            if (!channelIdsSet.contains(channel.getId())) {
                                iterator1.remove();
                            }
                        }
                    }
                }
            }
            return super.afterCall(who, method, args, result);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    static class  createNotificationChannels extends MethodProxy {
        @Override
        public String getMethodName() {
            return "createNotificationChannels";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            MethodParameterUtils.replaceFirstAppPkg(args);
            return method.invoke(who,args);
        }

        @Override
        public boolean beforeCall(Object who, Method method, Object... args) {
            final int packageNameIndex = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, String.class, 0);
            if (packageNameIndex >= 0) {
                final String packageName = (String) args[packageNameIndex];
                final int userId = VUserHandle.getUserId(VClientImpl.get().getVUid());
                final int channelsIndex = 1;
                if (args[channelsIndex] != null && ParceledListSlice.isInstance(args[channelsIndex])) {
                    final List<NotificationChannel> channels = ParceledListSlice.getList(args[channelsIndex]);
                    for (NotificationChannel notificationChannel : channels) {
                        VNotificationManager.get().createNotificationChannel(packageName, userId, notificationChannel.getId());
                        VLog.v(TAG, "createNotificationChannels: " + notificationChannel);
                    }
                }
            }
            return super.beforeCall(who, method, args);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    static class  getNotificationChannels extends MethodProxy {
        ThreadLocal<String> mPackageName = new ThreadLocal<String>();

        @Override
        public String getMethodName() {
            return "getNotificationChannels";
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            mPackageName.set(null);
            final int index = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, String.class, 0);
            if (index >= 0) {
                String pkg = (String) args[index];
                mPackageName.set(pkg);
                args[index] = VirtualCore.get().getHostPkg();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                args[1] = VirtualCore.get().getHostPkg();
            }
            return method.invoke(who,args);
        }

        @Override
        public Object afterCall(Object who, Method method, Object[] args, Object result) throws Throwable {
            final int userId = VUserHandle.getUserId(VClientImpl.get().getVUid());
            if (ParceledListSlice.isInstance(result)) {
                List<String> channelIds = VNotificationManager.get().getNotificationChannels(mPackageName.get(), userId);
                if (channelIds == null) {
                    return MethodParameterUtils.convertListToParceledListSliceIfNeeded(method, Collections.emptyList());
                } else {
                    VLog.v(TAG, "getNotificationChannels channelIds " + Arrays.toString(channelIds.toArray()));
                    Set<String> channelIdsSet = new HashSet<>(channelIds);
                    final List<NotificationChannel> channels = ParceledListSlice.getList(result);
                    Iterator<NotificationChannel> iterator = channels.iterator();
                    while (iterator.hasNext()) {
                        NotificationChannel channel = iterator.next();
                        if (!channelIdsSet.contains(channel.getId())) {
                            iterator.remove();
                        }
                    }
                }
            }
            return super.afterCall(who, method, args, result);
        }
    }
}
