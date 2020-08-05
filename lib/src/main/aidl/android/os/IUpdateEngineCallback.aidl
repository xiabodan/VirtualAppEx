// IUpdateEngineCallback.aidl
package android.os;

// Declare any non-default types here with import statements

/** @hide */
oneway interface IUpdateEngineCallback {
  /** @hide */
  void onStatusUpdate(int status_code, float percentage);
  /** @hide */
  void onPayloadApplicationComplete(int error_code);
}