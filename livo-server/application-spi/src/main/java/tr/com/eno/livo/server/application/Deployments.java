package tr.com.eno.livo.server.application;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Deployments implements Serializable, Comparable<Deployments> {

    public static final String DEFAULT_DOMAIN = "default";
    private static final long serialVersionUID = -1438948634969823088L;
    private final String applicationName;
    private final String applicationDomain;
    private final int userId;
    private String userName;
    private final int deployId;
    private final long createDate;
    private final String timeUUID;
    private String formatedDate;
    private final Application currentApp;
    private final boolean isActive;

    @ConstructorProperties({"applicationName", "applicationDomain", "userId", "deployId", "createDate", "timeUUID", "currentApp", "isActive"})
    public Deployments(String applicationName, String applicationDomain, int userId, int deployId, long createDate, String timeUUID, Application currentApp, boolean isActive) {

        this.applicationName = applicationName;
        this.applicationDomain = applicationDomain;
        this.userId = userId;
        this.deployId = deployId;
        this.createDate = createDate;
        this.timeUUID = timeUUID;
        this.currentApp = currentApp;
        this.isActive = isActive;
        
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getApplicationDomain() {
        return applicationDomain;
    }

    public int getDeployId() {
        return deployId;
    }

    public long getCreateDate() {
        return createDate;
    }

    public int getUserId() {
        return userId;
    }

    public String getTimeUUID() {
        return timeUUID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFormatedDate() {
        return formatedDate;
    }

    public void setFormatedDate(String formattedDate) {
        this.formatedDate = formattedDate;
    }

    public Application getCurrentApp() {
        return currentApp;
    }

    public boolean isIsActive() {
        return isActive;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            throw new NullPointerException();
        }

        if (obj instanceof Application) {

            Deployments otherApplication = (Deployments) obj;

            return Objects.equals(this.applicationDomain, otherApplication.applicationDomain) && Objects.equals(this.applicationName, otherApplication.applicationName);

        } else {

            return false;
        }
    }

    @Override
    public int hashCode() {

        return Objects.hash(this.applicationDomain, this.applicationName);
    }
    
    @Override
    public int compareTo(Deployments o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.applicationDomain == null) {
            throw new NullPointerException();
        }

        if (this.applicationName == null) {
            throw new NullPointerException();
        }

        int domainComparison = this.applicationDomain.compareTo(o.applicationDomain);
        int nameComparison = this.applicationName.compareTo(o.applicationName);
        
        return domainComparison == 0 ? nameComparison : domainComparison;
    }

}
