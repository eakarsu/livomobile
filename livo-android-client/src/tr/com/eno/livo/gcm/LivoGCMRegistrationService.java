package tr.com.eno.livo.gcm;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import org.apache.thrift.TException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import tr.com.eno.livo.ConfigPool;
import tr.com.eno.livo.ThriftServicesPool;
import tr.com.eno.livo.WebContainer;
import tr.com.eno.livo.thrift.notification.DeviceRegistrationInfo;
import tr.com.eno.livo.thrift.notification.NotificationRegistrationService;

public class LivoGCMRegistrationService extends IntentService {


    private static String TAG = "LivoGCMRegistrationService";

    private ConfigPool configPool= WebContainer.getConfigPool();


    public LivoGCMRegistrationService(){

        super(TAG);
    }



    /**
     * This method is invoked on the worker thread with a request to process.
     * Only one Intent is processed at a time, but the processing happens on a
     * worker thread that runs independently from other application logic.
     * So, if this code takes a long time, it will hold up other requests to
     * the same IntentService, but it will not hold up anything else.
     * When all requests have been handled, the IntentService stops itself,
     * so you should not call {@link #stopSelf}.
     *
     * @param intent The value passed to {@link
     *               Context#startService(Intent)}.
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        ThriftServicesPool thrift = new ThriftServicesPool(this.configPool.getServerAddress(),
                this.configPool.getServerPort());


        try {

            thrift.openTransport();

            NotificationRegistrationService.Client notificationService = thrift
                    .getNotificationRegistrationService();

            String deviceIdHash = Base64.encodeToString(Settings.Secure.
                            getString(super.getApplicationContext().getContentResolver(),
                                    Settings.Secure.ANDROID_ID).getBytes("UTF-8"),
                    Base64.DEFAULT);

            DeviceRegistrationInfo info = notificationService.
                    checkDeviceRegistration(this.configPool.getToken(),
                            this.configPool.getApplicationName(),deviceIdHash
                            , "Android");
            if(info.isRegistered()&&intent.getAction().equalsIgnoreCase(LivoGCMActions.NO_ACTION))
                Log.d(TAG,"Client already registered.");
            else{
                if(info.getSenderIdentifier()==null || info.getSenderIdentifier().isEmpty()){
                    Log.d(TAG,"No sender identifier returned from server.");
                    return;

                }

                InstanceID instanceID = InstanceID.getInstance(this);

                String token = instanceID.getToken(info.getSenderIdentifier(),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                notificationService.registerDevice(this.configPool.getToken(),this.configPool.getApplicationName(),
                        "android",deviceIdHash,token);


                thrift.closeTransport();
                this.configPool.setGCMToken(token);
            }

        }  catch (TException e) {
            thrift.closeTransport();
            Log.e(TAG,"NotificationRegistrationService",e);
        } catch (UnsupportedEncodingException e) {
            thrift.closeTransport();
            Log.e(TAG,"Sometimes happens  Olric, do not worry :)");
        } catch (IOException e) {
            thrift.closeTransport();
            Log.e(TAG, " IO problem.");
        }


//        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
//        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

    }
}
