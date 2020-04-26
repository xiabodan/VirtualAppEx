package lab.galaxy.yahfa.demoPlugin;

import android.util.Log;

import static lab.galaxy.yahfa.HookInfo.TAG;

public class Hook_MyApplication {
    public static String className = "com.example.myapplication.MainActivity";
    public static String methodName = "testYahfa";
    public static String methodSig =
            "(Ljava/lang/String;)Ljava/lang/String;";

    public static String hook(Object thiz, String a) {
        int uid = android.os.Process.myUid();
        Log.w(TAG, "in ClassWithVirtualMethod.testYahfa(): " + a + ": " + uid);
        return backup(thiz, a);
    }

    public static String backup(Object thiz, String a) {
        try {
            Log.w(TAG, "ClassWithVirtualMethod.tac() should not be here");
        }
        catch (Exception e) {

        }
        return "";
    }
}

