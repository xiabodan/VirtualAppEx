package com.lody.virtual.client.hook.proxies.location;

import android.content.Context;
import android.location.ILocationListener;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;

import com.lody.virtual.client.hook.base.BinderInvocationProxy;
import com.lody.virtual.client.hook.base.Inject;
import com.lody.virtual.client.hook.base.LogInvocation;
import com.lody.virtual.client.hook.base.MethodProxy;
import com.lody.virtual.client.hook.base.ReplaceLastPkgMethodProxy;
import com.lody.virtual.client.hook.base.StaticMethodProxy;
import com.lody.virtual.client.hook.utils.MethodParameterUtils;
import com.lody.virtual.client.stub.VASettings;
import com.lody.virtual.helper.Features;
import com.lody.virtual.helper.PackageNames;
import com.lody.virtual.helper.utils.VLog;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mirror.android.location.ILocationManager;
import mirror.android.location.LocationRequestL;

/**
 * @author Lody
 * @see android.location.LocationManager
 */
@LogInvocation(LogInvocation.Condition.ALWAYS)
@Inject(MethodProxies.class)
public class LocationManagerStub extends BinderInvocationProxy {
    private static final boolean DEBUG = Features.FEATURE_DEBUG_SUPPORT;
    private static final String TAG = "LocationStub";
    private static final String FUSED_PROVIDER = "fused";
    private final Map<ILocationListener, LocationListenerDelegate> mDelegates = new HashMap<ILocationListener, LocationListenerDelegate>();

    public LocationManagerStub() {
        super(ILocationManager.Stub.asInterface, Context.LOCATION_SERVICE);
    }

    private void fixLocationRequest(Object request) {
        if (request != null) {
            if (LocationRequestL.mHideFromAppOps != null) {
                LocationRequestL.mHideFromAppOps.set(request, false);
            }
            if (LocationRequestL.mWorkSource != null) {
                LocationRequestL.mWorkSource.set(request, null);
            }
        }
    }

