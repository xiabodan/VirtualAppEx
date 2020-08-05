// IUpdateEngine.aidl
package android.os;

// Declare any non-default types here with import statements

 import android.os.IUpdateEngineCallback;

 /** @hide */
 interface IUpdateEngine {
   /** @hide */
   void applyPayload(String url,
                     in long payload_offset,
                     in long payload_size,
                     in String[] headerKeyValuePairs);
   /** @hide */
   boolean bind(IUpdateEngineCallback callback);
   /** @hide */
   boolean unbind(IUpdateEngineCallback callback);
   /** @hide */
   void suspend();
   /** @hide */
   void resume();
   /** @hide */
   void cancel();
   /** @hide */
   void resetStatus();
   /** @hide */
   boolean verifyPayloadApplicable(in String metadataFilename);
 }

