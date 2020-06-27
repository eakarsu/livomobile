package tr.com.eno.livo;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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

public class UserAuthcFragment extends Fragment {


    private Button button;

    private EditText userId;

    private EditText userSecret;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.user_loginscreen, container, false);

        button = (Button) layout.findViewById(R.id.user_login);

        userId = ((EditText) layout.findViewById(R.id.user_login_name));

        userSecret = ((EditText) layout.findViewById(R.id.user_login_secret));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                button.setEnabled(false);

                new UserAndDeviceLogin().execute(UserAuthcFragment.this.userId.getText().toString(),
                        UserAuthcFragment.this.userSecret.getText().toString());

            }
        });
        // Inflate the layout for this fragment
        return layout;
    }

    @Override
    public void onStart() {
        super.onStart();

        // During startup, check if there are arguments passed to the fragment.
        // onStart is a good place to do this because the layout has already been
        // applied to the fragment at this point so we can safely call the method
        // below that sets the article text.
//        Bundle args = getArguments();
//        if (args != null) {
//            // Set article based on argument passed in
//            updateArticleView(args.getInt(ARG_POSITION));
//        } else if (mCurrentPosition != -1) {
//            // Set article based on saved instance state defined during onCreateView
//            updateArticleView(mCurrentPosition);
//        }
    }
