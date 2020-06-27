package tr.com.eno.livo;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;


import com.facebook.shimmer.ShimmerFrameLayout;

import org.apache.thrift.TException;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import tr.com.eno.livo.thrift.analytics.AnalyticsService;
import tr.com.eno.livo.thrift.analytics.Event;
import tr.com.eno.livo.thrift.authc.AuthenticationService;
import tr.com.eno.livo.thrift.authc.SettingsAuthenticationResult;

public class Livo extends FragmentActivity {

    final Context context = this;
    ShimmerFrameLayout container;
    FragmentManager fragmentManager = null;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        this.recreate();

    }

    protected final ShimmerFrameLayout getLayoutContainer() {

        Log.d("onCreate", "getLayoutContainer --------> " + (this.container == null));
        return this.container;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        if (item.getItemId() == R.id.settings)
            startActivityForResult(new Intent(this, ServerConfig.class), 1);

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            container.setBackgroundResource(0);

            container.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.splash))));

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            container.setBackgroundResource(0);

            container.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.splash))));
        }

    }

    private SettEventHandler settEventHandler;
    SettingsAuthenticationResult authSettRes = null;

    public void onCreate(Bundle paramBundle) {

        super.onCreate(paramBundle);
        fragmentManager = getSupportFragmentManager();

        Bundle extra = getIntent().getExtras();

        ConfigPool pool = ConfigPool.getInstance(getApplicationContext());

        pool.setRememberCompany(false);
        pool.setRememberUser(false);

        if (extra != null) {
            if (extra.getBoolean("UpdateError")) {
                AlertDialog.Builder updateError = new AlertDialog.Builder(this);
                updateError.setCancelable(false);
                updateError
                        .setTitle("Interrupted by connection loss!")
                        .setMessage("You have disconnected from internet during download.")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {

                                        dialog.dismiss();

                                    }
                                });

                updateError.create().show();

            }
        }

        // Layout
        setContentView(R.layout.screen);
        //FullScreen Mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.settEventHandler = new SettEventHandler();

        this.settEventHandler.start();

        this.container =
                (ShimmerFrameLayout) findViewById(R.id.fragment_container);

        this.container.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(
                getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.splash))));

        Log.e("Info", "RememberCompanyAuthc: " + pool.getRememberCompany() + " RememberUserAuthc: "
                + pool.getRememberUser() + " - CompanyAuthcInfo: " + pool.getCompanyId() + "-" + pool.getCompanySecret()
                + " UserAutchInfo: " + pool.getUserId() + "-" + pool.getUserSecret() + " -- Container --------> " + (this.container == null));

    }

    private class SettEventHandler extends Thread{

        private ConfigPool configPool;

        SettEventHandler (){

            this.configPool = ConfigPool.getInstance(getApplicationContext());

        }

        public void run(){

            Log.d("getAuthSettings", "server: " + configPool.getServerAddress() + " -- port: " + configPool.getServerPort() + " -- appName: " + configPool.getApplicationName());

            do{

                try {

                    Thread.sleep(2000);

                } catch (InterruptedException e){
                    Log.d("SettEventHandler","Inactive");
                    return;
                }

                if(configPool.getServerAddress() != null &&  this.configPool.getApplicationName() != null && !this.configPool.getApplicationName().equals("Default")) {

                    Log.d("getAuthSettings2", "server: " + configPool.getServerAddress() + " -- port: " + configPool.getServerPort() + " -- appName: " + configPool.getApplicationName());
                    ThriftServicesPool tPool = new ThriftServicesPool(this.configPool.getServerAddress(), this.configPool.getServerPort());

                    try {

                        if (isOnline()) {

                            Log.d("SettEventHandler", "Opening transport for event syncronization.");

                            tPool.openTransport(); //Open transport to the server  

                            AuthenticationService.Client authClient = tPool.getAuthService();//Get AuthenticationService Client  

                            authSettRes = authClient.authenticateSetting(this.configPool.getApplicationName());

                            Log.d("AuthSettings --> ", "appName: " + authSettRes.getAppName() + " -- key: " + authSettRes.getSettingKey() + " -- val: " + authSettRes.getSettingVal());

                            String authKey = authSettRes.getSettingKey();
                            if(authKey != null){

                                String process = authKey.split("\\.")[1];
                                if(process.equals("any")){

                                    this.configPool.putCompanyId("LivoAny");
                                    this.configPool.putCompanySecret("LivoAny");
                                    this.configPool.putUserId("livoany");
                                    this.configPool.putUserSecret("LivoAnY");
                                    this.configPool.setRememberCompany(true);
                                    this.configPool.setRememberUser(true);

                                }
                                else
                                if(process.equals("company")){

                                    this.configPool.putUserId("livocomp");
                                    this.configPool.putUserSecret("LivoComP");
                                    this.configPool.setRememberUser(true);

                                }
                                else
                                if(process.equals("System") || process.equals("ldap")){

                                    this.configPool.putCompanyId("LivoUsers");
                                    this.configPool.putCompanySecret("LivoUsers");
                                    this.configPool.setRememberCompany(true);

                                }

                            }

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    TextView startTextView = (TextView) getLayoutContainer().findViewById(R.id.setServerAppId);
                                    ((ViewGroup) startTextView.getParent()).removeView(startTextView);
                                }

                            });

                            if (this.configPool.getRememberCompany() && this.configPool.getRememberUser() && !this.configPool.getCompanyId().isEmpty() && !this.configPool.getUserId().isEmpty()) {

                                //getActionBar().hide();

                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, new SignInFragment()).commit();

                            } else if (this.configPool.getRememberCompany() && !this.configPool.getCompanyId().isEmpty()) {

                                //getActionBar().show();

                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, new UserAuthcFragment()).commit();

                            } else {

                                getActionBar().show();

                                fragmentManager.beginTransaction()
                                        .replace(R.id.fragment_container, new CompanyAuthcFragment()).commit();

                            }

                            tPool.closeTransport();

                        }

                    } catch (TException e) {
                        Log.e("SettEventHandler", "TException", e);
                        e.printStackTrace();
                        tPool.closeTransport();
                    }
                }

            } while(this.configPool.getServerAddress() == null);

        }

        protected boolean isOnline() {

            NetworkInfo localNetworkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();

            return (localNetworkInfo != null) && localNetworkInfo.isConnectedOrConnecting();

        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

}

