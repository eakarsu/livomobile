package tr.com.eno.livo;

import tr.com.eno.livo.thrift.shared.AuthenticationToken;
import android.content.Context;
import android.content.SharedPreferences;


public class ConfigPool {

    //Local fields
    private Context context;
    private SharedPreferences.Editor editAEON;
    private SharedPreferences.Editor editAuth;


    private ConfigPool(Context paramContext) {

        this.context = paramContext;

        this.editAEON = this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).edit();

        this.editAuth = this.context.getSharedPreferences("CurrentAuthenticationToken", Context.MODE_PRIVATE).edit();
    }

    /**
     * This object provides methods to save , bring back to use application preferences.
     *
     * @param paramContext Context of the application
     * @return instance of the {@link ConfigPool}
     */

    public static ConfigPool getInstance(Context paramContext) {

        return new ConfigPool(paramContext);
    }

    public String getGCMToken(){

        return this.context.getSharedPreferences("AEON",Context.MODE_PRIVATE).getString("nspToken",null);
    }

    public void setGCMToken(String arg){

        this.editAEON.putString("npsToken",arg.trim());
        this.editAEON.commit();

    }
    public String getServerAddress() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getString("serverAddress", null);
    }

    public int getServerPort() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getInt("port", 2366);
    }

    public void setBGUpdateCheck(boolean arg){

        this.editAEON.putBoolean("backGUC",arg);

        this.editAEON.commit();
    }

    public boolean getBGUpdateCheck(){

       return this.context.getSharedPreferences("AEON",Context.MODE_PRIVATE).getBoolean("backGUC",false);
    }

    public AuthenticationToken getToken() {

        SharedPreferences localSharedPreferences = this.context.getSharedPreferences("CurrentAuthenticationToken",
                Context.MODE_PRIVATE);

        AuthenticationToken token =
                new AuthenticationToken(localSharedPreferences.getString("uniqueValue", null),
                        localSharedPreferences.getString("companyId", ""),
                        localSharedPreferences.getLong("authenticationTime", 0));

        token.setExpirationTime(localSharedPreferences.getLong("expirationTime", 0));

        token.setUserPrincipal(localSharedPreferences.getString("userPrincipal", ""));

        return token;
    }

    public void putServerConfig(String paramString, int paramInt) {

        this.editAEON.putString("serverAddress", paramString.trim());

        if (paramInt < 80) {

            this.editAEON.putInt("port", 2366);
        }

        this.editAEON.putInt("port", paramInt);

        this.editAEON.commit();
    }

    public void putCompanyId(String companyId) {

        this.editAEON.putString("companyId", companyId.trim());

        this.editAEON.commit();
    }

    public String getCompanyId() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE)
                .getString("companyId", "");
    }

    public String getCompanySecret() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getString("companySecret", "");
    }

    public void putCompanySecret(String arg) {

        this.editAEON.putString("companySecret", arg.trim());

        this.editAEON.commit();
    }


    public void setRememberCompany(boolean arg) {


        this.editAEON.putBoolean("rememberCompanyAuthc", arg);

        this.editAEON.commit();
    }


    public boolean getRememberCompany() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getBoolean("rememberCompanyAuthc", true);
    }


    public void putUserId(String userId) {

        this.editAEON.putString("userId", userId.trim());

        this.editAEON.commit();
    }

    public String getUserId() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE)
                .getString("userId", "");
    }

    public String getUserSecret() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getString("userSecret", "");
    }

    public void putUserSecret(String arg) {

        this.editAEON.putString("userSecret", arg.trim());

        this.editAEON.commit();
    }

    public void setOfflineUsage(boolean arg){

        this.editAEON.putBoolean("offline", arg);

        this.editAEON.commit();

    }

    public boolean getOfflineUsage(){

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getBoolean("offline", false);
    }

    public void setRememberUser(boolean arg) {

        this.editAEON.putBoolean("rememberUserAuthc", arg);

        this.editAEON.commit();
    }


    public boolean getRememberUser() {


        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getBoolean("rememberUserAuthc", true);
    }

    public void putToken(AuthenticationToken token) {

        this.editAuth.putLong("expirationTime", token.getExpirationTime());

        this.editAuth.putString("uniqueValue", token.getUniqueValue());

        this.editAuth.putLong("authenticationTime", token.getAuthenticationTime());

        this.editAuth.putString("companyId", token.getCompanyId());

        this.editAuth.putString("userPrincipal", token.getUserPrincipal());

        this.editAuth.commit();
    }

    public void setAppExistence(boolean existence) {

        this.editAEON.putBoolean("appExists", existence);

        this.editAEON.commit();
    }

    public boolean getAppExistence() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getBoolean("appExists", false);
    }

    public void setApplicationName(String applicationName) {

        this.editAEON.putString("ApplicationName", applicationName.trim());

        this.editAEON.commit();
    }

    public String getApplicationName() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getString("ApplicationName", "Default");
    }

    public boolean getAnonymousDataPermission() {

        return this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getBoolean("anonymous", true);
    }

    public synchronized void setAnonymousDataPermission(boolean perm) {

        this.editAEON.putBoolean("anonymous", perm);

        this.editAEON.commit();
    }

//    public synchronized void setOptInStatus(boolean status) {
//
//        this.editAEON.putBoolean("optInStatus", status);
//
//        this.editAEON.commit();
//    }
//
//    public boolean getOptInStatus() {
//
//        boolean status = this.context.getSharedPreferences("AEON", Context.MODE_PRIVATE).getBoolean("optInStatus", true);
//
//        return status;
//    }

}
