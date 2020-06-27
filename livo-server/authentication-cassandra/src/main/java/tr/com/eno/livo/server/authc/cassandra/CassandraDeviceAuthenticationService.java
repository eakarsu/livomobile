package tr.com.eno.livo.server.authc.cassandra;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.authc.AuthenticationToken;
import tr.com.eno.livo.server.authc.DeviceAuthenticationService;

public class CassandraDeviceAuthenticationService implements DeviceAuthenticationService {
    
    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final String DEVICE_LIMIT_CONFIGURATION_KEY = "device.limit";
    private static final String CASSANDRA_USER_DOMAIN = "System";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDeviceAuthenticationService.class);
    private static int deviceLimit;

    protected void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based DeviceAuthenticationService implementation...");

        String host = config.containsKey(CASSANDRA_HOST_CONFIGURATION_KEY) && config.get(CASSANDRA_HOST_CONFIGURATION_KEY) != null ? (String) config
                .get(CASSANDRA_HOST_CONFIGURATION_KEY) : "localhost";

        LOGGER.debug("Using Cassandra host '{}'...", host);

        int port = config.containsKey(CASSANDRA_PORT_CONFIGURATION_KEY) && config.get(CASSANDRA_PORT_CONFIGURATION_KEY) != null ? (Integer) config
                .get(CASSANDRA_PORT_CONFIGURATION_KEY) : 9042;

        LOGGER.debug("Using Cassandra port {}...", port);

        CassandraHelper.connect(host, port);
        
        //get device limit from db
        deviceLimit = CassandraHelper.getDeviceLimitFromSSs(DEVICE_LIMIT_CONFIGURATION_KEY);
        //if there does not exist devce limit in db, then get it from configuration file
        deviceLimit = deviceLimit != -1 ? deviceLimit : (config.containsKey(DEVICE_LIMIT_CONFIGURATION_KEY) && config.get(DEVICE_LIMIT_CONFIGURATION_KEY) != null ? (Integer) config.get(DEVICE_LIMIT_CONFIGURATION_KEY) : 3);
        
        LOGGER.debug("Setting device limit to {}...", deviceLimit);

        LOGGER.debug("Registering shutdown hook to disconnect from Cassandra...");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {

                CassandraHelper.disconnect();
            }
        }));

        LOGGER.info("Successfully started Cassandra-based DeviceAuthenticationService implementation.");
    }

    protected void stop() {

        LOGGER.info("Stopping Cassandra-based DeviceAuthenticationService implementation...");
        
        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based DeviceAuthenticationService implementation.");
    }

    @Override
    public AuthenticationToken login(AuthenticationToken userAuthToken, String deviceId, String appName) throws SecurityException {
        
        String companyId = userAuthToken.getCompanyId();
        String userPrincipal = userAuthToken.getUserPrincipal();
        int appDeviceLimit = CassandraHelper.getDeviceLimit4App(appName);
        appDeviceLimit = appDeviceLimit != -1 ? appDeviceLimit : deviceLimit; //If the device limit for app is defined, then receive it, else receive global value.
        
        Set<String> deviceIds = CassandraHelper.getDeviceIDs(companyId, userPrincipal);
            LOGGER.debug("Device size is '{}' and limited with '{}' using device ID '{}' for the company ID '{}' and user principal '{}'.", deviceIds.size(), deviceLimit, deviceId, companyId, userPrincipal);
        
        if(deviceId == null){

            throw new SecurityException("Device Problem!");
            
        }
            
        if (deviceIds.contains(deviceId)) {
            
            LOGGER.debug("Device ID '{}' is previously registered for the company ID '{}' and user principal '{}', returning...", deviceId, companyId, userPrincipal);
            
            return new AuthenticationToken(companyId, userPrincipal, deviceId, CASSANDRA_USER_DOMAIN, userAuthToken.getUniqueValue(), userAuthToken.getAuthenticationTime(), userAuthToken.getExpirationTime());
        }
        
        if (companyId.equals("LivoAny") || companyId.equals("LivoUsers") || deviceIds.size() < deviceLimit) { //< appDeviceLimit
            
            LOGGER.debug("Registering device ID '{}' for the company ID '{}' and user principal '{}'...", deviceId, companyId, userPrincipal);
            
            CassandraHelper.registerDeviceID(companyId, userPrincipal, deviceId);
            
            return new AuthenticationToken(companyId, userPrincipal, deviceId, CASSANDRA_USER_DOMAIN, userAuthToken.getUniqueValue(), userAuthToken.getAuthenticationTime(), userAuthToken.getExpirationTime());
            
        } else {
            
            LOGGER.debug("Device limit is reached for the company ID '{}' and user principal '{}'.Info::deviceId size: {} - appDeviceLimit: {} for app '{}'", companyId, userPrincipal, deviceIds.size(), appDeviceLimit, appName);
            
            throw new SecurityException("Device limit reached.");
        }
    }

    @Override
    public void logout(AuthenticationToken token) throws SecurityException {
    }

}