    @Override
    protected void onBindMethods() {
        super.onBindMethods();
        final FakeLocationHandler getAllProvidersHandler = new FakeLocationHandler("getAllProvidersHandler",
                Arrays.asList(LocationManager.NETWORK_PROVIDER, LocationManager.GPS_PROVIDER));
        final FakeLocationHandler getBestProviderHandler = new FakeLocationHandler("getBestProviderHandler", LocationManager.NETWORK_PROVIDER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
            addMethodProxy(new FakeLocationHandler("setExtraLocationControllerPackageEnabled", null, true));
            addMethodProxy(new FakeLocationHandler("setExtraLocationControllerPackage", null, true));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P ) {
            addMethodProxy(new FakeLocationHandler("injectLocation", Boolean.TRUE, true));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            addMethodProxy(new requestLocationUpdates());
            addMethodProxy(new removeUpdates());
            addMethodProxy(new FakeLocationHandler("registerGnssStatusCallback", true));
            addMethodProxy(new FakeLocationHandler("unregisterGnssStatusCallback"));
            addMethodProxy(new FakeLocationHandler("addGnssMeasurementsListener", true));
            addMethodProxy(new FakeLocationHandler("removeGnssMeasurementsListener"));
            addMethodProxy(new FakeLocationHandler("addGnssNavigationMessageListener", true));
            addMethodProxy(new FakeLocationHandler("removeGnssNavigationMessageListener"));
            addMethodProxy(new FakeLocationHandler("getGnssBatchSize", Integer.valueOf(0)));
            addMethodProxy(new FakeLocationHandler("addGnssBatchingCallback", true));
            addMethodProxy(new FakeLocationHandler("removeGnssBatchingCallback"));
            addMethodProxy(new FakeLocationHandler("startGnssBatch", true));
            addMethodProxy(new FakeLocationHandler("flushGnssBatch"));
            addMethodProxy(new FakeLocationHandler("stopGnssBatch", true));
            addMethodProxy(new locationCallbackFinished());
            addMethodProxy(getAllProvidersHandler);
            addMethodProxy(getBestProviderHandler);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addMethodProxy(new ReplaceLastPkgMethodProxy("addTestProvider"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("removeTestProvider"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("setTestProviderLocation"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("clearTestProviderLocation"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("setTestProviderEnabled"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("clearTestProviderEnabled"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("setTestProviderStatus"));
            addMethodProxy(new ReplaceLastPkgMethodProxy("clearTestProviderStatus"));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addGpsMeasurementsListener", true));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addGpsNavigationMessageListener", true));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeGpsMeasurementListener", 0));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeGpsNavigationMessageListener", 0));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("requestGeofence", 0));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeGeofence", 0));
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addProximityAlert", 0));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("addNmeaListener", 0));
            addMethodProxy(new FakeReplaceLastPkgMethodProxy("removeNmeaListener", 0));
        }
    }

    class locationCallbackFinished extends ReplaceLastPkgMethodProxy {
        public locationCallbackFinished() {
            super("locationCallbackFinished");
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                return true;
            }
            final int index = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, ILocationListener.class, 0);
            if (index >= 0) {
                final ILocationListener listener = (ILocationListener)args[index];
                synchronized (mDelegates) {
                    final LocationListenerDelegate delegate = mDelegates.get(listener);
                    if (delegate != null) {
                        args[index] = delegate;
                    }
                    VLog.d(TAG, "locationCallbackFinished: %s -> %s", listener, delegate);
                }
            }
            return super.call(who, method, args);
        }
    }

    final private class LocationListenerDelegate extends ILocationListener.Stub {
        private final String TAG = LocationManagerStub.TAG + LocationListenerDelegate.class.getSimpleName();
        private final ILocationListener mOrigListener;
        private String mOrigPrivider;
        private boolean isBlocked;

        LocationListenerDelegate(ILocationListener listener, String provider, boolean isBlocked) {
            mOrigListener = listener;
            mOrigPrivider = provider;
            this.isBlocked = isBlocked;
        }

        void updateProvider(String provider) {
            mOrigPrivider = provider;
        }

        @Override
        public void onLocationChanged(Location location) throws RemoteException {
            if (location != null) {
                location.setProvider(mOrigPrivider);
                if (isBlocked) {
                    location.reset();
                }
            }
            VLog.i(TAG, "onLocationChanged: location %s", location);
            mOrigListener.onLocationChanged(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) throws RemoteException {
            VLog.i(TAG, "onStatusChanged: provider %s, status %s, extras %s", provider, status, extras);
            mOrigListener.onStatusChanged(provider, status, extras);
        }

        @Override
        public void onProviderEnabled(String provider) throws RemoteException {
            VLog.i(TAG, "onProviderEnabled: provider %s", provider);
            mOrigListener.onProviderEnabled(provider);
        }

        @Override
        public void onProviderDisabled(String provider) throws RemoteException {
            VLog.i(TAG, "onProviderDisabled: provider %s", provider);
            mOrigListener.onProviderDisabled(provider);
        }
    }

    private class requestLocationUpdates extends MethodProxy {
        private List<String> mProviderList = null;
        private boolean isBlocked;
        private LocationManager mLocationManager = null;
        private LocationProvider mFusedLocationProvider = null;
        private final Context context;

        requestLocationUpdates() {
            context = getHostContext();
        }

        @Override
        public String getMethodName() {
            return "requestLocationUpdates";
        }

        private void updateList() {
            synchronized (this) {
                if (mProviderList == null) {
                    try {
                        if (mLocationManager == null) {
                            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        }
                        mProviderList = mLocationManager.getProviders(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mProviderList = new ArrayList<String>();
                    }
                }
            }
        }

        private boolean checkIfCanUseFusedProvider() {
            if (!TextUtils.equals(getAppPkg(), PackageNames.GMS_PKG_NAME)) {
                return false;
            }
            synchronized (this) {
                if (mFusedLocationProvider == null) {
                    try {
                        if (mLocationManager == null) {
                            mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                        }
                        mFusedLocationProvider = mLocationManager.getProvider(FUSED_PROVIDER);
                        if (mFusedLocationProvider == null) {
                            return false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }
            return true;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (args != null && args.length > 0 && LocationRequestL.isInstance(args[0])) {
                final Object request = args[0];
                fixLocationRequest(request);
                final String provider = LocationRequestL.mProvider(request);
                final int listenerIdx = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, ILocationListener.class, 0);
                if (provider != null && listenerIdx >= 0) {
                    if (!TextUtils.equals(provider, FUSED_PROVIDER) && checkIfCanUseFusedProvider()) {
                        LocationRequestL.mProvider(request, FUSED_PROVIDER);
                        VLog.w(TAG, "requestLocationUpdates: force set provider from %s to %s", provider, LocationRequestL.mProvider(request));
                    }
                    final ILocationListener listener = (ILocationListener)args[listenerIdx];
                    synchronized (mDelegates) {
                        LocationListenerDelegate delegate = mDelegates.get(listener);
                        if (delegate == null) {
                            delegate = new LocationListenerDelegate(listener, provider, isBlocked);
                            mDelegates.put(listener, delegate);
                        } else {
                            delegate.updateProvider(provider);
                        }
                        args[listenerIdx] = delegate;
                    }
                }
                if (DEBUG) {
                    updateList();
                    VLog.d(TAG, "requestLocationUpdates: request %s from %s, support list %s",
                            request, getAppPkg(), Arrays.toString(mProviderList.toArray(new String[0])));
                }
            }
            return super.call(who, method, args);
        }
    }

    class removeUpdates extends ReplaceLastPkgMethodProxy {

        public removeUpdates() {
            super("removeUpdates");
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                // TODO
                return 0;
            }
            final int index = MethodParameterUtils.findFirstObjectIndexForClassInArgs(args, ILocationListener.class, 0);
            if (index >= 0) {
                final ILocationListener listener = (ILocationListener)args[index];
                synchronized (mDelegates) {
                    final LocationListenerDelegate delegate = mDelegates.get(listener);
                    if (delegate != null) {
                        args[index] = delegate;
                    }
                }
            }
            return super.call(who, method, args);
        }
    }

    private class FakeLocationHandler extends StaticMethodProxy {
        private final Object fakeResult;
        private boolean forceFake = false;

        public FakeLocationHandler(String name, Object fakeResult) {
            super(name);
            this.fakeResult = fakeResult;
        }

        public FakeLocationHandler(String name) {
            super(name);
            this.fakeResult = null;
        }

        public FakeLocationHandler(String name, Object fakeResult, boolean forceFake) {
            super(name);
            this.fakeResult = fakeResult;
            this.forceFake = forceFake;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (forceFake) {
                return fakeResult;
            }
            MethodParameterUtils.replaceLastAppPkg(args);
            return method.invoke(who, args);
        }
    }

    private static class FakeReplaceLastPkgMethodProxy extends ReplaceLastPkgMethodProxy {
        private Object mDefValue;

        private FakeReplaceLastPkgMethodProxy(String name, Object def) {
            super(name);
            mDefValue = def;
        }

        @Override
        public Object call(Object who, Method method, Object... args) throws Throwable {
            if (isFakeLocationEnable()) {
                return mDefValue;
            }
            return super.call(who, method, args);
        }
    }
}
