package tr.com.eno.livo.server.application.cassandra;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.logging.Level;
import org.nustaq.serialization.FSTConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.application.Application;
import tr.com.eno.livo.server.application.ApplicationNotFoundException;
import tr.com.eno.livo.server.application.Asset;
import tr.com.eno.livo.server.application.AuthorizationPolicy;
import tr.com.eno.livo.server.application.Companies;
import tr.com.eno.livo.server.application.DeploymentNotFoundException;
import tr.com.eno.livo.server.application.Deployments;
import tr.com.eno.livo.server.application.IconsSplashes;
import tr.com.eno.livo.server.application.Screen;
import tr.com.eno.livo.server.application.Theme;
import tr.com.eno.livo.server.file.File;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;

public class CassandraHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHelper.class);
    private static Cluster cluster;
    private static Session session;
    private static FSTConfiguration fstConfiguration;
    private static PreparedStatement selectApplicationStatement;
    private static PreparedStatement selectApplicationStatement2;
    private static PreparedStatement selectApplicationsStatement;
    private static PreparedStatement selectDeploymentsStatement;
    private static PreparedStatement selectAuthorizedUsersStatement;
    private static PreparedStatement selectAuthorizedGroupsStatement;
    private static PreparedStatement insertDeploymentStatement;
    private static PreparedStatement insertApplicationStatement;
    private static PreparedStatement deleteApplicationStatement;
    private static PreparedStatement deleteDeploymentsStatement;
    private static PreparedStatement deleteAuthorizedUsersStatement;
    private static PreparedStatement deleteAuthorizedGroupsStatement;
    private static PreparedStatement updateApplicationStatement;
    private static PreparedStatement insertAuthorizedUserStatement;
    private static PreparedStatement deleteAuthorizedUserStatement;
    private static PreparedStatement insertAuthorizedGroupStatement;
    private static PreparedStatement deleteAuthorizedGroupStatement;
    private static PreparedStatement selectAppCompaniesStatement;
    private static PreparedStatement selectCompanyAppsStatement;
    private static PreparedStatement insertCompanyStatement;
    private static PreparedStatement selectAppCompanyStatement;
    private static PreparedStatement deleteCompanyStatement;
    private static PreparedStatement updateApplicationOwnerStatement;
    private static PreparedStatement selectIconsSplashes4AppStatement;
    private static PreparedStatement insertIconsSplashes2AppStatement;
    private static PreparedStatement updIconsSplashes4AppStatement;
    private static PreparedStatement delIconsSplashes4AppStatement;
    private static PreparedStatement selectAppIconsSplashesStatement;
    private static PreparedStatement selectAllDeploymentsStatement;
    private static PreparedStatement selectAllAppDeploymentsStatement;
    private static PreparedStatement deleteSelectedDeploymentStatement;
    private static PreparedStatement selectAppDeploymentStatement;
    private static PreparedStatement activateDeploymentStatement;
    private static PreparedStatement deActivateDeploymentStatement;

    private CassandraHelper() {
    }

    static void connect(String host, int port) {

        // Check if we are already connected
        if (cluster != null && !cluster.isClosed()) {

            LOGGER.debug("Already connected to the Cassandra cluster, ignoring...");

            return;
        }

        LOGGER.debug("Connecting to the Cassandra cluster at '{}:{}'...", host, port);

        cluster = Cluster.builder().addContactPoint(host).withPort(port).withoutJMXReporting().build();

        LOGGER.debug("Initiating session...");

        session = cluster.connect();

        try {

            LOGGER.debug("Creating KEYSPACE IF NOT EXISTS application...");

            session.execute("CREATE KEYSPACE IF NOT EXISTS application WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 }");

            LOGGER.debug("Creating tables: application.applications, companies, deployments, authorized_users, authorized_user_groups, icons_splashes...");

            session.execute("CREATE TABLE IF NOT EXISTS application.applications (domain text, name text, create_user int, create_date bigint, ownerid int, authorization_policy int, freeform boolean, asset_paths set<text>, screen_ids set<text>, theme_id text, PRIMARY KEY (domain, name));");
            session.execute("CREATE TABLE IF NOT EXISTS application.companies(app_name text, company_id text, create_user int, create_date bigint,  PRIMARY KEY (app_name,company_id));");
            session.execute("CREATE TABLE IF NOT EXISTS application.deployments (application_domain text, application_name text, time timeuuid, create_user int, deploy_id int, data blob, is_active boolean, PRIMARY KEY (application_domain, application_name, time));");
            session.execute("CREATE TABLE IF NOT EXISTS application.authorized_users (application_domain text, application_name text, user_domain text, user_id text, PRIMARY KEY (application_domain, application_name, user_domain, user_id));");
            session.execute("CREATE TABLE IF NOT EXISTS application.authorized_user_groups (application_domain text, application_name text, group_domain text, group_name text, PRIMARY KEY (application_domain, application_name, group_domain, group_name));");
            session.execute("CREATE TABLE IF NOT EXISTS application.icons_splashes (imgid text, application_name text, platform text, filename text, create_user int, create_date bigint, PRIMARY KEY (application_name, imgid, platform));");

            LOGGER.debug("Preparing statements...");

            selectApplicationStatement = session.prepare("SELECT * FROM application.applications WHERE domain = ? AND name = ? LIMIT 1;");
            selectApplicationStatement2 = session.prepare("SELECT * FROM application.applications WHERE name = ? ALLOW FILTERING;");
            selectApplicationsStatement = session.prepare("SELECT * FROM application.applications WHERE domain = ?;");
            selectDeploymentsStatement = session.prepare("SELECT * FROM application.deployments WHERE application_domain = ? AND application_name = ? ORDER BY application_name DESC, time DESC LIMIT ?;");
            selectAppDeploymentStatement = session.prepare("SELECT * FROM application.deployments WHERE application_domain = ? AND application_name = ? AND time = ?;");
            selectAllAppDeploymentsStatement = session.prepare("SELECT * FROM application.deployments WHERE application_domain = ? AND application_name = ?;");
            selectAllDeploymentsStatement = session.prepare("SELECT * FROM application.deployments WHERE application_domain = ? ORDER BY application_name DESC, time DESC ALLOW FILTERING;");
            selectAuthorizedUsersStatement = session.prepare("SELECT * FROM application.authorized_users WHERE application_domain = ? AND application_name = ?;");
            selectAuthorizedGroupsStatement = session.prepare("SELECT * FROM application.authorized_user_groups WHERE application_domain = ? AND application_name = ?;");
            selectAppCompaniesStatement = session.prepare("SELECT * FROM application.companies WHERE app_name = ?;");
            selectAppCompanyStatement = session.prepare("SELECT * FROM application.companies WHERE app_name = ? and company_id = ?;");
            selectCompanyAppsStatement = session.prepare("SELECT * FROM application.companies WHERE company_id = ? ALLOW FILTERING;");
            insertApplicationStatement = session.prepare("INSERT INTO application.applications (domain, name, create_user, create_date, ownerid, authorization_policy, freeform, asset_paths, screen_ids, theme_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            insertCompanyStatement = session.prepare("INSERT INTO application.companies (app_name, company_id, create_user, create_date) VALUES (?, ?, ?, ?);");
            insertAuthorizedUserStatement = session.prepare("INSERT INTO application.authorized_users (application_domain, application_name, user_domain, user_id) VALUES (?, ?, ?, ?);");
            insertAuthorizedGroupStatement = session.prepare("INSERT INTO application.authorized_user_groups (application_domain, application_name, group_domain, group_name) VALUES (?, ?, ?, ?);");
            insertDeploymentStatement = session.prepare("INSERT INTO application.deployments (application_domain, application_name, time, create_user, deploy_id, data, is_active) VALUES (?, ?, now(), ?, ?, ?, true);");
            updateApplicationStatement = session.prepare("UPDATE application.applications SET authorization_policy = ?, freeform = ?, asset_paths = ?, screen_ids = ?, theme_id = ? WHERE domain = ? AND name = ?;");
            updateApplicationOwnerStatement = session.prepare("UPDATE application.applications SET ownerid = ? WHERE domain = ? AND name = ?;");
            deleteApplicationStatement = session.prepare("DELETE FROM application.applications WHERE domain = ? AND name = ?;");
            deleteDeploymentsStatement = session.prepare("DELETE FROM application.deployments WHERE application_domain = ? AND application_name = ?;");
            deleteSelectedDeploymentStatement = session.prepare("DELETE FROM application.deployments WHERE application_domain = ? AND application_name = ? AND time = ?;");
            activateDeploymentStatement = session.prepare("UPDATE application.deployments SET is_active = true WHERE application_domain = ? AND application_name = ? AND time = ?;");
            deActivateDeploymentStatement = session.prepare("UPDATE application.deployments SET is_active = false WHERE application_domain = ? AND application_name = ? AND time = ?;");            
            deleteAuthorizedUserStatement = session.prepare("DELETE FROM application.authorized_users WHERE application_domain = ? AND application_name = ? AND user_domain = ? AND user_id = ?;");
            deleteAuthorizedUsersStatement = session.prepare("DELETE FROM application.authorized_users WHERE application_domain = ? AND application_name = ?;");
            deleteAuthorizedGroupStatement = session.prepare("DELETE FROM application.authorized_user_groups WHERE application_domain = ? AND application_name = ? AND group_domain = ? AND group_name = ?;");
            deleteAuthorizedGroupsStatement = session.prepare("DELETE FROM application.authorized_user_groups WHERE application_domain = ? AND application_name = ?;");
            deleteCompanyStatement = session.prepare("DELETE FROM application.companies WHERE app_name = ? AND company_id = ?;");
            
            selectIconsSplashes4AppStatement = session.prepare("SELECT * FROM application.icons_splashes WHERE application_name = ?;");
            selectAppIconsSplashesStatement = session.prepare("SELECT * FROM application.icons_splashes WHERE application_name = ? and imgid = ?;");
            insertIconsSplashes2AppStatement = session.prepare("INSERT INTO application.icons_splashes (imgid, application_name, platform, filename, create_user, create_date) VALUES (?, ?, ?, ?, ?, ?);");
            updIconsSplashes4AppStatement = session.prepare("UPDATE application.icons_splashes SET filename = ? WHERE imgid = ? AND application_name = ? AND platform = ?;");
            delIconsSplashes4AppStatement = session.prepare("DELETE FROM application.icons_splashes WHERE application_name = ?;");
            
            LOGGER.debug("Initializing FST...");

            fstConfiguration = FSTConfiguration.createDefaultConfiguration();
            fstConfiguration.registerClass(Application.class, Asset.class, File.class, Screen.class, Theme.class);

        } catch (Exception e) {

            LOGGER.error(e.getMessage(), e);

            LOGGER.error("Failed to connect and setup the Cassandra server.");

            if (!cluster.isClosed()) {
                cluster.close();
            }

            cluster = null;
        }
    }

    static void disconnect() {

        LOGGER.debug("Closing session...");

        session.close();

        LOGGER.debug("Disconnecting from the Cassandra cluster...");

        cluster.close();
    }

    static void insertApplication(Application application) {

        String name = application.getName();
        String domain = application.getDomain();
        int createUser = application.getCreateUser();
        long createDate = application.getCreateDate();
        int ownerId = application.getOwnerId();
        boolean freeform = application.isFreeform();
        int authorizationPolicyCode = application.getAuthorizationPolicy() == null ? AuthorizationPolicy.ALLOW_ALL.getCode() : application.getAuthorizationPolicy().getCode();

        Set<String> assetPaths = new HashSet<>();

        if (application.getAssets() != null) {

            for (String assetName : application.getAssets().keySet()) {

                String assetPath = application.getAssets().get(assetName).getFile() == null ? assetName : application.getAssets().get(assetName).getFile().getPath();

                assetPaths.add(assetPath);
            }
        }
        System.out.println("CassandraHelper.insertApplication() -- > " + createUser + " -- " + createDate);
        BoundStatement statement = insertApplicationStatement.bind(domain, name, createUser, createDate, ownerId, authorizationPolicyCode, freeform, assetPaths, Collections.EMPTY_SET, null);

        session.execute(statement);
    }

    static Application findApplication(String domain, String applicationName) throws ApplicationNotFoundException {

        BoundStatement statement = selectApplicationStatement.bind(domain, applicationName);

        Row applicationRow = session.execute(statement).one();

        if (applicationRow == null) {

            LOGGER.debug("Failed to find application '{}' of domain '{}'.", applicationName, domain);

            throw new ApplicationNotFoundException();
        }

        return createApplication(applicationRow);
    }
    
    static Application findApplication(String applicationName) throws ApplicationNotFoundException {

        BoundStatement statement = selectApplicationStatement2.bind(applicationName);

        Row applicationRow = session.execute(statement).one();

        if (applicationRow == null) {

            LOGGER.debug("Failed to find application '{}'.", applicationName);

            throw new ApplicationNotFoundException();
        }

        return createApplication(applicationRow);
    }
    
    static void findDeployment(String domain, String applicationName, UUID timeUUID) throws DeploymentNotFoundException {

        BoundStatement statement = selectAppDeploymentStatement.bind(domain,applicationName,timeUUID);

        Row deployRow = session.execute(statement).one();

        if (deployRow == null) {

            LOGGER.debug("Failed to find deployment for '{}' at '{}'.", applicationName, timeUUID);

            throw new DeploymentNotFoundException();
        }

    }

    static void deleteApplication(String domain, String applicationName) {

        BoundStatement deleteApplicationBoundStatement = deleteApplicationStatement.bind(domain, applicationName);
        BoundStatement deleteDeploymentsBoundStatement = deleteDeploymentsStatement.bind(domain, applicationName);
        BoundStatement deleteAuthorizedUsersBoundStatement = deleteAuthorizedUsersStatement.bind(domain, applicationName);
        BoundStatement deleteAuthorizedGroupsBoundStatement = deleteAuthorizedGroupsStatement.bind(domain, applicationName);
        BoundStatement deleteIconsSplashes4AppStatement = delIconsSplashes4AppStatement.bind(applicationName);

        BatchStatement batchStatement = new BatchStatement();

        batchStatement.add(deleteApplicationBoundStatement);
        batchStatement.add(deleteDeploymentsBoundStatement);
        batchStatement.add(deleteAuthorizedUsersBoundStatement);
        batchStatement.add(deleteAuthorizedGroupsBoundStatement);
        batchStatement.add(deleteIconsSplashes4AppStatement);
        List<Row> companyList = session.execute(selectAppCompaniesStatement.bind(applicationName)).all();
        for (Row companyRow : companyList) { //delete all rest

            batchStatement.add(deleteCompanyStatement.bind(applicationName, companyRow.getString("company_id")));

        }

        session.execute(batchStatement);
    }
    
    static void deleteDeployment(String domain, String applicationName, String timeUUID) {

        BoundStatement deleteDeploymentsBoundStatement = deleteSelectedDeploymentStatement.bind(domain, applicationName, UUID.fromString(timeUUID));

        BatchStatement batchStatement = new BatchStatement();

        batchStatement.add(deleteDeploymentsBoundStatement);

        session.execute(batchStatement);
        
        LOGGER.debug("Deleted deployment from DB at '{}'.", timeUUID);
        
    }

    static Integer deployApplication(Application application, int userId) throws ApplicationNotFoundException {

//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
//
//            FSTObjectOutput out = fstConfiguration.getObjectOutput(baos);
//            out.writeObject(application, Application.class);
//            out.flush();
//
//            data = baos.toByteArray();
//
//        } catch (Exception exception) {
//
//            LOGGER.error("Failed to serialize application '{}' of domain '{}'.", name, domain);
//
//            LOGGER.error(exception.getMessage(), exception);
//
//            return;
//        }
        byte[] data = fstConfiguration.asByteArray(application);

        if (data == null) {

            LOGGER.error("Serialized application data is null.");

            return -1;
        }
        
        BoundStatement selectAll = selectAllDeploymentsStatement.bind(application.getDomain());
        ResultSet resultSet = session.execute(selectAll);
        int deployId = 0;
        if (!resultSet.isExhausted()){
            
            for (Row row : resultSet) {

                if (row.isNull("deploy_id")) continue;
                
                if(deployId < row.getInt("deploy_id"))
                    deployId = row.getInt("deploy_id");

            }
            
        }

        LOGGER.debug("Deployment for application '{}' with deployId {}.", application.getName(), deployId + 1);
        BoundStatement allAppDeployment = selectAllAppDeploymentsStatement.bind(application.getDomain(), application.getName());
        List<Row> deployRows = session.execute(allAppDeployment).all();

        BatchStatement batchStatement = new BatchStatement();
        for (Row deployRow : deployRows) { //deactivate all rest

            batchStatement.add(deActivateDeploymentStatement.bind(application.getDomain(), application.getName(), deployRow.getUUID("time")));

        }
        BoundStatement statement = insertDeploymentStatement.bind(application.getDomain(), application.getName(), userId, deployId + 1, ByteBuffer.wrap(data));
        batchStatement.add(statement);
        
        session.execute(batchStatement);
        
        return deployId + 1;
    }

    static List<Deployments> listAppDeployments(String domain, String applicationName, int limit) {

        LOGGER.info("passed {} for listing deployments for application '{}' of domain '{}'...", limit, applicationName, domain);
        if(limit < 0)
            limit = 1000;

        BoundStatement statement = selectDeploymentsStatement.bind(domain, applicationName, limit);

        ResultSet resultSet = session.execute(statement);

        if (resultSet.isExhausted()) {

            LOGGER.debug("Failed to find any deployment for application '{}' of domain '{}'.", applicationName, domain);

            return Collections.EMPTY_LIST;
        }

        List<Deployments> deploymentList = new LinkedList<>();

        for (Row row : resultSet) {

            if (row.isNull("data")) {

                LOGGER.debug("Data column of row is null.");

                continue;
            }

            byte[] data = row.getBytes("data").array();

            Application currentApp = (Application) fstConfiguration.asObject(data);
            
            Deployments deployment = new Deployments(row.getString("application_name"), row.getString("application_domain"), row.getInt("create_user"), row.getInt("deploy_id"), getTimeFromUUID(row.getUUID("time")), row.getUUID("time").toString(), currentApp, row.getBool("is_active"));

            deploymentList.add(deployment);
        }

        LOGGER.debug("Found {} deployments for application '{}' of domain '{}'.", deploymentList.size(), applicationName, domain);

        return Collections.unmodifiableList(deploymentList);
    }
    
    static List<Deployments> listAllDeployments(String domain) {


        BoundStatement statement = selectAllDeploymentsStatement.bind(domain);

        ResultSet resultSet = session.execute(statement);

        if (resultSet.isExhausted()) {

            LOGGER.debug("Failed to find any deployment for domain '{}'.", domain);

            return Collections.EMPTY_LIST;
        }

        List<Deployments> deploymentList = new LinkedList<>();

        for (Row row : resultSet) {

            if (row.isNull("data")) {

                LOGGER.debug("Data column of row is null.");

                continue;
            }
            LOGGER.debug("Deployment {}: '{}' - '{}'", row.getInt("deploy_id"), getTimeFromUUID(row.getUUID("time")), row.getUUID("time"));

            byte[] data = row.getBytes("data").array();

            Application currentApp = (Application) fstConfiguration.asObject(data);
            
            Deployments deployment = new Deployments(row.getString("application_name"), row.getString("application_domain"), row.getInt("create_user"), row.getInt("deploy_id"), getTimeFromUUID(row.getUUID("time")), row.getUUID("time").toString(), currentApp, row.getBool("is_active"));

            deploymentList.add(deployment);
        }

        LOGGER.debug("Found {} deployments for domain '{}'.", deploymentList.size(), domain);

        return deploymentList;
        
    }
    
    static final long NUM_100NS_INTERVALS_SINCE_UUID_EPOCH = 0x01b21dd213814000L;
    private static long getTimeFromUUID(UUID uuid) {
        return (uuid.timestamp() - NUM_100NS_INTERVALS_SINCE_UUID_EPOCH) / 10000;
    }

    static SortedSet<Application> listApplications(String domain) {

        BoundStatement statement = selectApplicationsStatement.bind(domain);

        ResultSet resultSet = session.execute(statement);

        SortedSet<Application> applications = new TreeSet<>();

        for (Row applicationRow : resultSet) {

            Application application = createApplication(applicationRow);

            if (application != null) {
                applications.add(application);
            }
        }

        LOGGER.debug("Found {} applications in domain '{}'.", applications.size(), domain);

        return Collections.unmodifiableSortedSet(applications);
    }

    static void updateApplication(Application application) {

        String name = application.getName();
        String domain = application.getDomain();
        boolean freeform = application.isFreeform();
        int authorizationPolicyCode = application.getAuthorizationPolicy() == null ? AuthorizationPolicy.ALLOW_ALL.getCode() : application.getAuthorizationPolicy().getCode();

        Set<String> assetPaths = new HashSet<>();

        if (application.getAssets() != null) {

            for (String assetName : application.getAssets().keySet()) {

                String assetPath = application.getAssets().get(assetName).getFile() == null ? assetName : application.getAssets().get(assetName).getFile().getPath();

                assetPaths.add(assetPath);
            }
        }

        BoundStatement statement = updateApplicationStatement.bind(authorizationPolicyCode, freeform, assetPaths, Collections.EMPTY_SET, null, domain, name); // TODO Set screen_ids and theme_id columns property in future.

        session.execute(statement);
    }
    
    static void activateDeployment(String domain, String applicationName, UUID timeUUID) {

        BoundStatement allAppDeployment = selectAllAppDeploymentsStatement.bind(domain, applicationName);
        List<Row> deployRows = session.execute(allAppDeployment).all();

        BatchStatement batchStatement = new BatchStatement();
        for (Row deployRow : deployRows) { //deactivate all rest

            batchStatement.add(deActivateDeploymentStatement.bind(domain, applicationName, deployRow.getUUID("time")));

        }
        BoundStatement activateCurrent = activateDeploymentStatement.bind(domain, applicationName, timeUUID);
        batchStatement.add(activateCurrent);

        session.execute(batchStatement);
    }


    static void authorizeUsers(String domain, String applicationName, User[] users) {

        if (users == null) {
            return;
        }

        BatchStatement batchStatement = new BatchStatement();

        for (User user : users) {

            if (user.getDomain() != null && user.getId() != null && !user.getDomain().trim().isEmpty() && !user.getId().trim().isEmpty()) {

                BoundStatement statement = insertAuthorizedUserStatement.bind(domain, applicationName, user.getDomain(), user.getId());

                batchStatement.add(statement);
            }
        }

        session.execute(batchStatement);
    }

    static void deauthorizeUsers(String domain, String applicationName, User[] users) {

        if (users == null) {
            return;
        }

        BatchStatement batchStatement = new BatchStatement();

        for (User user : users) {

            if (user.getDomain() != null && user.getId() != null && !user.getDomain().trim().isEmpty() && !user.getId().trim().isEmpty()) {

                BoundStatement statement = deleteAuthorizedUserStatement.bind(domain, applicationName, user.getDomain(), user.getId());

                batchStatement.add(statement);
            }
        }

        session.execute(batchStatement);
    }

    static void authorizeGroups(String domain, String applicationName, UserGroup[] groups) {

        if (groups == null) {
            return;
        }

        BatchStatement batchStatement = new BatchStatement();

        for (UserGroup group : groups) {

            if (group.getDomain() != null && group.getName() != null && !group.getDomain().trim().isEmpty() && !group.getName().trim().isEmpty()) {

                BoundStatement statement = insertAuthorizedUserStatement.bind(domain, applicationName, group.getDomain(), group.getName());

                batchStatement.add(statement);
            }
        }

        session.execute(batchStatement);
    }

    static void deauthorizeGroups(String domain, String applicationName, UserGroup[] groups) {

        if (groups == null) {
            return;
        }

        BatchStatement batchStatement = new BatchStatement();

        for (UserGroup group : groups) {

            if (group.getDomain() != null && group.getName() != null && !group.getDomain().trim().isEmpty() && !group.getName().trim().isEmpty()) {

                BoundStatement statement = deleteAuthorizedGroupStatement.bind(domain, applicationName, group.getDomain(), group.getName());

                batchStatement.add(statement);
            }
        }

        session.execute(batchStatement);
    }

    private static Application createApplication(Row applicationRow) {

        String name = applicationRow.getString("name");
        String domain = applicationRow.getString("domain");
        int createUser = applicationRow.getInt("create_user");
        long createDate = applicationRow.getLong("create_date");
        int ownerId = applicationRow.getInt("ownerid");
        int authorizationPolicyCode = applicationRow.getInt("authorization_policy");
        boolean freeform = applicationRow.getBool("freeform");

        Set<String> assetPaths = applicationRow.getSet("asset_paths", String.class);

        Set<String> screenIds = Collections.EMPTY_SET; //TODO Consider screen IDs in the future
        Theme theme = null; //TODO Consider themes in the future

        Set<User> authorizedUsers = listAuthorizedUsers(domain, name);
        Set<UserGroup> authorizedGroups = listAuthorizedGroups(domain, name);

        Application application = new Application(name, domain, createUser, createDate, ownerId, AuthorizationPolicy.getAuthorizationPolicy(authorizationPolicyCode), freeform, null, null, null, authorizedUsers, authorizedGroups);

        if (assetPaths != null) {

            for (String assetPath : assetPaths) {

                File assetFile = new File(null, assetPath, null);
                Asset asset = new Asset();
                asset.setFile(assetFile);
                asset.setName(assetPath);

                application.addAsset(assetPath, asset);
            }
        }

        return application;
    }

    private static Set<User> listAuthorizedUsers(String domain, String name) {

        BoundStatement statement = selectAuthorizedUsersStatement.bind(domain, name);

        ResultSet resultSet = session.execute(statement);

        if (resultSet.isExhausted()) {

            return Collections.EMPTY_SET;
        }

        Set<User> users = new HashSet<>();

        for (Row authorizedUserRow : resultSet) {

            String userDomain = authorizedUserRow.getString("user_domain");
            String userId = authorizedUserRow.getString("user_id");

            users.add(new User(null, userId, userDomain, null, null, null, null));
        }

        return Collections.unmodifiableSet(users);
    }

    private static Set<UserGroup> listAuthorizedGroups(String domain, String name) {

        BoundStatement statement = selectAuthorizedGroupsStatement.bind(domain, name);

        ResultSet resultSet = session.execute(statement);

        if (resultSet.isExhausted()) {

            return Collections.EMPTY_SET;
        }

        Set<UserGroup> groups = new HashSet<>();

        for (Row authorizedGroupRow : resultSet) {

            String groupDomain = authorizedGroupRow.getString("group_domain");
            String groupName = authorizedGroupRow.getString("group_id");

            groups.add(new UserGroup(groupName, groupDomain, null, null));
        }

        return Collections.unmodifiableSet(groups);
    }
    
    static void authorizedCompanies(String applicationName, Companies[] companies) {
        
        if (companies == null) {
            return;
        }

        BatchStatement batchStatement = new BatchStatement();
        
        List<Row> companyList = session.execute(selectAppCompaniesStatement.bind(applicationName)).all();
        List<Row> removedCompanyList = new ArrayList<>();

        for (Companies company : companies) {

            if (company.getAppName() != null && company.getCompanyId() != null && !company.getAppName().trim().isEmpty() && !company.getCompanyId().trim().isEmpty()) {

                //if there does not exist any app company for defined app and company, then insert new authorised company
                Row appCompany = session.execute(selectAppCompanyStatement.bind(applicationName, company.getCompanyId())).one();
                if(appCompany == null){

                    batchStatement.add(insertCompanyStatement.bind(company.getAppName(), company.getCompanyId(), company.getCreateUser(), new Date().getTime()));
                
                }
                else{ //if there exists, then erase the company from the list which will be deleted
                    
                    LOGGER.debug("authorizedCompanies --> applicationName: {} - {}", applicationName, company.getCompanyId());
                    if(companyList != null)
                        for (Row companyRow : companyList) {

                            if(companyRow.getString("company_id").equals(appCompany.getString("company_id")))
                                removedCompanyList.add(companyRow);

                        }
                    
                }
                
            }
        }
        
        companyList.removeAll(removedCompanyList);
        
        if(companyList != null)
            for (Row companyRow : companyList) { //delete all rest

                batchStatement.add(deleteCompanyStatement.bind(applicationName, companyRow.getString("company_id")));

            }

        session.execute(batchStatement);
        
    }

    static void updateOwner(Application currentApp, Integer userId) {
        
        session.execute(updateApplicationOwnerStatement.bind(userId, currentApp.getDomain(), currentApp.getName()));
        
    }

    private static Companies createCompany(Row companyRow) {
        
        Companies company = new Companies(companyRow.getString("app_name"), companyRow.getString("company_id"), companyRow.getInt("create_user"), companyRow.getLong("create_date"));
        
        return company;
        
    }
    
    static SortedSet<Companies> listAppCompanies(String appName) {
        
        List<Row> companyList = session.execute(selectAppCompaniesStatement.bind(appName)).all();
        SortedSet<Companies> companies = new TreeSet<>();

        for (Row companyRow : companyList) {

            Companies company = createCompany(companyRow);
            companies.add(company);

        }

        LOGGER.debug("listAppCompanies --> Found {} companies for app '{}'.", companies.size(), appName);

        return Collections.unmodifiableSortedSet(companies);
        
    }
    
    static List<IconsSplashes> listIconsSplashes4App(String appName) {

        BoundStatement statement = selectIconsSplashes4AppStatement.bind(appName);

        ResultSet resultSet = session.execute(statement);

        List<IconsSplashes> iconsSplashes = new ArrayList<>();

        for (Row row : resultSet) {

            iconsSplashes.add(new IconsSplashes(appName, row.getString("imgid"), row.getString("platform"), row.getString("filename"), row.getInt("create_user"), row.getLong("create_date")));
            
        }

        LOGGER.debug("Found {} iconsSplashesMaps in app '{}'.", iconsSplashes.size(), appName);

        return iconsSplashes;
    }
    
    static void saveIconsSplashes4App(String appName, String imgId, String platform, String fileName, Integer userId){
        
        Row iconSplashRow = session.execute(selectAppIconsSplashesStatement.bind(appName, imgId)).one();
        
        if(iconSplashRow == null)
            session.execute(insertIconsSplashes2AppStatement.bind(imgId, appName, platform, fileName, userId, new Date().getTime()));
        else
            session.execute(updIconsSplashes4AppStatement.bind(fileName, imgId, appName, platform));
        
    }
    
}
