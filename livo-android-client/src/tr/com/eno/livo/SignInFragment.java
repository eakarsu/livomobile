package tr.com.eno.livo;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.apache.thrift.TException;

import java.io.UnsupportedEncodingException;

import tr.com.eno.livo.thrift.authc.AuthenticationService;
import tr.com.eno.livo.thrift.authc.CompanyAuthenticationResult;
import tr.com.eno.livo.thrift.authc.DeviceAuthenticationResult;
import tr.com.eno.livo.thrift.authc.UserAuthenticationResult;
import tr.com.eno.livo.thrift.provision.ProvisioningService;
import tr.com.eno.livo.thrift.shared.Profile;


public class SignInFragment extends Fragment {

    private ShimmerFrameLayout container;
    private ConfigPool cPool;
    private boolean keepOn = false;
    private Login login;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("onCreate", "--------> SignInFragment");

        this.keepOn = true;
        container = ((Livo) getActivity()).getLayoutContainer();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.sign_in_screen, container, false);
    }

    @Override
    public void onStop() {

        super.onStop();

        try{
        this.login.cancel(true);
        }catch(Exception ex){}

    }


    @Override
    public void onDestroy() {


        super.onDestroy();
    }


    @Override
    public void onStart() {

        Log.d("SıgnIn","Started");

        super.onStart();

        this.keepOn = true;

        this.login = new Login();

        this.login.execute(new String[]{"", ""});

    }

    private class Login extends AsyncTask<String, Integer, String> {

        // checking whether Internet connection exists
        public boolean isOnline() {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            return (localNetworkInfo != null)
                    && (localNetworkInfo.isConnectedOrConnecting());
        }

        protected void onPostExecute(String paramString) {
            super.onPostExecute(paramString);

            stopAnimation();

        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            startAnimation();

        }

        @Override
        protected String doInBackground(String... param) {
            cPool = ConfigPool.getInstance(getActivity().getApplicationContext());

            while (SignInFragment.this.keepOn) {

                if (!isOnline()) {

                    Log.d("LoginScreen", "No internet connection warning has been sent.");
                    //Publish no internet connection process
                    publishProgress(ClientConstants.NO_INTERNET_CONNECTION);

                    break;


                } else {


                    try {

                        ThriftServicesPool tPool = new ThriftServicesPool(
                                cPool.getServerAddress(), cPool.getServerPort());

                        tPool.openTransport(); //Open transport to the server

                        AuthenticationService.Client authClient = tPool
                                .getAuthService();//Get AuthenticationService Client

                        ProvisioningService.Client proviClient = tPool
                                .getProvisionService();// Get ProvisioningService Client

                        CompanyAuthenticationResult authRes = authClient
                                .authenticateCompany(cPool.getCompanyId(), cPool.getCompanySecret(), cPool.getApplicationName());//Authenticate with given credentials.

                        UserAuthenticationResult userResult = authClient.authenticateUser(authRes.getAuthenticationToken(), cPool.getUserId(), cPool.getUserSecret(), cPool.getApplicationName());

                        String deviceUUIDHashed = Base64.encodeToString(Settings.Secure.getString(getActivity().getApplicationContext()
                                .getContentResolver(), Settings.Secure.ANDROID_ID).getBytes("UTF-8"), Base64.DEFAULT);

                        DeviceAuthenticationResult deviceResult = authClient.authenticateDevice(userResult.getAuthenticationToken(), deviceUUIDHashed, cPool.getApplicationName());

                        if (authRes.isSuccess() && userResult.isSuccess() && deviceResult.isSuccess()) {

                            cPool.putToken(deviceResult.getAuthenticationToken()); //Save incoming token

                            ProfileContainer prof = new ProfileContainer(getActivity().getApplicationContext());

                            Profile serverProfile = proviClient.checkProvision(deviceResult.getAuthenticationToken(),
                                    cPool.getApplicationName(), prof.getClientProfile());
                            Log.d("LoginScreen", "token ---> " + deviceResult.getAuthenticationToken().getUniqueValue());

                            if (serverProfile.getHash() == null || serverProfile.getHash().isEmpty()) {

                                if (cPool.getAppExistence()) {

                                    Log.d("LoginScreen", "No update needed, going on normally.");

                                    tPool.closeTransport();

                                    publishProgress(ClientConstants.NO_UPDATE_NEEDED);

                                    break;

                                } else {

                                    Log.d("LoginScreen", "No such an application warning has been sent.");

                                    tPool.closeTransport();

                                    publishProgress(ClientConstants.NO_SUCH_AN_APPLICATION);

                                    break;

                                }

                            } else {

                                prof.setServerProfile(serverProfile);//Save incoming server profile

                                tPool.closeTransport();

                                Log.d("LoginScreen", "Authentication success with update/install process has been sent.");

                                publishProgress(ClientConstants.AUTH_SUCCESS);

                                break;

                            }


                        } else {

                            tPool.closeTransport();

                            if (!authRes.isSuccess()) {

                                publishProgress(ClientConstants.FAILED_ON_COMPANYAUTHC);

                                break;
                            } else if (!userResult.isSuccess()) {

                                publishProgress(ClientConstants.FAILED_ON_USERAUTHC);

                                break;
                            } else {

                                publishProgress(ClientConstants.FAILED_ON_DEVICEAUTHC);

                                break;
                            }
                        }


                    } catch (TException e) {

                        Log.d("LoginScreen", "ThriftException error has been sent.");

                        publishProgress(ClientConstants.THRIFT_EXCEPTION);

                    } catch (UnsupportedEncodingException e) {

                        publishProgress(ClientConstants.DEVICE_ID_PROBLEM);

                        break;
                    }

                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }

            }
            return null;
        }

        protected void onProgressUpdate(Integer... args) {


            Toast warningToast;

            switch (args[0].intValue()) {

                case ClientConstants.NO_SUCH_AN_APPLICATION:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Application has been removed!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;
                case ClientConstants.NO_UPDATE_NEEDED:



                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    startActivity(new Intent(getActivity().getApplicationContext(), WebContainer.class));
                                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                    getActivity().finish();
                                }
                            }, 1000);



                    break;