//
//    public void updateArticleView(int position) {
//        TextView article = (TextView) getActivity().findViewById(R.id.article);
//        article.setText(Ipsum.Articles[position]);
//        mCurrentPosition = position;
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }


    //Login process handler for company authentication.
    private class UserAndDeviceLogin extends AsyncTask<String, Integer, String> {

        private ShimmerFrameLayout container = ((Livo) getActivity()).getLayoutContainer();

        private  String userId;

        private  String userSecret;

        private ConfigPool cPool;


        // Start animation inside of given container;
        private void startAnimation() {

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
            UserAuthcFragment.this.button.setEnabled(true);

        }


        @Override
        protected void onPreExecute() {


            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            imm.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

            this.startAnimation();

        }

        @Override
        protected String doInBackground(String... param) {

            this.userId = param[0];

            this.userSecret = param[1];

            String TAG = "User&DeviceAuthc";

            ThriftServicesPool tPool = null;

            if (param[0].isEmpty() || param[1].isEmpty()) {

                //Publish null credential process
                publishProgress(ClientConstants.NULL_CREDENTIALS);


            } else if (!isOnline() || param[0].equals("livoany") || param[0].equals("livocomp")) {

                Log.d(TAG, "No internet connection warning has been sent.");
                //Publish no internet connection process
                publishProgress(ClientConstants.NO_INTERNET_CONNECTION);


            } else {

                 cPool = ConfigPool.getInstance(getActivity().getApplicationContext());

                if (cPool.getServerAddress() == null) {

                    Log.d(TAG, "No server adress warning has been sent.");
                    //Publish missing configuration process
                    publishProgress(ClientConstants.NO_SERVER_ADRESS);


                } else {

                    try {

                        tPool = new ThriftServicesPool(
                                cPool.getServerAddress(), cPool.getServerPort());

                        tPool.openTransport(); //Open transport to the server

                        AuthenticationService.Client authClient = tPool
                                .getAuthService();//Get AuthenticationService Client

                        ProvisioningService.Client proviClient = tPool
                                .getProvisionService();// Get ProvisioningService Client

                        if(cPool.getToken().getExpirationTime()<System.currentTimeMillis()){

                            CompanyAuthenticationResult result = authClient.authenticateCompany(cPool.getCompanyId(),cPool.getCompanySecret(), cPool.getApplicationName());

                            if(result.isSuccess())
                                cPool.putToken(result.getAuthenticationToken());
                            else{
                                publishProgress(ClientConstants.FAILED_ON_COMPANYAUTHC);
                                cPool.setRememberCompany(false);
                                cPool.putCompanyId("");
                                cPool.setRememberUser(false);
                                cPool.putUserId("");
                                return null;
                            }
                        }

                        UserAuthenticationResult userAuthcResult = authClient.authenticateUser(cPool.getToken(), param[0], param[1], cPool.getApplicationName());


                        if (userAuthcResult.isSuccess()) {

                            String deviceUUIDHashed = Base64.encodeToString(Settings.Secure.getString(getActivity().getApplicationContext()
                                    .getContentResolver(), Settings.Secure.ANDROID_ID).getBytes("UTF-8"), Base64.DEFAULT);

                            DeviceAuthenticationResult deviceAuthcResult = authClient.authenticateDevice(userAuthcResult.getAuthenticationToken(), deviceUUIDHashed, cPool.getApplicationName());

                            if (deviceAuthcResult.isSuccess()) {

                                cPool.putToken(deviceAuthcResult.getAuthenticationToken());

                                ProfileContainer prof = new ProfileContainer(getActivity().getApplicationContext());

                                Profile serverProfile = proviClient.checkProvision(deviceAuthcResult.getAuthenticationToken(),
                                        cPool.getApplicationName(), prof.getClientProfile());

                                if (serverProfile.getHash().isEmpty()) {

                                    if (cPool.getAppExistence()) {

                                        Log.d(TAG, "No update needed, going on normally.");

                                        tPool.closeTransport();

                                        publishProgress(ClientConstants.NO_UPDATE_NEEDED);
                                    } else {

                                        Log.d(TAG, "No such an application warning has been sent.");

                                        tPool.closeTransport();

                                        publishProgress(ClientConstants.NO_SUCH_AN_APPLICATION);

                                    }
                                } else {

                                    prof.setServerProfile(serverProfile);//Save incoming server profile

                                    tPool.closeTransport();

                                    Log.d(TAG, "Authentication success with update/install process has been sent.");

                                    publishProgress(ClientConstants.AUTH_SUCCESS);

                                }


                            } else {

                                tPool.closeTransport();

                                Log.e(TAG, "Device has no permission!");

                                publishProgress(ClientConstants.DEVICE_PERMISSON);
                            }

                            return "success";
                        } else {

                            tPool.closeTransport();

                            Log.d(TAG, "Authentication fail warning has been sent.");

                            publishProgress(ClientConstants.AUTH_FAIL);

                        }

                    } catch (TException e) {

                        Log.e(TAG, "ThriftException error has been sent.");

                        publishProgress(ClientConstants.THRIFT_EXCEPTION);

                        return null;
                    } catch (UnsupportedEncodingException e) {


                        Log.e(TAG, "Device authc fail sent.");

                        tPool.closeTransport();

                        publishProgress(ClientConstants.DEVICE_ID_PROBLEM);

                        return null;
                    }

                }

            }

            return null;
        }

        protected void onProgressUpdate(Integer... args) {


            Toast warningToast;

            switch (args[0].intValue()) {


                case ClientConstants.NO_SERVER_ADRESS:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "No server adress provided!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.NULL_CREDENTIALS:

                    Log.d("switch", "Null credentials.");

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "ID/Password can not be empty!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.THRIFT_EXCEPTION:

                    Log.d("LoginScreen", "ThriftException.");

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Server is unreachable atm.", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();


                    break;

                case ClientConstants.AUTH_FAIL:

                    Log.d("switch", "AuthFail");

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Wrong ID/Password pair!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;
                case ClientConstants.FAILED_ON_COMPANYAUTHC:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Validate company authentication.", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    getActivity().recreate();

                    break;

                case ClientConstants.NO_INTERNET_CONNECTION:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "No internet connection.", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;


                case ClientConstants.DEVICE_ID_PROBLEM:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Device information cannot be resolved!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.DEVICE_PERMISSON:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Number of devices exceeded!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.NO_SUCH_AN_APPLICATION:

                    warningToast = Toast.makeText(getActivity().getApplicationContext(), "Application is not deployed!", Toast.LENGTH_SHORT);

                    warningToast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);

                    warningToast.show();

                    break;

                case ClientConstants.NO_UPDATE_NEEDED:

                    this.cPool.putUserId(this.userId);
                    this.cPool.putUserSecret(this.userSecret);

                    startActivity(new Intent(getActivity().getApplicationContext(), WebContainer.class));
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    getActivity().finish();

                    break;


                default:

                    this.cPool.putUserId(this.userId);
                    this.cPool.putUserSecret(this.userSecret);

                    startActivity(new Intent(getActivity().getApplicationContext(), GetUpdates.class));
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                    getActivity().finish();


                    break;
            }

            this.stopAnimation();
        }
    }
}
