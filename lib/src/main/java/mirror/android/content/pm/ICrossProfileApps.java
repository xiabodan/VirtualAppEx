package mirror.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

public class ICrossProfileApps {
    public static Class<?> TYPE = RefClass.load(ICrossProfileApps.class, "android.content.pm.ICrossProfileApps");

    public static class Stub {
        public static Class<?> TYPE = RefClass.load(ICrossProfileApps.Stub.class, "android.content.pm.ICrossProfileApps$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
