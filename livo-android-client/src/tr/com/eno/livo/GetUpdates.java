package tr.com.eno.livo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.thrift.TException;

import tr.com.eno.livo.thrift.file.FileTransferService;
import tr.com.eno.livo.thrift.file.FileTransferSession;
import tr.com.eno.livo.thrift.shared.Profile;

public class GetUpdates
        extends Activity implements Runnable{

    private Handler handler = new Handler();

    protected ProgressBar progressBar;

    protected ImageView imageView;

    protected TextView infoText;

    protected ProfileContainer container;

    public void onCreate(Bundle paramBundle) {

        super.onCreate(paramBundle);

        setContentView(R.layout.get_updates);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        imageView = (ImageView)findViewById(R.id.imageView);

        imageView.setBackground(new BitmapDrawable(getResources(), BlurBuilder.blur(
                getApplicationContext(), BitmapFactory.decodeResource(getResources(), R.drawable.splash))));

        infoText =(TextView)findViewById(R.id.infoText);

        GetFiles localGetFiles = new GetFiles();

        Profile[] arrayOfProfile = new Profile[2];

        container = new ProfileContainer(getApplicationContext());

        arrayOfProfile[0] = container.getServerProfile();

        arrayOfProfile[1] = container.getClientProfile();

        localGetFiles.execute(arrayOfProfile);


    }

    @Override
    public void run() {

        Intent webContainer = new Intent(getApplicationContext(), WebContainer.class);

        webContainer.putExtra("update", true);

        GetUpdates.this.startActivity(webContainer);

        GetUpdates.this.overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);

        GetUpdates.this.finish();

    }

    private class GetFiles
            extends AsyncTask<Profile, String, String> {

        private static final String TAG = "t.c.e.livo.GetUpdates";

        private Profile clientProfile;

        private Profile serverProfile;

        protected void onPreExecute() {
            super.onPreExecute();
        }


        private List<Set<tr.com.eno.livo.thrift.shared.File>> getDeltaDelete(Profile serverProfile, Profile clientProfile) {
            List<Set<tr.com.eno.livo.thrift.shared.File>> localLinkedList = new LinkedList<Set<tr.com.eno.livo.thrift.shared.File>>();
            Set<tr.com.eno.livo.thrift.shared.File> filesToDelete = new HashSet<tr.com.eno.livo.thrift.shared.File>();
            Set<tr.com.eno.livo.thrift.shared.File> filesToUpdate = new HashSet<tr.com.eno.livo.thrift.shared.File>();
            if (!serverProfile.equals(clientProfile)) {

                for (tr.com.eno.livo.thrift.shared.File file : serverProfile.getFiles()) {

                    if (!clientProfile.getFiles().contains(file))
                        filesToUpdate.add(file);
                }

                for (tr.com.eno.livo.thrift.shared.File file : clientProfile.getFiles()) {

                    if (!serverProfile.getFiles().contains(file))
                        filesToDelete.add(file);
                }
            }

            localLinkedList.add(filesToDelete);

            localLinkedList.add(filesToUpdate);

            return localLinkedList;
        }


        protected String doInBackground(Profile... args) {
            clientProfile = args[1];

            serverProfile = args[0];

            
            List<Set<tr.com.eno.livo.thrift.shared.File>> list =
                    getDeltaDelete(serverProfile, clientProfile);

            //The files that will be deleted
            Set<tr.com.eno.livo.thrift.shared.File> deleteSet = list.get(0);

            // The files that will be updated or added
            Set<tr.com.eno.livo.thrift.shared.File> updateSet = list.get(1);


            // ConfigPool initialization
            ConfigPool configPool = ConfigPool.getInstance(getApplicationContext());

            //ThriftPool initialization
            ThriftServicesPool servicePool = new ThriftServicesPool(configPool.getServerAddress(), configPool.getServerPort());

            //Open transport for getting files
            try {

                servicePool.openTransport();

                FileTransferService.Client fileTransferClient = servicePool.getFileTransferService();

                long updateBucketCount = 0;

                for (tr.com.eno.livo.thrift.shared.File file : updateSet) {

                    //TODO large server load will cause problems because of this approach.
                    FileTransferSession session = fileTransferClient.initiateSession(file, 40960);

                    updateBucketCount += session.getBucketCount();

                    fileTransferClient.destroySession(session);

                }

                long currentBucketIndex = 0;


                for (tr.com.eno.livo.thrift.shared.File file : updateSet) {


        		    Log.d("Getting File: ", getFilesDir() + "///" + file.getPath());
                    Log.d("File Size:",file.getSize()+"");

                    //creating file session
                    FileTransferSession session = fileTransferClient.initiateSession(file, 40960);

                    //creating file
                    java.io.File clientFile = new java.io.File(getFilesDir() + "/" + file.getPath());

                    if (!clientFile.getParentFile().exists()) {
                        clientFile.getParentFile().mkdirs();
                        clientFile.createNewFile();
                    }

                    FileOutputStream fileOutputStream = new FileOutputStream(clientFile, false);

                    FileChannel stream = fileOutputStream.getChannel();

                    while (session.getBucketCount() > session.getCurrentIndex()) {

                        ByteBuffer buffer = fileTransferClient.fetchBucket(session);

                        stream.write(buffer);

                        buffer.clear();

                        currentBucketIndex++;

                        publishProgress((int)(long)(currentBucketIndex*100/updateBucketCount)+"", file.getPath());

                        session.setCurrentIndex(session.getCurrentIndex() + 1);

                    }


                    stream.close();

                    fileOutputStream.flush();

                    fileOutputStream.close();

                    fileTransferClient.destroySession(session);
                }

                if(updateSet.size()==0)
                    publishProgress(100+"","UPDATED");

                publishProgress(-1+"");

            }  catch (TException e) {

                Log.d(TAG, e.getMessage());
                return null;
            } catch (IOException e) {

                Log.d(TAG, e.getMessage());
                return null;
            }


            return "success";

        }

        protected void onPostExecute(String paramString) {
            super.onPostExecute(paramString);

            if (paramString != null) {
                   // trimCache();

                GetUpdates.this.handler.postDelayed(GetUpdates.this,1000);

            } else {

                Intent returnIntent = new Intent(getApplicationContext(), Livo.class);

                returnIntent.putExtra("UpdateError", true);

                startActivity(returnIntent);
            }
        }

        protected void onProgressUpdate(String... paramVarArgs) {

            if (Integer.parseInt(paramVarArgs[0]) == -1) {

                container.equalizeProfiles(this.serverProfile);

                GetUpdates.this.progressBar.setProgress(100);

                GetUpdates.this.imageView.setAlpha(1f);

                GetUpdates.this.infoText.setText(100 + "%" + "  UPDATED");

                return;
            }

            int progress = Integer.parseInt(paramVarArgs[0]);

            GetUpdates.this.progressBar.setProgress(progress);

            GetUpdates.this.imageView.setAlpha((float) progress / 100);

            GetUpdates.this.infoText.setText(progress+"%"+"  "+paramVarArgs[1]);


        }

        public void trimCache() {
            Log.d("GetUpdates", "Removing cache.");
            try {
                java.io.File dir = new File(getFilesDir().getParentFile(), "app_webview");

                Log.e("file path:", dir.getAbsolutePath());
                if (dir != null && dir.isDirectory()) {
                    deleteDir(dir);
                }
                Log.d("GetUpdates", "Cache has been removed.");
            } catch (Exception e) {
                Log.e("GetUpdates", e.getMessage());
            }
        }

        public boolean deleteDir(java.io.File dir) {
            if (dir != null && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    boolean success = deleteDir(new java.io.File(dir, children[i]));
                    if (!success) {
                        return false;
                    } else
                        Log.e("Deleted File:", new java.io.File(dir, children[i]).getAbsolutePath());
                }
            }

            // The directory is now empty so delete it
            try {
                FileUtils.forceDelete(dir);
                return true;
            } catch (IOException e) {
                Log.e("GetUpdates", "Error", e);
                return false;
            }
        }
    }
}

