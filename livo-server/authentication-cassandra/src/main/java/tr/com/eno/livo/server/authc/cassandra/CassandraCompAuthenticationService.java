package tr.com.eno.livo.server.authc.cassandra;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.application.Companies;
import tr.com.eno.livo.server.authc.AuthenticationToken;
import tr.com.eno.livo.server.authc.CompanyAuthenticationService;

public class CassandraCompAuthenticationService implements CompanyAuthenticationService {

    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final String CASSANDRA_USER_DOMAIN = "System";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraCompAuthenticationService.class);

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

        LOGGER.info("Successfully started Cassandra-based CassandraCompAuthenticationService implementation.");
    }

    protected void stop() {

        LOGGER.info("Stopping Cassandra-based CassandraCompAuthenticationService implementation...");

        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based CassandraCompAuthenticationService implementation.");
    }

    @Override
    public AuthenticationToken login(String companyId, String companySecret, String appName) throws SecurityException {
        
        LOGGER.info("CassandraCompAuthenticationService login implementation --> {} - {}", companyId, companySecret);

        if (companyId == null) {

            LOGGER.error("Passed company ID is null.");

            throw new NullPointerException();
        }

        if (companySecret == null) {

            LOGGER.error("Passed company password is null.");

            throw new NullPointerException();
        }
        
        //LivoAny and LivoUsers are auto entered companies. So, we can not look a app company for them.
        if(!companyId.equals("LivoAny") && !companyId.equals("LivoUsers")){
            List<Companies> companyList = CassandraHelper.listAppCompanies(appName);
            if (companyList.size() == 0) {

                LOGGER.error("There does not exist any company for '{}'.", appName);

                throw new SecurityException("Company Authentication failed.");

            }
            boolean companyInList = false;
            for (Companies company : companyList) {
                if(company.getCompanyId().equals(companyId)) companyInList = true;
            }
            if(!companyInList){

                LOGGER.error("The company Id '{}' does not exist in authorized company list for '{}'.", companyId, appName);

                throw new SecurityException("Company Authentication failed.");

            }
        }
        
        LOGGER.debug("Logging in company '{}'...", companyId);
        
        if (!CassandraHelper.checkCompany(companyId, companySecret)) {
            
            LOGGER.error("Authentication of company '{}' failed.", companyId);
            
            throw new SecurityException("Company Authentication failed.");
        }
        
        Calendar calendar = Calendar.getInstance();

        Date currentDate = calendar.getTime();

        calendar.add(Calendar.MINUTE, 10);

        Date expirationDate = calendar.getTime();
                
        AuthenticationToken token = new AuthenticationToken(companyId, null, null, null, UUID.randomUUID().toString(), currentDate, expirationDate);
        
        return token;
    }

    @Override
    public void logout(AuthenticationToken token) throws SecurityException {
        
        LOGGER.debug("Logging out company '{}'...", token.getCompanyId());
    }
}
