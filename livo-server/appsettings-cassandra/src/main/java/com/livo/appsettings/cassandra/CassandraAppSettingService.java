package com.livo.appsettings.cassandra;

import com.livo.appsettings.AppSettingService;
import com.livo.appsettings.AppSettings;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CassandraAppSettingService implements AppSettingService {
    
    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraAppSettingService.class);
    
    protected void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based CassandraAppSettingService implementation...");

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

        LOGGER.info("Successfully started Cassandra-based AppSettingService implementation.");
    }

    protected void stop() {

        LOGGER.info("Stopping Cassandra-based AppSettingService implementation...");

        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based AppSettingService implementation.");
    }

    @Override
    public void createAppSettings(AppSettings appSettings) {
        
        if (appSettings == null) {

            LOGGER.error("Passed AppSettingService object to be used for creation is null.");

            throw new NullPointerException();
        }
        
        CassandraHelper.insertAppSettings(appSettings);
        
    }

    @Override
    public Set<AppSettings> listAppSettings() {
        
        return CassandraHelper.listAppSettings();
        
    }
    
    @Override
    public Set<AppSettings> listAppSettingsFor(String appName) {
        
        if (appName == null) {

            LOGGER.error("listAppSettingsFor::Passed appName to be used for listing is null.");

            throw new NullPointerException();
        }
        
        return CassandraHelper.listAppSettingsFor(appName);
        
    }

    @Override
    public void saveAppSettings(AppSettings appSettings) {
        
        if (appSettings == null) {

            LOGGER.error("Passed AppSettingService object to be used for creation is null.");

            throw new NullPointerException();
        }
        
        if (appSettings.getSettingValue() == null) {

            LOGGER.debug("Passed the value of AppSettingService is null.");

            throw new NullPointerException();
        }
        
        CassandraHelper.updateAppSettings(appSettings);
        
    }

    @Override
    public AppSettings appSettingExists(String keyBase, String source, String appName) {
        
        if (keyBase == null || source == null) {

            LOGGER.error("Passed settingKey object to be used for exist control is null.");

            throw new NullPointerException();
        }
        
        if (appName == null) {

            LOGGER.error("Passed appName object to be used for exist control is null.");

            throw new NullPointerException();
        }
        
        return CassandraHelper.appSettingExists(keyBase, source, appName);
        
    }

    @Override
    public void delAppSettings(String appName) {
        CassandraHelper.deleteAllAppSettings4(appName);
    }

}
