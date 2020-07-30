package mirror.android.location;

import mirror.RefBoolean;
import mirror.RefClass;
import mirror.RefMethod;
import mirror.RefObject;

public class LocationRequestL {
    public static Class<?> TYPE = RefClass.load(LocationRequestL.class, "android.location.LocationRequest");
    public static RefBoolean mHideFromAppOps;
    public static RefObject<Object> mWorkSource;
    public static RefObject<String> mProvider;
    public static RefMethod<String> getProvider;

    public static boolean isInstance(Object object) {
        if (TYPE != null) {
            return TYPE.isInstance(object);
        }
        return false;
    }

    public static String mProvider(Object thiz) {
        return mProvider != null ? mProvider.get(thiz) : null;
    }

    public static void mProvider(Object thiz, String value) {
        if (mProvider != null) {
            mProvider.set(thiz, value);
        }
    }
}
