package tr.com.eno.livo.server.application.cassandra;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.UUID;
import java.util.regex.Pattern;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.application.Application;
import tr.com.eno.livo.server.application.ApplicationAlreadyExistsException;
import tr.com.eno.livo.server.application.ApplicationNotFoundException;
import tr.com.eno.livo.server.application.ApplicationService;
import tr.com.eno.livo.server.application.AuthorizationPolicy;
import tr.com.eno.livo.server.application.InvalidApplicationException;
import tr.com.eno.livo.server.application.ApplicationDeploymentFailedException;
import tr.com.eno.livo.server.application.Companies;
import tr.com.eno.livo.server.application.DeploymentNotFoundException;
import tr.com.eno.livo.server.application.Deployments;
import tr.com.eno.livo.server.application.IconsSplashes;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;

public class CassandraApplicationService implements ApplicationService {

    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final Pattern APPLICATION_ID_PATTERN = Pattern.compile("[\\w&&\\D][-\\w]{5,}?");
    private static final byte DEFAULT_DEVICE_LIMIT = 10;
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraApplicationService.class);
    private EventAdmin eventAdmin;

    protected void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based ApplicationService implementation...");

        String host = config.containsKey(CASSANDRA_HOST_CONFIGURATION_KEY) && config.get(CASSANDRA_HOST_CONFIGURATION_KEY) != null ? (String) config
                .get(CASSANDRA_HOST_CONFIGURATION_KEY) : "localhost";

        LOGGER.debug("Using Cassandra host '{}'...", host);

        int port = config.containsKey(CASSANDRA_PORT_CONFIGURATION_KEY) && config.get(CASSANDRA_PORT_CONFIGURATION_KEY) != null ? (Integer) config
                .get(CASSANDRA_PORT_CONFIGURATION_KEY) : 9042;

        LOGGER.debug("Using Cassandra port {}...", port);

        CassandraHelper.connect(host, port);

        LOGGER.debug("Registering shutdown hook to disconnect from Cassandra...");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {

                CassandraHelper.disconnect();
            }
        }));

        LOGGER.info("Successfully started Cassandra-based ApplicationService implementation.");
    }

    protected void stop() {

        LOGGER.info("Stopping Cassandra-based ApplicationService implementation...");

        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based ApplicationService implementation.");
    }
    
    protected void registerService(EventAdmin eventAdmin) {
        
        LOGGER.debug("Registering EventAdmin instance...");
        
        this.eventAdmin = eventAdmin;
    }
    
    protected void unregisterService() {
        
        LOGGER.debug("Unregistering EventAdmin instance...");
        
        this.eventAdmin = null;
    }

    @Override
    public void createApplication(Application application) throws ApplicationAlreadyExistsException, InvalidApplicationException {

        if (application == null) {

            LOGGER.error("Passed application object to be used for creation is null.");

            throw new NullPointerException();
        }

        if (application.getName() == null) {

            LOGGER.error("Passed application's name is null.");

            throw new NullPointerException();
        }

        if (application.getDomain() == null) {

            LOGGER.error("Passed application's domain is null.");

            throw new NullPointerException();
        }

        if (application.getName().trim().isEmpty()) {

            LOGGER.error("Passed application's name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (application.getDomain().trim().isEmpty()) {

            LOGGER.error("Passed application's domain is an empty string.");

            throw new IllegalArgumentException();
        }

        validateApplication(application);

        LOGGER.debug("Creating application '{}' in domain '{}'...", application.getName(), application.getDomain());

        CassandraHelper.insertApplication(application);
    }

    @Override
    public void deleteApplication(String domain, String applicationName) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Deleting application '{}' from domain '{}'...", applicationName, domain);

        CassandraHelper.deleteApplication(domain, applicationName);
    }
    
    @Override
    public void deleteDeployment(Integer deployId, String domain, String applicationName, String timeUUID) throws DeploymentNotFoundException {
        
        LOGGER.debug("Deleting1 deployment for application '{}' from domain '{}' at '{}'...", applicationName, domain, timeUUID);

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        CassandraHelper.findDeployment(domain, applicationName, UUID.fromString(timeUUID));

        LOGGER.debug("Deleting deployment for application '{}' from domain '{}' at '{}'...", applicationName, domain, timeUUID);

        CassandraHelper.deleteDeployment(domain, applicationName, timeUUID);
    }
    
    @Override
    public void activateDeployment(Integer deployId, String domain, String applicationName, String timeUUID) throws DeploymentNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        CassandraHelper.findDeployment(domain, applicationName, UUID.fromString(timeUUID));

        LOGGER.debug("Updating deployment for application '{}' from domain time id '{}' at '{}'...", applicationName, domain, timeUUID);

        CassandraHelper.activateDeployment(domain, applicationName, UUID.fromString(timeUUID));
    }

    @Override
    public Integer deployApplication(Application application, int userId) throws ApplicationNotFoundException, ApplicationDeploymentFailedException {

        if (application == null) {

            LOGGER.error("Passed application object to be used for deployment is null.");

            throw new NullPointerException();
        }

        if (application.getName() == null) {

            LOGGER.error("Passed application's name is null.");

            throw new NullPointerException();
        }

        if (application.getDomain() == null) {

            LOGGER.error("Passed application's domain is null.");

            throw new NullPointerException();
        }

        if (application.getName().trim().isEmpty()) {

            LOGGER.error("Passed application's name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (application.getDomain().trim().isEmpty()) {

            LOGGER.error("Passed application's domain is an empty string.");

            throw new IllegalArgumentException();
        }

        LOGGER.debug("Deploying application '{}' of domain '{}'...", application.getName(), application.getDomain());

        return CassandraHelper.deployApplication(application, userId);
        
    }

    @Override
    public List<Deployments> getAppHistory(String domain, String applicationName, int limit) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Loading last {} deployments for the application '{}' of domain '{}'...", limit, applicationName, domain);

        List<Deployments> appDeployments = CassandraHelper.listAppDeployments(domain, applicationName, limit);

        LOGGER.debug("Found {} application deployments.", appDeployments.size());

        return appDeployments;
    }

    @Override
    public SortedSet<Application> listApplications(String domain) {

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        LOGGER.debug("Listing applications of domain '{}'...", domain);

        SortedSet<Application> applications = CassandraHelper.listApplications(domain);

        LOGGER.debug("Found {} applications.", applications.size());

        return applications;
    }

    @Override
    public Application loadApplication(String domain, String applicationName) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        LOGGER.debug("Loading application '{}' of domain '{}'...", applicationName, domain);

        return CassandraHelper.findApplication(domain, applicationName);
    }

    @Override
    public void saveApplication(Application application) throws ApplicationNotFoundException {

        if (application == null) {

            LOGGER.error("Passed application object to be used for deployment is null.");

            throw new NullPointerException();
        }

        if (application.getName() == null) {

            LOGGER.error("Passed application's name is null.");

            throw new NullPointerException();
        }

        if (application.getDomain() == null) {

            LOGGER.error("Passed application's domain is null.");

            throw new NullPointerException();
        }

        if (application.getName().trim().isEmpty()) {

            LOGGER.error("Passed application's name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (application.getDomain().trim().isEmpty()) {

            LOGGER.error("Passed application's domain is an empty string.");

            throw new IllegalArgumentException();
        }

        Application currentApplication = CassandraHelper.findApplication(application.getDomain(), application.getName());

        LOGGER.debug("Saving application '{}' of domain '{}'...", application.getName(), application.getDomain());

        CassandraHelper.updateApplication(new Application(currentApplication.getName(), currentApplication.getDomain(), currentApplication.getCreateUser(), currentApplication.getCreateDate(), currentApplication.getOwnerId(), currentApplication.getAuthorizationPolicy(), application.isFreeform(), application.getAssets(), application.getScreens(), application.getTheme(), currentApplication.getAuthorizedUsers(), currentApplication.getAuthorizedGroups()));
    }

    @Override
    public void authorizeUsers(String domain, String applicationName, User... users) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        if (users == null || users.length == 0) {

            LOGGER.debug("No users were passed to authorize access to application '{}' of domain '{}'.", applicationName, domain);

            return;
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Authorizing users' access to application '{}' of domain '{}'...", applicationName, domain);

        CassandraHelper.authorizeUsers(domain, applicationName, users);
    }

    @Override
    public void deauthorizeUsers(String domain, String applicationName, User... users) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        if (users == null || users.length == 0) {

            LOGGER.debug("No users were passed to deauthorize access to application '{}' of domain '{}'.", applicationName, domain);

            return;
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Deauthorizing users' access to application '{}' of domain '{}'...", applicationName, domain);

        CassandraHelper.deauthorizeUsers(domain, applicationName, users);
    }

    @Override
    public void authorizeGroups(String domain, String applicationName, UserGroup... groups) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        if (groups == null || groups.length == 0) {

            LOGGER.debug("No user groups were passed to authorize access to application '{}' of domain '{}'.", applicationName, domain);

            return;
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Authorizing users groups' access to application '{}' of domain '{}'...", applicationName, domain);

        CassandraHelper.authorizeGroups(domain, applicationName, groups);
    }

    @Override
    public void deauthorizeGroups(String domain, String applicationName, UserGroup... groups) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        if (groups == null || groups.length == 0) {

            LOGGER.debug("No user groups were passed to deauthorize access to application '{}' of domain '{}'.", applicationName, domain);

            return;
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Deathorizing users groups' access to application '{}' of domain '{}'...", applicationName, domain);

        CassandraHelper.deauthorizeGroups(domain, applicationName, groups);
    }

    @Override
    public void setAuthorizationPolicy(String domain, String applicationName, AuthorizationPolicy policy) throws ApplicationNotFoundException {

        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        if (policy == null) {

            LOGGER.error("Passed authorization policy is null.");

            throw new IllegalArgumentException("Passed authorization policy is null.");
        }

        Application currentApplication = CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Setting authorization policy of application '{}' belonging to domain '{}' to '{}'...", applicationName, domain, policy.toString());

        CassandraHelper.updateApplication(new Application(currentApplication.getName(), currentApplication.getDomain(), currentApplication.getCreateUser(), currentApplication.getCreateDate(), currentApplication.getOwnerId(), policy, currentApplication.isFreeform(), currentApplication.getAssets(), currentApplication.getScreens(), currentApplication.getTheme(), currentApplication.getAuthorizedUsers(), currentApplication.getAuthorizedGroups()));
    }

    private void validateApplication(Application application) throws InvalidApplicationException {

        if (!APPLICATION_ID_PATTERN.matcher(application.getName()).matches()) {

            LOGGER.error("Application name '{}' is invalid.", application.getName());

            throw new InvalidApplicationException("Application name is invalid.");
        }
    }

    /**
     * @return the eventAdmin
     */
    public EventAdmin getEventAdmin() {
        return eventAdmin;
    }

    @Override
    public void authorizeCompanies(String domain, String applicationName, Companies... companies) throws ApplicationNotFoundException {
        
        if (applicationName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (applicationName.trim().isEmpty()) {

            LOGGER.error("Passed application name is an empty string.");

            throw new IllegalArgumentException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        if (companies == null || companies.length == 0) {

            LOGGER.debug("No app companies were passed to authorize access to application '{}' of domain '{}'.", applicationName, domain);

            return;
        }

        CassandraHelper.findApplication(domain, applicationName);

        LOGGER.debug("Authorizing companies access to application '{}' of domain '{}'...", applicationName, domain);

        CassandraHelper.authorizedCompanies(applicationName, companies);
        
    }

    @Override
    public void updateOwner(Application currentApp, Integer userId) throws ApplicationNotFoundException {
        
        if (currentApp == null) {

            LOGGER.error("Passed application is null.");

            throw new NullPointerException();
        }

        if (userId == null) {

            LOGGER.debug("Failed updating an owner to NULL for application '{}'.", currentApp.getName());

            return;
        }

        LOGGER.debug("Updating owner to '{}' for application '{}'...", userId, currentApp.getName());
        
        CassandraHelper.updateOwner(currentApp, userId);
        
    }

    @Override
    public SortedSet<Companies> listAppCompanies(String appName){
        
        if (appName == null) {

            LOGGER.error("Passed application name is null.");

            throw new NullPointerException();
        }

        LOGGER.debug("List AppCompanies for application '{}'...", appName);

        return CassandraHelper.listAppCompanies(appName);
        
    }

    @Override
    public List<IconsSplashes> listIconsSplashes4App(String appName) throws ApplicationNotFoundException {
        
        if (appName == null) {

            LOGGER.error("Passed application's name is a null.");

            throw new IllegalArgumentException();
        }

        Application currentApplication = CassandraHelper.findApplication(appName);

        LOGGER.debug("List icons and splashes for app '{}'...", appName);
        
        return CassandraHelper.listIconsSplashes4App(appName);
        
    }

    @Override
    public void saveIconsSplashes4App(String appName, String imgId, String platform, String fileName, Integer userId) throws ApplicationNotFoundException {
        
        if (appName == null) {

            LOGGER.error("Passed application's name is a null.");

            throw new IllegalArgumentException();
        }

        Application currentApplication = CassandraHelper.findApplication(appName);

        LOGGER.debug("Saving icons and splashes for app '{}'...", appName);
        
        if(fileName != null && !fileName.isEmpty())
            CassandraHelper.saveIconsSplashes4App(appName, imgId, platform, fileName, userId);
        
    }

    @Override
    public List<Deployments> getAllDeploymentHistory(String domain) {
        
        if (domain == null) {

            LOGGER.error("Passed application domain is null.");

            throw new NullPointerException();
        }

        if (domain.trim().isEmpty()) {

            LOGGER.error("Passed application domain is an empty string.");

            throw new IllegalArgumentException();
        }

        LOGGER.debug("Loading all deployments for domain '{}'...", domain);

        List<Deployments> applicationDeployments = CassandraHelper.listAllDeployments(domain);

        LOGGER.debug("getAllDeploymentHistory::Found {} application deployments.", applicationDeployments.size());

        return applicationDeployments;
        
    }
    
}
