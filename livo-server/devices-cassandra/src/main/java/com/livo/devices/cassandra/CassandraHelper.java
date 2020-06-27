package com.livo.devices.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.livo.devices.MobileDevices;

public class CassandraHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHelper.class);
    private static Cluster cluster;
    private static Session session;
    private static PreparedStatement selectAllDeviceIdsStatement;
    private static PreparedStatement deleteDeviceIdStatement;

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

            LOGGER.debug("Creating tables security.devices...");

            session.execute("CREATE TABLE IF NOT EXISTS security.devices (company_id text, user_principal text, device_id text, record_date bigint,  PRIMARY KEY (company_id, user_principal, device_id));");
            
            LOGGER.debug("Preparing statements...");

            selectAllDeviceIdsStatement = session.prepare("SELECT * FROM security.devices;");
            deleteDeviceIdStatement = session.prepare("DELETE FROM security.devices WHERE company_id = ? AND user_principal = ? AND device_id = ?;");

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
    
    private static MobileDevices createMobileDevice(Row deviceRow){
        
        if(deviceRow != null){
            String companyId = deviceRow.getString("company_id");
            String userPrincipal = deviceRow.getString("user_principal");
            String deviceId = deviceRow.getString("device_id");
            Long recordDate = deviceRow.getLong("record_date");

            MobileDevices mobileDevice = new MobileDevices(companyId, userPrincipal, deviceId, recordDate);

            return mobileDevice;
        }
        else
            return null;
        
        
    }
    
    static Set<MobileDevices> listAllDevices() {

        LOGGER.debug("listAllDevices");
        BoundStatement statement = selectAllDeviceIdsStatement.bind();

        List<Row> rows = session.execute(statement).all();
        LOGGER.debug("Selected all mobile devices");
        if (rows == null || rows.size() == 0) { //row.isNull("device_ids")

            LOGGER.debug("There is no device.");

            return Collections.EMPTY_SET;
        }

        Set<MobileDevices> rawSet = new HashSet<>();
        for (Row row : rows) {
            MobileDevices mobileDevice = createMobileDevice(row);
            if (mobileDevice != null) {
                LOGGER.debug("Added company: {} --> user: {}", mobileDevice.getCompanyId(), mobileDevice.getUserPrincipal());
                rawSet.add(mobileDevice);
            }
        }
        //Set<String> rawSet = row.getSet("device_ids", String.class);
        LOGGER.debug("rawSet size: '{}'", rawSet.size());

        return Collections.unmodifiableSet(rawSet);
    }
    
     static void deleteDeviceId(String companyId, String userPrincipal, String deviceId) {
         
        LOGGER.debug("deleteDeviceId: '{}'", "companyId: " + companyId + ", userPrincipal: " + userPrincipal + ", deviceId: " + deviceId);
        BoundStatement statement = deleteDeviceIdStatement.bind(companyId,userPrincipal,deviceId);

        session.execute(statement);
         
     }

}
