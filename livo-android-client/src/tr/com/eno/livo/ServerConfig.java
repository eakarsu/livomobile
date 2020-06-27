package tr.com.eno.livo;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

public class ServerConfig
        extends Activity {

    // private EditText editPort;
    private EditText editServer;
    private EditText editAppName;
    private ConfigPool pool;
    private Button saveButton;
    private Button saveAppButton;
    private TextView textView; //Server name
    private TextView textView2;//Application Name
    private Switch anonymousData;
    private Switch companyAuthc;
    private Switch userAuthc;
    private Switch backgroundCheck;
    private LinearLayout container;
    private String TAG="Server&ClientConfig";
    private Switch offLineUsage;


    public void onCreate(Bundle paramBundle) {

        super.onCreate(paramBundle);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        //Sets content view
        setContentView(R.layout.serverconfig);

        container = (LinearLayout) findViewById(R.id.configurations);

        container.setBackground(new BitmapDrawable(getResources(),BlurBuilder.blur(getApplicationContext(),BitmapFactory.decodeResource(getResources(),R.drawable.splash))));

        //ConfigPool initialization.
        this.pool = ConfigPool.getInstance(getApplicationContext());

        //Gets server address from pool.
        String str = this.pool.getServerAddress();

        //Server address holder TextView.
        this.textView = ((TextView) findViewById(R.id.address_holder));

        this.anonymousData = (Switch) findViewById(R.id.anonymous);

        this.anonymousData.setChecked(this.pool.getAnonymousDataPermission());

        this.anonymousData.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                pool.setAnonymousDataPermission(isChecked);

            }
        });

        this.userAuthc = (Switch)findViewById(R.id.remember_user);

        this.companyAuthc =(Switch)findViewById(R.id.remember_company);

        this.userAuthc.setChecked(this.pool.getRememberUser());

        this.companyAuthc.setChecked(this.pool.getRememberCompany());

        this.companyAuthc.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                pool.setRememberCompany(isChecked);

                if (!isChecked) {

                    userAuthc.setChecked(isChecked);

                    pool.putCompanyId("");


                }

            }
        });

        this.userAuthc.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                pool.setRememberUser(isChecked);

                if (isChecked)
                    companyAuthc.setChecked(isChecked);
                else{
                    pool.putUserId("");

                    offLineUsage.setChecked(isChecked);
                }
            }
        });

        this.backgroundCheck = (Switch)findViewById(R.id.checkupdate);

        this.backgroundCheck.setChecked(this.pool.getBGUpdateCheck());

        this.backgroundCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pool.setBGUpdateCheck(isChecked);
            }
        });


        this.offLineUsage = (Switch)findViewById(R.id.offlineUsage);

        this.offLineUsage.setChecked(this.pool.getOfflineUsage());

        this.offLineUsage.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                pool.setOfflineUsage(isChecked);

                if(isChecked)
                    userAuthc.setChecked(isChecked);
            }
        });

        //Show,check value.
        if (str != null) {

            this.textView.setText("Current Adress: " + str);
        } else {

            this.textView.setText("Current Address: Not Saved Yet");
        }

        //Save server configuration button.
        this.saveButton = ((Button) findViewById(R.id.saveconfig));

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View paramAnonymousView) {

                onSaveConfig();
            }
        });

        //Server values.
        this.editServer = ((EditText) findViewById(R.id.serverAddress));

        // this.editPort = ((EditText)findViewById(R.id.serverPort));

        //Container application name holder TextView.
        this.textView2 = (TextView) findViewById(R.id.containerApp);

        this.textView2.setText("Current Application: " + this.pool.getApplicationName());

        //Application name value.
        this.editAppName = (EditText) findViewById(R.id.appName);

        //Save application name button.
        this.saveAppButton = (Button) findViewById(R.id.saveApplicationName);

        this.saveAppButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                onSaveAppName();
            }
        });

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            this.finish();

        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        Intent returnIntent = new Intent();

        setResult(RESULT_OK, returnIntent);

        this.finish();

        return true;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if(newConfig.orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            container.setBackgroundResource(0);
            container.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.splash))));
        }
        else if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
        {

            container.setBackgroundResource(0);
            container.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.splash))));
        }

    }

    public final void onSaveConfig() {

        if (this.editServer.getText().toString().isEmpty()) {

            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);

            localBuilder.setMessage(" <Server Adress> must be specified before saving!").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                }
            });

            localBuilder.create().show();

        } else {

            String str = this.editServer.getText().toString();

//    if ((!this.editPort.getText().toString().isEmpty()))
//    {
//      int i = Integer.parseInt(this.editPort.getText().toString());
//      this.pool.putServerConfig(str, i);
//    }
//    else
            // {
            this.pool.putServerConfig(str, 2366);
            // }

            this.textView.setText("Current Adress: " + str);

            this.editServer.setText("");

        }
    }

    public final void onSaveAppName() {

        if (this.editAppName.getText().toString().isEmpty()) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(this);

            localBuilder.setMessage("Application name must be specified before saving!.").setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    paramAnonymousDialogInterface.cancel();
                }
            });

            localBuilder.create().show();

        } else {
            String s = this.editAppName.getText().toString();

            this.pool.setApplicationName(s);

            this.textView2.setText("Current Application: " + s);

            this.editAppName.setText("");
        }

    }

}

