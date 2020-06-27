package tr.com.eno.livo.server.authc.cassandra;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.authc.AuthenticationToken;
import tr.com.eno.livo.server.authc.UserAuthenticationService;

public class CassandraUserAuthenticationService implements UserAuthenticationService {

    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final String CASSANDRA_USER_DOMAIN = "System";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraUserAuthenticationService.class);

    protected void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based UserAuthenticationService implementation...");

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

        LOGGER.info("Successfully started Cassandra-based UserAuthenticationService implementation.");
    }

    protected void stop() {

        LOGGER.info("Stopping Cassandra-based UserAuthenticationService implementation...");

        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based UserAuthenticationService implementation.");
    }

    @Override
    public AuthenticationToken login(AuthenticationToken companyAuthToken, String userPrincipal, String userCredentials, String appName) throws SecurityException {

        if (userPrincipal == null) {

            LOGGER.error("Passed user ID is null.");

            throw new NullPointerException();
        }

        if (userCredentials == null) {

            LOGGER.error("Passed user password is null.");

            throw new NullPointerException();
        }

        if (companyAuthToken == null) {

            LOGGER.error("Passed authentication token is null.");

            throw new NullPointerException();
        }
        
        if(!userPrincipal.equals("livoany") && !userPrincipal.equals("livocomp")){ //livoany and livocomp are auto entered users. So, we can not look a app user group for them.
            LOGGER.info("Check in the user '{}' whether he/she is arranged to a app '{}' group or not ...", userPrincipal, appName);
            List<String> systemGroups = CassandraHelper.getSystemGroups4App(appName);
            if (systemGroups.size() == 0) {

                LOGGER.error("Any System Group did not defined for app '{}'.", appName);

                throw new SecurityException("User Authentication failed.");

            }
            boolean userExistsInAppGroups = false;
            String groupName = "";
            for (String systemGroup : systemGroups) {

                groupName = systemGroup.split("\\.")[1];
                userExistsInAppGroups = CassandraHelper.userExistsInGroup(groupName, userPrincipal);
                if(userExistsInAppGroups){

                    LOGGER.info("Check in the company '{}' whether it is defined in the group '{}' or not ...", companyAuthToken.getCompanyId(), groupName);
                    if (!CassandraHelper.companyExistsInGroup(groupName, companyAuthToken.getCompanyId())) {

                        LOGGER.error("The company '{}' does not defined in group '{}' of user '{}'.", companyAuthToken.getCompanyId(), groupName, userPrincipal);

                        throw new SecurityException("User Authentication failed.");

                    }
                    break;

                }

            }
            if (!userExistsInAppGroups) {

                LOGGER.error("The user '{}' does not exist in app '{}' groups.", userPrincipal, appName);

                throw new SecurityException("User Authentication failed.");

            }
        }
        
        LOGGER.debug("Logging in user '{}'...", userPrincipal);
        
        if (!CassandraHelper.checkPassword(userPrincipal, userCredentials)) {
            
            LOGGER.error("Authentication of user '{}' failed.", userPrincipal);
            
            throw new SecurityException("Authentication failed.");
        }
        
        LOGGER.info("Generate AuthenticationToken for the user '{}' and app '{}'...", userPrincipal, appName);
        
        AuthenticationToken token = new AuthenticationToken(companyAuthToken.getCompanyId(), userPrincipal.toLowerCase(Locale.ENGLISH), null, CASSANDRA_USER_DOMAIN, companyAuthToken.getUniqueValue(), new Date(), companyAuthToken.getExpirationTime());
        
        return token;
    }

    @Override
    public void logout(AuthenticationToken token) throws SecurityException {
        
        LOGGER.debug("Logging out user '{}'...", token.getUserPrincipal());
    }
}
