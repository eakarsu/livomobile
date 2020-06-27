package tr.com.eno.livo;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
import org.apache.cordova.CordovaActivity;
import org.apache.thrift.TException;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.Secure;
import android.util.Base64;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import tr.com.eno.livo.gcm.LivoGCMActions;
import tr.com.eno.livo.gcm.LivoGCMRegistrationService;
import tr.com.eno.livo.thrift.analytics.AnalyticsService;
import tr.com.eno.livo.thrift.analytics.Event;
import tr.com.eno.livo.thrift.analytics.EventRecordFailedError;
import tr.com.eno.livo.thrift.authc.AuthenticationService;
import tr.com.eno.livo.thrift.authc.CompanyAuthenticationResult;
import tr.com.eno.livo.thrift.authc.UserAuthenticationResult;
import tr.com.eno.livo.thrift.provision.ProvisioningService;
import tr.com.eno.livo.thrift.shared.AuthenticationToken;
import tr.com.eno.livo.thrift.shared.Profile;

public class WebContainer extends CordovaActivity {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    protected final static String TAG = "Container";

    protected final static String ATAG = "Container$EventHandler";

    private static ConfigPool cPool;

    private EventHandler eventHandler;

    private boolean ignoreUpdate;

    protected String deviceUUIDAsHash;

    protected EventHolder eventHolder;

    private AlertDialog dialog;

    public static ConfigPool getConfigPool() {

        return cPool;
    }

    //Belongs to Client asking for updates, must be removed after notification triggered updates.
    public void showDialog() {

        if (!WebContainer.this.ignoreUpdate)
            this.dialog.show();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        super.init();


        this.dialog = new AlertDialog.Builder(this)
                .setTitle("New Update Found")
                .setMessage("Do you want to update now ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), GetUpdates.class));
                        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                        finish();
                    }
                }).setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        WebContainer.this.ignoreUpdate = true;
                        dialog.cancel();
                    }
                }).create();


        String deviceUUID = Secure.getString(getApplicationContext()
                .getContentResolver(), Secure.ANDROID_ID);

        WebContainer.cPool = ConfigPool.getInstance(getApplicationContext());

        this.ignoreUpdate = WebContainer.cPool.getBGUpdateCheck();

        this.eventHolder = new EventHolder(getApplicationContext());

        try {

            deviceUUIDAsHash = Base64.encodeToString(
                    deviceUUID.getBytes("UTF-8"), Base64.DEFAULT);


            Event event = new Event();

            event.setId(deviceUUIDAsHash);

            event.setName("platformUsed");

            event.setStartTime(new Date().getTime());

            event.setEndTime(new Date().getTime());

            Map<String, String> parameters = new HashMap<String, String>();

            parameters.put("platform", "Android");

            event.setParameters(parameters);

            List<Event> list = this.eventHolder.getCachedEvents();

            list.add(event);

            this.eventHolder.addEventsToCache(list);

        } catch (UnsupportedEncodingException e) {

            Log.e(TAG, e.getMessage());
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null && extra.getBoolean("update", false))
            super.appView.clearCache(true);

        super.loadUrl("file://" + getFilesDir().getAbsolutePath() + "/"
                + "index.html");

        Log.d("WebContainer", "javascript: { var currentLivoUsername = '" + WebContainer.cPool.getUserId() + "'; var currentLivoPass = '" + WebContainer.cPool.getUserSecret() + "'; }");
        super.loadUrl("javascript: { var currentLivoUsername = '" + WebContainer.cPool.getUserId() + "'; var currentLivoPass = '" + WebContainer.cPool.getUserSecret() + "'; }");

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, LivoGCMRegistrationService.class);
            intent.setAction(LivoGCMActions.NO_ACTION);
            startService(intent);
        }
    }


    @Override
    public void onStart() {

        super.onStart();

        this.eventHandler = new EventHandler();

        this.eventHandler.start();

    }

    @Override
    public void onStop() {

        this.eventHandler.interrupt();

        super.onStop();
    }



    private class EventHandler extends Thread{

        private ConfigPool configPool;
        private Event event;
        private Handler mHandler = new Handler(Looper.getMainLooper());

        EventHandler (){

            this.configPool = ConfigPool.getInstance(getApplicationContext());

            this.event = new Event(UUID.randomUUID().toString(), "userActive");
        }

        @Override
        public void run(){

            while(true){

                ThriftServicesPool thrift = new ThriftServicesPool(
                        this.configPool.getServerAddress(), this.configPool.getServerPort());
                try {
                    event.setStartTime(new Date().getTime());

                    Thread.sleep(60000);

                    event.setEndTime(new Date().getTime());

                    event.setParameters(new HashMap<String, String>());

                    List<Event> es = eventHolder.getCachedEvents();

                    es.add(event);

                    eventHolder.addEventsToCache(es);

                    Log.d(ATAG,"WebContainer es.size(): " + es.size() + " after sleep 60 sc.");
                    if (isOnline()) { //es.size() > 4 &&

                        Log.d(ATAG,"Opening transport for event syncronization.");

                        thrift.openTransport();

                        AnalyticsService.Client analyticsClient = thrift.getAnalyticsService();

                        if(!analyticsClient.isOptedIn(this.configPool.getToken()))

                            analyticsClient.optIn(this.configPool.getToken());

                        for (Event ev : new LinkedList<Event>(es)) {

                            analyticsClient.logEvent(this.configPool.getToken(), ev);

                            es.remove(ev);
                        }

                        eventHolder.addEventsToCache(es);

                        analyticsClient.optOut(this.configPool.getToken());

                        ProvisioningService.Client provisiningClient = thrift.getProvisionService();

                        ProfileContainer prof = new ProfileContainer(getApplicationContext());

                        Log.d(ATAG, "token ---> " + this.configPool.getToken().getUniqueValue());
                        Profile serverProfile = provisiningClient.checkProvision(this.configPool.getToken(),
                                this.configPool.getApplicationName(), prof.getClientProfile());

                        if (serverProfile.getHash() == null || serverProfile.getHash().isEmpty()) {

                            if (this.configPool.getAppExistence()) {

                                Log.d("CompLoginScreen", "No update needed, going on normally.");

                            } else {

                                Log.d("CompLoginScreen", "No such an application warning has been sent.");

                            }
                        } else {

                            prof.setServerProfile(serverProfile);//Save incoming server profile

                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    showDialog();

                                }
                            }, 1000);

                            Log.d("LoginScreen", "Authentication success with update/install process has been sent.");

                        }

                        thrift.closeTransport();

                    }

                } catch (InterruptedException e) {

                    Log.d(ATAG,"Inactive");

                    event.setEndTime(new Date().getTime());

                    event.setParameters(new HashMap<String, String>());

                    List<Event> es = eventHolder.getCachedEvents();

                    es.add(event);

                    eventHolder.addEventsToCache(es);

                    thrift.closeTransport();

                    return;
                } catch (TException e) {
                    Log.e(ATAG, "TException", e);
                    thrift.closeTransport();
                }
            }
        }
    }

    protected boolean isOnline() {
        NetworkInfo localNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();

        return (localNetworkInfo != null)
                && (localNetworkInfo.isConnectedOrConnecting());
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
