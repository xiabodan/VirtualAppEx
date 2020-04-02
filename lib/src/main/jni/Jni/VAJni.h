//
// VirtualApp Native Project
//

#ifndef NDK_CORE_H
#define NDK_CORE_H

#include <jni.h>
#include <stdlib.h>


#include "Helper.h"
#include "Foundation/VMPatch.h"
#include "Foundation/IOUniformer.h"

extern alias_ref<jclass> nativeEngineClass;

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *reserved);
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved);

enum {
    SDK_VERSION_FROYO = 8,               /* 8, FROYO */
    SDK_VERSION_GINGERBREAD,             /* 9, GINGERBREAD */
    SDK_VERSION_GINGERBREAD_MR1,         /* 10, Android 2.3.3 */
    SDK_VERSION_HONEYCOMB,               /* 11, Android 3.0 */
    SDK_VERSION_HONEYCOMB_MR1,           /* 12, Android 3.1 */
    SDK_VERSION_HONEYCOMB_MR2,           /* 13, Android 3.2 */
    SDK_VERSION_ICE_CREAM_SANDWICH,      /* 14, Android 4.0 */
    SDK_VERSION_ICE_CREAM_SANDWICH_MR1,  /* 15, Android 4.0.3 */
    SDK_VERSION_JELLY_BEAN,              /* 16, Android 4.1 */
    SDK_VERSION_JELLY_BEAN_MR1,          /* 17, Android 4.2 */
    SDK_VERSION_JELLY_BEAN_MR2,          /* 18, Android 4.3 */
    SDK_VERSION_KITKAT,                  /* 19, Android 4.4 */
    SDK_VERSION_KITKAT_WATCH,            /* 20, Android 4.4W */
    SDK_VERSION_LOLLIPOP,                /* 21, Android 5.0 */
    SDK_VERSION_LOLLIPOP_MR1,            /* 22, Android 5.1 */
    SDK_VERSION_M,                       /* 23, Android 6.0 */
    SDK_VERSION_N,                       /* 24, Android 7.0 */
    SDK_VERSION_N_MR1,                   /* 25, Android 7.1 */
    SDK_VERSION_O,                       /* 26, Android 8.0 */
    SDK_VERSION_O_MR1,                   /* 27, Android 8.1 */
    SDK_VERSION_P,                       /* 28, Android 9.0 */
};

#endif //NDK_CORE_H