//                case ClientConstants.THRIFTPOOL_EXCEPTION:
//
//
//
//                        warningToast = Toast.makeText(getActivity().getApplicationContext(), "Server is not reachable at the moment. Try again later.", Toast.LENGTH_SHORT);
//
//                        warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
//
//                        warningToast.show();
//
//                        keepOn =false;
//
//                        stopAnimation();
//
//                       ((TextView)getActivity().findViewById(R.id.signInText)).setText("\u2639");
//
//
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            getActivity().finish();
//                        }
//                    },5000);
//                    break;


                case ClientConstants.NO_INTERNET_CONNECTION:

                    if(cPool.getOfflineUsage()){

                                startActivity(new Intent(getActivity().getApplicationContext(), WebContainer.class));
                                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                getActivity().finish();


                    }else {


                            startActivity(new Intent(getActivity().getApplicationContext(), ServerConfig.class));

                            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

                            warningToast = Toast.makeText(getActivity().getApplicationContext(), "You need to enable offline usage mode in order to use app without connection.", Toast.LENGTH_SHORT);

                            warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                            warningToast.show();
                        }


                    break;

                case ClientConstants.THRIFT_EXCEPTION:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Server is not reachable at the moment.", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();


                    break;

                case ClientConstants.DEVICE_ID_PROBLEM:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Can not resolve device info.", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.FAILED_ON_COMPANYAUTHC:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Company credentials has been changed by your administrator!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    cPool.putCompanyId("");

                    getActivity().recreate();

                    break;

                case ClientConstants.FAILED_ON_USERAUTHC:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "User credentials has been changed by your administrator!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    cPool.putUserId("");

                    getActivity().recreate();


                    break;
                case ClientConstants.FAILED_ON_DEVICEAUTHC:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Your device has no permission anymore!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    getActivity().recreate();

                    break;

                default:

                    Log.d("SignInFragment", "postDelayed 1000");
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getActivity().getApplicationContext(), GetUpdates.class));
                            getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            getActivity().finish();
                        }
                    }, 1000);



                    break;
            }
        }
    }

    private void startAnimation() {

        if(this.container != null) {
            this.container.setBaseAlpha(0);
            this.container.setDuration(4000);
            this.container.startShimmerAnimation();
        }

    }

    private void stopAnimation() {

        if(this.container != null)
            this.container.stopShimmerAnimation();


    }
}
