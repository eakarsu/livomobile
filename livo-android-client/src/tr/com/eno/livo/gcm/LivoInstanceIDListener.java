package tr.com.eno.livo.gcm;


import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class LivoInstanceIDListener extends InstanceIDListenerService {

    private static final String TAG = "LivoInstanceIDListener";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */

    @Override
    public void onTokenRefresh() {
        // Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
        Intent intent = new Intent(this, LivoGCMRegistrationService.class);
        intent.setAction(LivoGCMActions.ID_CHANGED);
        startService(intent);
    }
}
