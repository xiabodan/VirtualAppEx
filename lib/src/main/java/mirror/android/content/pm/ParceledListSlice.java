package mirror.android.content.pm;

import android.os.Parcelable;

import java.util.List;

import mirror.RefClass;
import mirror.RefConstructor;
import mirror.RefMethod;
import mirror.RefStaticMethod;
import mirror.RefStaticObject;

/**
 * @author Lody
 */

public class ParceledListSlice {
    public static RefStaticObject<Parcelable.Creator> CREATOR;
    public static Class<?> TYPE = RefClass.load(ParceledListSlice.class, "android.content.pm.ParceledListSlice");
    public static RefMethod<Boolean> append;
    public static RefConstructor<Parcelable> ctor;
    public static RefMethod<Boolean> isLastSlice;
    public static RefMethod<Parcelable> populateList;
    public static RefStaticMethod<Object> emptyList;
    public static RefMethod<Void> setLastSlice;
    public static RefMethod<List<?>> getList;

    public static List getList(Object thiz) {
        return getList != null ? getList.call(thiz, null) : null;
    }

    public static boolean isInstance(Object obj){
        if (TYPE != null) {
            return TYPE.isInstance(obj);
        }
        return false;
    }

    public static boolean isClass(Class clazz) {
        return TYPE != null && clazz == TYPE;
    }

    public static Object newInstance(List list) {
        if (ctor != null) {
            return ctor.newInstance(new Object[]{list});
        }
        return null;
    }

    public static Object emptyList() {
        return emptyList != null ? emptyList.call(null) : null;
    }
}
