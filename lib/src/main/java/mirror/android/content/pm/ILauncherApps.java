package mirror.android.content.pm;

import android.os.IBinder;
import android.os.IInterface;

import mirror.MethodParams;
import mirror.RefClass;
import mirror.RefStaticMethod;

/**
 * @author Lody
 */
public class ILauncherApps {
    public static Class<?> TYPE = RefClass.load(ILauncherApps.class, "android.content.pm.IShortcutService");

    public static final class Stub {
        public static Class<?> TYPE = RefClass.load(Stub.class, "android.content.pm.ILauncherApps$Stub");
        @MethodParams({IBinder.class})
        public static RefStaticMethod<IInterface> asInterface;
    }
}
