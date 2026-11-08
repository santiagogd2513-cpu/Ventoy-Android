#include <jni.h>

extern "C"
JNIEXPORT jstring JNICALL
Java_dev_ventoy_android_NativeBridge_getBridgeStatus(JNIEnv* env, jobject /* thiz */) {
    return env->NewStringUTF(
        "Native bridge loaded. Ventoy engine integration is not implemented yet."
    );
}
