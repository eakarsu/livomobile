package tr.com.eno.livo.server.application;

import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import javax.management.MXBean;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;

@MXBean
public interface ApplicationService {

    public static final String APPLICATION_DELETED_EVENT_TOPIC = "tr/com/eno/livo/server/application/deleted";
    public static final String APPLICATION_DEPLOYED_EVENT_TOPIC = "tr/com/eno/livo/server/application/deployed";
    public static final String APPLICATION_SAVED_EVENT_TOPIC = "tr/com/eno/livo/server/application/saved";
    public static final String APPLICATION_DELETE_DEPLOYMENT_EVENT_TOPIC = "tr/com/livo/server/application/deldeployment";
    public static final String APPLICATION_ACTIVATE_DEPLOYMENT_EVENT_TOPIC = "tr/com/livo/server/application/activate_deployment";

    public void createApplication(Application application) throws ApplicationAlreadyExistsException, InvalidApplicationException;

    /**
     * Deletes selected application.
     *
     * @param domain
     * @param applicationName Name of the application
     */
    public void deleteApplication(String domain, String applicationName) throws ApplicationNotFoundException;
    
    /**
     * Deletes selected deployment.
     *
     * @param domain
     * @param applicationName name and id of the application
     * @param timeuuid the id of the time when the application is created 
     */
    public void deleteDeployment(Integer deployId, String domain, String applicationName, String timeUUID) throws DeploymentNotFoundException;
    
    /**
     * Activate selected deployment.
     *
     * @param domain
     * @param applicationName name and id of the application
     * @param timeuuid the id of the time when the application is created 
     */
    public void activateDeployment(Integer deployId, String domain, String applicationName, String timeUUID) throws DeploymentNotFoundException;

    /**
     * Saves and deploys application to client devices.
     *
     * @param application Application {@link Application}
     */
    public Integer deployApplication(Application application, int userId) throws ApplicationNotFoundException, ApplicationDeploymentFailedException;

    /**
     * Returns the application's previous versions as application objects sorted
     * ascending (newest first), with an optional limit that limits the number
     * of results ascending (newest top 'limit' application objects). Note that
     * setting limit to -1 means there is no limit.
     *
     * @param domain
     * @param name Name of application
     * @param limit History limit
     * @return history of the application as an application list
     */
    public List<Deployments> getAppHistory(String domain, String name, int limit) throws ApplicationNotFoundException;
    
    /**
     * Returns the all deployment versions as application objects sorted
     * ascending (newest first)
     *
     * @param domain
     * @return all deployment versions as an application list
     */
    public List<Deployments> getAllDeploymentHistory(String domain);

    /**
     * Lists applications of the company.
     *
     * @param domain
     * @return Set of applications
     */
    public SortedSet<Application> listApplications(String domain);

    /**
     * Loads the application.
     *
     * @param domain
     * @param name Name of the application
     * @return Application
     * @throws tr.com.eno.livo.server.application.ApplicationNotFoundException
     */
    public Application loadApplication(String domain, String name) throws ApplicationNotFoundException;

    /**
     * Saves the application but DOES NOT deploys it to the client devices.
     *
     * @param application {@link Application}
     */
    public void saveApplication(Application application) throws ApplicationNotFoundException;
    
    public void authorizeUsers(String domain, String name, User... users) throws ApplicationNotFoundException;
    
    public void deauthorizeUsers(String domain, String name, User... users) throws ApplicationNotFoundException;
    
    public void authorizeGroups(String domain, String name, UserGroup... groups) throws ApplicationNotFoundException;
    
    public void deauthorizeGroups(String domain, String name, UserGroup... groups) throws ApplicationNotFoundException;
    
    public SortedSet<Companies> listAppCompanies(String appName) throws ApplicationNotFoundException;
    
    public void authorizeCompanies(String domain, String name, Companies... companies) throws ApplicationNotFoundException;

    public void updateOwner(Application currentApp, Integer userId) throws ApplicationNotFoundException;
    
    public void setAuthorizationPolicy(String domain, String name, AuthorizationPolicy policy) throws ApplicationNotFoundException;
    
    public List<IconsSplashes> listIconsSplashes4App(String appName) throws ApplicationNotFoundException;
    
    public void saveIconsSplashes4App(String appName, String imgId, String platform, String fileName, Integer userId) throws ApplicationNotFoundException;
    
}
