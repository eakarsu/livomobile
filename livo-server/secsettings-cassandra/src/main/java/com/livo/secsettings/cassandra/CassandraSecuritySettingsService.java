package com.livo.secsettings.cassandra;

import com.livo.secsettings.SecuritySettings;
import com.livo.secsettings.SecuritySettingsService;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraSecuritySettingsService implements SecuritySettingsService {
    
    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraSecuritySettingsService.class);
    
    protected void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based CassandraSecuritySettingsService implementation...");

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

    @Override
    public void createSecuritySettings(SecuritySettings secSettings) {
        
        if (secSettings == null) {

            LOGGER.error("Passed SecuritySettings object to be used for creation is null.");

            throw new NullPointerException();
        }
        
        CassandraHelper.insertSecSettings(secSettings);
        
    }

    @Override
    public Set<SecuritySettings> listSecuritySettings() {
        
        return CassandraHelper.listSecuritySettings();
        
    }

    @Override
    public void saveSecuritySettings(SecuritySettings secSettings) {
        
        if (secSettings == null) {

            LOGGER.error("Passed SecuritySettings object to be used for creation is null.");

            throw new NullPointerException();
        }
        
        if (secSettings.getSettingValue() == null) {

            LOGGER.debug("Passed the value of SecuritySettings is null.");

            throw new NullPointerException();
        }
        
        CassandraHelper.updateSecuritySettings(secSettings);
        
    }

    @Override
    public SecuritySettings securitySettingExists(String settingKey) {
        
        if (settingKey == null) {

            LOGGER.error("Passed settingKey object to be used for exist control is null.");

            throw new NullPointerException();
        }
        
        return CassandraHelper.securitySettingExists(settingKey);
        
    }

}
