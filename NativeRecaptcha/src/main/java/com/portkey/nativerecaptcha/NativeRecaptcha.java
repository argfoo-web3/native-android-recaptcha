package com.portkey.nativerecaptcha;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class NativeRecaptcha {
    public interface Callback {
        void onSuccess(String captchaToken);
        void onFailure(Exception e);
    }
    public static Callback callback = null;
    public static void SetCallback(Callback cb)
    {
        callback = cb;
    }

    public static void VerifyGoogleReCAPTCHA(Context context, String siteKey) {
        if(callback == null)
        {
            Log.d("TAG", "Callback must be set before calling for recaptcha!");
            return;
        }
        // below line is use for getting our safety
        // net client and verify with reCAPTCHA
        SafetyNet.getClient(context).verifyWithRecaptcha(siteKey)
                // after getting our client we have
                // to add on success listener.
                .addOnSuccessListener(new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
                    @Override
                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
                        // in below line we are checking the response token.
                        if (response.getTokenResult().isEmpty()) {
                            Log.e("Recaptcha", "EMPTY TOKEN RESULT detected!");
                        }
                        callback.onSuccess(response.getTokenResult());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // this method is called when we get any error.
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            // below line is use to display an error message which we get.
                            Log.d("Recaptcha", "Error message: " +
                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
                        }
                        callback.onFailure(e);
                    }
                });
    }
}
