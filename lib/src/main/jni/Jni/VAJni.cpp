#include <elf.h>//
// VirtualApp Native Project
//
#include <Foundation/IOUniformer.h>
#include <fb/include/fb/Build.h>
#include <fb/include/fb/ALog.h>
#include <fb/include/fb/fbjni.h>
#include "VAJni.h"

using namespace facebook::jni;

static void jni_nativeLaunchEngine(alias_ref<jclass> clazz, JArrayClass<jobject> javaMethods,
                                   jstring packageName,
                                   jboolean isArt, jint apiLevel, jint cameraMethodType) {
    hookAndroidVM(javaMethods, packageName, isArt, apiLevel, cameraMethodType);
}


static void jni_nativeEnableIORedirect(alias_ref<jclass>, jstring selfSoPath, jint apiLevel,
                                       jint preview_api_level) {
    ScopeUtfString so_path(selfSoPath);
    IOUniformer::startUniformer(so_path.c_str(), apiLevel, preview_api_level);
}

static void jni_nativeIOWhitelist(alias_ref<jclass> jclazz, jstring _path) {
    ScopeUtfString path(_path);
    IOUniformer::whitelist(path.c_str());
}

static void jni_nativeIOForbid(alias_ref<jclass> jclazz, jstring _path) {
    ScopeUtfString path(_path);
    IOUniformer::forbid(path.c_str());
}


static void jni_nativeIORedirect(alias_ref<jclass> jclazz, jstring origPath, jstring newPath) {
    ScopeUtfString orig_path(origPath);
    ScopeUtfString new_path(newPath);
    IOUniformer::redirect(orig_path.c_str(), new_path.c_str());

}

static jstring jni_nativeGetRedirectedPath(alias_ref<jclass> jclazz, jstring origPath) {
    ScopeUtfString orig_path(origPath);
    const char *redirected_path = IOUniformer::query(orig_path.c_str());
    if (redirected_path != NULL) {
        return Environment::current()->NewStringUTF(redirected_path);
    }
    return NULL;
}

static jstring jni_nativeReverseRedirectedPath(alias_ref<jclass> jclazz, jstring redirectedPath) {
    ScopeUtfString redirected_path(redirectedPath);
    const char *orig_path = IOUniformer::reverse(redirected_path.c_str());
    return Environment::current()->NewStringUTF(orig_path);
}


alias_ref<jclass> nativeEngineClass;

// private api
template<typename T>
int findOffset(void *start, int regionStart, int regionEnd, T value) {

    if (NULL == start || regionEnd <= 0 || regionStart < 0) {
        return -1;
    }
    char *c_start = (char *) start;

    for (int i = regionStart; i < regionEnd; i += 4) {
        T *current_value = (T *) (c_start + i);
        if (value == *current_value) {
            ALOGD("found offset: %d", i);
            return i;
        }
    }
    return -2;
}

// Android 'L' makes __system_property_get a non-global symbol.
// Here we provide a stub which loads the symbol from libc via dlsym.
typedef int (*PFN_SYSTEM_PROP_GET)(const char *, char *);
int property_get(const char* name, char* value, const char* def)
{
    static PFN_SYSTEM_PROP_GET __real_system_property_get = NULL;
    if (!__real_system_property_get) {
        // libc.so should already be open, get a handle to it.
        void *handle = dlopen("libc.so", 0);
        if (handle) {
            __real_system_property_get = (PFN_SYSTEM_PROP_GET) dlsym(handle, "__system_property_get");
            dlclose(handle);
        }
    }
    int ret = -1;
    if (__real_system_property_get) {
        ret = (*__real_system_property_get)(name, value);
    }
    if (ret <= 0 && def != nullptr) {
        strcpy(value, def);
        ret = strlen(def);
    }
    return ret;
}

static int get_build_version_sdk()
{
    int retval;
    char prop_buffer[PROP_VALUE_MAX];
    retval = property_get("ro.build.version.sdk", prop_buffer, nullptr);
    if (retval > 0) {
        return atoi(prop_buffer);
    }
    return 0;
}


void androidPrivateApiEscape(JavaVM* jvm, int targetSdkVersion) {
    int sdk_int = get_build_version_sdk();
    ALOGD("androidPrivateApiEscape: sdk %d, target_sdk %d", sdk_int, targetSdkVersion);
    if (sdk_int < SDK_VERSION_O_MR1 || sdk_int == SDK_VERSION_O_MR1) {
        return;
    }
    void* runtime = *((void**)jvm + 1);
    const int MAX = 2000;
    int offsetOfVmExt = findOffset(runtime, 0, MAX, (long)jvm);
    ALOGD("offsetOfVmExt: %d", offsetOfVmExt);

    if (offsetOfVmExt < 0) {
        return;
    }

    int targetSdkVersionOffset = findOffset(runtime, offsetOfVmExt, MAX, targetSdkVersion);
    ALOGD("target: %d", targetSdkVersionOffset);

    if (targetSdkVersionOffset < 0) {
        return;
    }

    int32_t* targetSdkVersionAddr = (int32_t*)((char*)runtime + targetSdkVersionOffset);
#ifdef DEBUG
    for (int index = 0;index < 50;index ++) {
        ALOGD("targetSdkVersionAddr index[%d]%x", index, targetSdkVersionAddr[index]);
    }
#endif
#if defined(__LP64__)
    if (targetSdkVersionAddr[15] == 3 || targetSdkVersionAddr[15] == 2) {
        targetSdkVersionAddr[15] = 0;
        ALOGD("androidPrivateApiEscape: find hidden_policy_ targetSdkVersionAddr[15], 64bit runtime");
    }
#else
    if (targetSdkVersionAddr[11] == 3 || targetSdkVersionAddr[11] == 2) {
        targetSdkVersionAddr[11] = 0;
        ALOGD("androidPrivateApiEscape: find hidden_policy_ targetSdkVersionAddr[11], 32bit runtime");
    }
#endif
}
extern "C" {
uint32_t __attribute__((weak)) android_get_application_target_sdk_version();
}

JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM *vm, void *) {
    ALOGD("android_get_application_target_sdk_version %p", android_get_application_target_sdk_version);
    if (android_get_application_target_sdk_version) {
        ALOGD("android_get_application_target_sdk_version %d", android_get_application_target_sdk_version());
        int targetsdk = android_get_application_target_sdk_version();
        androidPrivateApiEscape(vm, targetsdk);
    }

    return initialize(vm, [] {
        nativeEngineClass = findClassStatic("com/lody/virtual/client/NativeEngine");
        nativeEngineClass->registerNatives({
                        makeNativeMethod("nativeEnableIORedirect",
                                         jni_nativeEnableIORedirect),
                        makeNativeMethod("nativeIOWhitelist",
                                         jni_nativeIOWhitelist),
                        makeNativeMethod("nativeIOForbid",
                                         jni_nativeIOForbid),
                        makeNativeMethod("nativeIORedirect",
                                         jni_nativeIORedirect),
                        makeNativeMethod("nativeGetRedirectedPath",
                                         jni_nativeGetRedirectedPath),
                        makeNativeMethod("nativeReverseRedirectedPath",
                                         jni_nativeReverseRedirectedPath),
                        makeNativeMethod("nativeLaunchEngine",
                                         jni_nativeLaunchEngine),
                }
        );
    });
}

extern "C" __attribute__((constructor)) void _init(void) {
    IOUniformer::init_env_before_all();
}


