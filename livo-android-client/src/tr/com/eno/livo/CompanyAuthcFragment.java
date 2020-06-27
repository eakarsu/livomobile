package tr.com.eno.livo;

import android.animation.ObjectAnimator;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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


public class CompanyAuthcFragment extends Fragment {

    private Button button;

    private EditText companyId;

    private EditText companySecret;

    protected void inflateNextFragment() {

        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(null).
                setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container, new UserAuthcFragment()).commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.company_loginscreen, container, false);

        this.button = (Button) layout.findViewById(R.id.company_login);

        this.companyId = ((EditText) layout.findViewById(R.id.company_login_name));

        this.companySecret = ((EditText) layout.findViewById(R.id.company_login_secret));

        this.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CompanyAuthcFragment.this.button.setEnabled(false);

                new CompanyLogin().execute(companyId.getText().toString(),
                        companySecret.getText().toString());

            }
        });
        // Inflate the layout for this fragment
        return layout;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }


    //Login process handler for company authentication.
    private class CompanyLogin extends AsyncTask<String, Integer, String> {

        private ShimmerFrameLayout container = ((Livo) getActivity()).getLayoutContainer();

        private String companyId;

        private String companySecret;

        private ConfigPool cPool;


        // Start animation inside of given container;
        private void starAnimation() {

            this.container.setBaseAlpha(0);
            this.container.setDuration(4000);
            this.container.setDropoff(0.2f);
            this.container.setIntensity(0.2f);
            this.container.setMaskShape(ShimmerFrameLayout.MaskShape.RADIAL);
            this.container.setRepeatMode(ObjectAnimator.REVERSE);
            this.container.startShimmerAnimation();

        }

        private void stopAnimation() {

            this.container.stopShimmerAnimation();


        }


        // checking whether Internet connection exists
        public boolean isOnline() {
            NetworkInfo localNetworkInfo = ((ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE))
                    .getActiveNetworkInfo();
            return (localNetworkInfo != null)
                    && (localNetworkInfo.isConnectedOrConnecting());
        }

        protected void onPostExecute(String paramString) {

            super.onPostExecute(paramString);

            CompanyAuthcFragment.this.button.setEnabled(true);

        }


        @Override
        protected void onPreExecute() {


            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            this.starAnimation();

        }

        @Override
        protected String doInBackground(String... param) {

            this.companyId = param[0];

            this.companySecret = param[1];

            Log.e("ThreadGeyiği",this.companyId+"---"+this.companySecret);

            if (param[0].isEmpty() || param[1].isEmpty()) {

                //Publish null credential process
                publishProgress(ClientConstants.NULL_CREDENTIALS);


            } else if (!isOnline() || param[0].equals("LivoAny") || param[0].equals("LivoUsers")) {

                Log.d("CompLoginScreen", "No internet connection warning has been sent.");
                //Publish no internet connection process
                publishProgress(ClientConstants.NO_INTERNET_CONNECTION);


            } else {

                 this.cPool = ConfigPool.getInstance(getActivity().getApplicationContext());

                if (cPool.getServerAddress() == null) {

                    Log.d("CompLoginScreen", "No server adress warning has been sent.");
                    //Publish missing configuration process
                    publishProgress(ClientConstants.NO_SERVER_ADRESS);


                } else {

                    try {

                        ThriftServicesPool tPool = new ThriftServicesPool(
                                cPool.getServerAddress(), cPool.getServerPort());
                        Log.d("CompLoginScreen", cPool.getServerAddress() + " --------- " + cPool.getServerPort());

                        tPool.openTransport(); //Open transport to the server

                        AuthenticationService.Client authClient = tPool
                                .getAuthService();//Get AuthenticationService Client

//                        ProvisioningService.Client proviClient = tPool
//                                .getProvisionService();// Get ProvisioningService Client

                        CompanyAuthenticationResult authRes = authClient
                                .authenticateCompany(param[0], param[1], cPool.getApplicationName());//Authenticate with given credentials.


                        if (authRes.isSuccess()) {

                            cPool.putToken(authRes.getAuthenticationToken()); //Save incoming token

                            Log.d("CompLoginScreen", cPool.getUserId().length() + " <---------> " + cPool.getUserSecret());
                            if(cPool.getUserId().length() != 0 && cPool.getUserSecret().length() != 0){

                                UserAuthenticationResult userResult = authClient.authenticateUser(authRes.getAuthenticationToken(), cPool.getUserId(), cPool.getUserSecret(), cPool.getApplicationName());

                                String deviceUUIDHashed = Base64.encodeToString(Settings.Secure.getString(getActivity().getApplicationContext()
                                        .getContentResolver(), Settings.Secure.ANDROID_ID).getBytes("UTF-8"), Base64.DEFAULT);

                                DeviceAuthenticationResult deviceResult = authClient.authenticateDevice(userResult.getAuthenticationToken(), deviceUUIDHashed, cPool.getApplicationName());

                                ProvisioningService.Client proviClient = tPool
                                        .getProvisionService();// Get ProvisioningService Client

                                if (userResult.isSuccess() && deviceResult.isSuccess()) {

                                    cPool.putToken(deviceResult.getAuthenticationToken()); //Save incoming token

                                    ProfileContainer prof = new ProfileContainer(getActivity().getApplicationContext());

                                    Profile serverProfile = proviClient.checkProvision(authRes.getAuthenticationToken(),
                                            cPool.getApplicationName(), prof.getClientProfile());


                                    if (serverProfile.getHash() == null || serverProfile.getHash().isEmpty()) {

                                        if (cPool.getAppExistence()) {

                                            Log.d("CompLoginScreen", "No update needed, going on normally.");

                                        } else {

                                            Log.d("CompLoginScreen", "No such an application warning has been sent.");

                                            tPool.closeTransport();

                                            publishProgress(ClientConstants.NO_SUCH_AN_APPLICATION);

                                            return null;

                                        }
                                    } else {

                                        prof.setServerProfile(serverProfile);//Save incoming server profile

                                        Log.d("LoginScreen", "Authentication success with update/install process has been sent.");

                                    }

                                }
                                else {

                                    tPool.closeTransport();

                                    if (!userResult.isSuccess()) {

                                        publishProgress(ClientConstants.FAILED_ON_USERAUTHC);

                                        return null;
                                    } else {

                                        publishProgress(ClientConstants.FAILED_ON_DEVICEAUTHC);

                                        return null;
                                    }
                                }

                            }

//                            ProfileContainer prof = new ProfileContainer(getActivity().getApplicationContext());
//
//                            Profile serverProfile = proviClient.checkProvision(authRes.getAuthenticationToken(),
//                                    cPool.getApplicationName(), prof.getClientProfile());

//                            Log.d("LoginScreen", "Application name: " + cPool.getApplicationName());
//                            Log.d("LoginScreen", "ServerProfile: " + serverProfile.toString());

//                            if (serverProfile.getHash() == null || serverProfile.getHash().isEmpty()) {
//
//                                if (cPool.getAppExistence()) {
//
//                                    Log.d("LoginScreen", "No update needed, going on normally.");
//
//                                    tPool.closeTransport();
//
//                                    publishProgress(ClientConstants.NO_UPDATE_NEEDED);
//                                } else {
//
//                                    Log.d("LoginScreen", "No such an application warning has been sent.");
//
//                                    tPool.closeTransport();
//
//                                    publishProgress(ClientConstants.NO_SUCH_AN_APPLICATION);
//
//                                }
//                          } else {
//
//                                prof.setServerProfile(proviClient.checkProvision(authRes.getAuthenticationToken(),
//                                        cPool.getApplicationName(), prof.getClientProfile()));//Save incoming server profile
//
//
//                                tPool.closeTransport();
//
//                                Log.d("LoginScreen", "Authentication success with update/install process has been sent.");
//
//                                publishProgress(ClientConstants.AUTH_SUCCESS);
//
//                            }

                            publishProgress(ClientConstants.AUTH_SUCCESS);

                            tPool.closeTransport();

                            return "success";
                        } else {

                            tPool.closeTransport();

                            Log.d("CompLoginScreen", "Authentication fail warning has been sent.");
                            publishProgress(ClientConstants.AUTH_FAIL);

                        }

                    } catch (TException e) {

                        Log.d("CompLoginScreen", "ThriftException error has been sent.");

                        publishProgress(ClientConstants.THRIFT_EXCEPTION);

                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

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

                case ClientConstants.NO_SERVER_ADRESS:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(),"No server adress provided!",Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.NULL_CREDENTIALS:

                    Log.d("switch", "Null credentials.");

                    warningToast = Toast.makeText(getActivity().getApplicationContext(),"ID/Password can not be empty!",Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.THRIFT_EXCEPTION:

                    Log.d("LoginScreen", "ThriftException.");

                    warningToast = Toast.makeText(getActivity().getApplicationContext(),"Server is unreachable at the moment.",Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();


                    break;

                case ClientConstants.AUTH_FAIL:

                    Log.d("switch", "AuthFail");

                    warningToast = Toast.makeText(getActivity().getApplicationContext(),"Wrong ID/Password pair!",Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.NO_INTERNET_CONNECTION:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(),"No internet connection.",Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;


                default:

                    if(cPool.getUserId().length() == 0 && cPool.getUserSecret().length() == 0){

                        Log.d("switch", "AuthSuccess");

                        CompanyAuthcFragment.this.inflateNextFragment();

                        this.cPool.putCompanyId(this.companyId);

                        this.cPool.putCompanySecret(this.companySecret);

                    }
                    else{

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                startActivity(new Intent(getActivity().getApplicationContext(), GetUpdates.class));
                                getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                                getActivity().finish();
                            }
                        }, 1000);

                    }

                    break;
            }

            this.stopAnimation();
        }
    }
}
