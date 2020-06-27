package tr.com.eno.livo.server.authc.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import org.apache.shiro.codec.Hex;
import org.apache.shiro.crypto.hash.Sha512Hash;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.application.Companies;

public class CassandraHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHelper.class);
    private static Cluster cluster;
    private static Session session;
    private static PreparedStatement selectDeviceIdsStatement;
    private static PreparedStatement selectSecSettingsKeyStatement;
    private static PreparedStatement createDeviceIdStatement;
    private static PreparedStatement selectUserPasswordStatement;
    private static PreparedStatement selectCompPasswordStatement;
    private static PreparedStatement selectAppCompaniesStatement;
    private static PreparedStatement selectAppSettingsKeyStatement;
    private static PreparedStatement selectGroupStatement;
    private static PreparedStatement selectGroupCompStatement;

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

            LOGGER.debug("Creating keyspace...");

            session.execute("CREATE KEYSPACE IF NOT EXISTS security WITH replication = { 'class': 'SimpleStrategy', 'replication_factor': 1 }");

            LOGGER.debug("Creating tables security.devices, security.users, security.companies...");

            session.execute("CREATE TABLE IF NOT EXISTS security.devices (company_id text, user_principal text, device_id text, record_date bigint,  PRIMARY KEY (company_id, user_principal, device_id));");
            session.execute("CREATE TABLE IF NOT EXISTS security.users (id text, mail text, group_id text, first_name text, last_name text, password text, password_salt text, active boolean, PRIMARY KEY (id));");
            session.execute("CREATE TABLE IF NOT EXISTS security.companies (record_id int, company_id text, company_title text, create_date bigint, create_user int, login_perm boolean, password text, password_salt text, PRIMARY KEY (record_id));");

            LOGGER.debug("Preparing statements...");

            selectDeviceIdsStatement = session.prepare("SELECT device_id FROM security.devices WHERE company_id = ? AND user_principal = ?;");
            selectSecSettingsKeyStatement = session.prepare("SELECT * FROM security.settings WHERE setting_key = ? ALLOW FILTERING;");
            selectAppCompaniesStatement = session.prepare("SELECT * FROM application.companies WHERE app_name = ?;");
            selectAppSettingsKeyStatement = session.prepare("SELECT * FROM application.settings WHERE setting_key = ? and application_name = ?;");
            selectGroupStatement = session.prepare("SELECT * FROM security.user_groups WHERE name = ?;");
            selectGroupCompStatement = session.prepare("SELECT * FROM security.group_companies WHERE group_name = ? and company_id = ?;");
            createDeviceIdStatement = session.prepare("INSERT INTO security.devices(company_id,user_principal,device_id,record_date) VALUES(?,?,?,?);");

            selectUserPasswordStatement = session.prepare("SELECT password, password_salt FROM security.users WHERE id = ?;");
            selectCompPasswordStatement = session.prepare("SELECT password, password_salt FROM security.companies WHERE company_id = ?;");

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
    
    static int getDeviceLimitFromSSs(String key) {

        LOGGER.debug("getDeviceLimitFromSSs() is started");
        BoundStatement statement = selectSecSettingsKeyStatement.bind(key);

        Row row = session.execute(statement).one();
        
        LOGGER.debug("Key: Row --> {}: {}", key, row);

        if(row == null || row.isNull("setting_val"))
            return -1;
        else{

            return Integer.parseInt(row.getString("setting_val"));
            
        }
            
    }

    static Set<String> getDeviceIDs(String companyId, String userPrincipal) {

        BoundStatement statement = selectDeviceIdsStatement.bind(companyId, userPrincipal);

        List<Row> rows = session.execute(statement).all();
        LOGGER.debug("Search devices associated with the company ID '{}' and user principal '{}'", companyId, userPrincipal);
        if (rows == null || rows.size() == 0) { //row.isNull("device_ids")

            LOGGER.debug("No devices associated with the company ID '{}' and user principal '{}' were found.", companyId, userPrincipal);

            return Collections.EMPTY_SET;
        }

        Set<String> rawSet = new HashSet<>();
        for (Row row : rows) {
            rawSet.add(row.getString("device_id"));
        }
        //Set<String> rawSet = row.getSet("device_ids", String.class);
        LOGGER.debug("rawSet size: '{}'", rawSet.size());

        return Collections.unmodifiableSet(rawSet);
    }

    static void registerDeviceID(String companyId, String userPrincipal, String deviceId) {

        BoundStatement statement = createDeviceIdStatement.bind(companyId, userPrincipal, deviceId, new Date().getTime());

        session.execute(statement);
    }

    static boolean checkPassword(String id, String password) {

        BoundStatement statement = selectUserPasswordStatement.bind(id.toLowerCase(Locale.ENGLISH));

        Row row = session.execute(statement).one();

        if (row == null) {

            LOGGER.debug("User '{}' not found.", id);

            return false;
        }

        if (row.isNull("password")) {

            LOGGER.debug("Password retrieved from the database is null.");

            return false;

        }

        if (row.isNull("password_salt")) {

            LOGGER.debug("Password salt retrieved the from database is null.");

            return false;
        }

        String passwordHash = row.getString("password");
        String passwordSalt = row.getString("password_salt");

        ByteSource passwordSource = ByteSource.Util.bytes(password);
        ByteSource passwordSaltSource = ByteSource.Util.bytes(Hex.decode(passwordSalt));

        Sha512Hash hash = new Sha512Hash(passwordSource, passwordSaltSource, 3);

        return Objects.equals(hash.toHex(), passwordHash);
    }
    
    static boolean checkCompany(String id, String password) {

        BoundStatement statement = selectCompPasswordStatement.bind(id);

        Row row = session.execute(statement).one();

        if (row == null) {

            LOGGER.debug("Company '{}' not found.", id);

            return false;
        }

        if (row.isNull("password")) {

            LOGGER.debug("Password retrieved from the database is null.");

            return false;

        }

        if (row.isNull("password_salt")) {

            LOGGER.debug("Password salt retrieved the from database is null.");

            return false;
        }

        String passwordHash = row.getString("password");
        String passwordSalt = row.getString("password_salt");

        ByteSource passwordSource = ByteSource.Util.bytes(password);
        ByteSource passwordSaltSource = ByteSource.Util.bytes(Hex.decode(passwordSalt));

        Sha512Hash hash = new Sha512Hash(passwordSource, passwordSaltSource, 3);

        return Objects.equals(hash.toHex(), passwordHash);
    }
    
    static List<Companies> listAppCompanies(String appName) {
        
        List<Row> companyList = session.execute(selectAppCompaniesStatement.bind(appName)).all();
        List<Companies> companies = new ArrayList<>();

        for (Row companyRow : companyList) {

            Companies company = createCompany(companyRow);
            companies.add(company);

        }

        LOGGER.debug("listAppCompanies --> Found {} companies for app '{}'.", companies.size(), appName);

        return companies;
        
    }
    
    private static Companies createCompany(Row companyRow) {
        
        Companies company = new Companies(companyRow.getString("app_name"), companyRow.getString("company_id"), companyRow.getInt("create_user"), companyRow.getLong("create_date"));
        
        return company;
        
    }
    
    static int getDeviceLimit4App(String appName) {
        
        BoundStatement statement = selectAppSettingsKeyStatement.bind("device.limit", appName);
        Row row = session.execute(statement).one();
        int deviceLimit4App = -1;
        if(row != null && !row.isNull("setting_val")){

            LOGGER.debug("Key: Val --> {}: {}", row.getString("setting_key"), row.getString("setting_val"));

            deviceLimit4App = Integer.parseInt(row.getString("setting_val"));

        }
        
        return deviceLimit4App;
            
    }
    
    static List<String> getSystemGroups4App(String appName) {
        
        BoundStatement statement = selectAppSettingsKeyStatement.bind("authorization.System", appName);
        List<String> systemGroups = new ArrayList<>();
        Row row = session.execute(statement).one();
        if(row != null && !row.isNull("setting_val")){

            LOGGER.debug("Key: Val --> {}: {}", row.getString("setting_key"), row.getString("setting_val"));

            systemGroups.addAll(Arrays.asList(row.getString("setting_val").split(",")));

        }
        
        statement = selectAppSettingsKeyStatement.bind("authorization.System_withcomp", appName);
        row = session.execute(statement).one();
        if(row != null && !row.isNull("setting_val")){

            LOGGER.debug("Key: Val --> {}: {}", row.getString("setting_key"), row.getString("setting_val"));

            systemGroups.addAll(Arrays.asList(row.getString("setting_val").split(",")));

        }
        
        return systemGroups;
            
    }
    
    static boolean userExistsInGroup(String groupName, String uId) {
        
        LOGGER.debug("userExistsInGroup::user id '{}' and group name '{}'.", uId, groupName);

        if (groupName == null) {

            LOGGER.debug("Group name to find is null; returning null...");

            return false;
        }

        BoundStatement statement = selectGroupStatement.bind(groupName);

        Row row = session.execute(statement).one();
        if (row == null) {

            LOGGER.debug("Cassandra row is null.");

            return false;
        }

        if (row.isNull("name")) {

            LOGGER.error("Group name column of security.user_groups row is null.");

            return false;
        }
        
        if (row.isNull("user_ids")) {

            LOGGER.error("Group user_ids column of security.user_groups row is null.");

            return false;
        }
        
        Set<String> userIDs = row.getSet("user_ids", String.class);
        for (String userID : userIDs) {
            
            if(userID.equals(uId))
                return true;
            
        }

        return false;
    }
    
    static boolean companyExistsInGroup(String groupName, String companyId) {
        
        LOGGER.debug("companyExistsInGroup::company id '{}' and group name '{}'.", companyId, groupName);

        try {
            
            //LivoAny or LivoUsers are auto entered companies. So, we can not look a group for them.
            if(companyId.equals("LivoAny") || companyId.equals("LivoUsers"))
                return true;
            
            Row rowGroupComp = session.execute(selectGroupCompStatement.bind(groupName, companyId.hashCode())).one();
            if(rowGroupComp == null) {

                LOGGER.error("Group user_ids column of security.user_groups row is null.");

                return false;
                
            }

        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
        } finally {
            LOGGER.debug("Finally");
        }
        
        return true;
        
    }
        
}
