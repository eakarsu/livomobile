package com.livo.devices.cassandra;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.livo.devices.DevicesService;
import com.livo.devices.MobileDevices;

public class CassandraDevicesService implements DevicesService {
    
    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraDevicesService.class);

    protected void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based DevicesService implementation...");

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

        LOGGER.info("Successfully started Cassandra-based DevicesService implementation.");
    }

    protected void stop() {

        LOGGER.info("Stopping Cassandra-based DevicesService implementation...");
        
        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based DevicesService implementation.");
    }

    @Override
    public Set<MobileDevices> listMobileDevices() {
        LOGGER.debug("listMobileDevices");
        return CassandraHelper.listAllDevices();
        
    }

    @Override
    public void deleteDeviceId(String companyId, String userPrincipal, String deviceId) {
        LOGGER.debug("{} will be deleted.", deviceId);
        
        CassandraHelper.deleteDeviceId(companyId, userPrincipal, deviceId);
        
        LOGGER.debug("{} was deleted.", deviceId);

    }
}
