// ISystemUpdateManager.aidl
package android.os;

// Declare any non-default types here with import statements

 import android.os.Bundle;
 import android.os.PersistableBundle;

 /** @hide */
 interface ISystemUpdateManager {
     Bundle retrieveSystemUpdateInfo();
     void updateSystemUpdateInfo(in PersistableBundle data);
 }
