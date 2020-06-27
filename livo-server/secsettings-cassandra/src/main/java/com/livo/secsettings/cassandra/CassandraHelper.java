/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.secsettings.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.livo.secsettings.SecuritySettings;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Macintosh
 */
public class CassandraHelper {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraHelper.class);
    private static Cluster cluster;
    private static Session session;
    private static PreparedStatement selectSecSettingsStatement;
    private static PreparedStatement selectSecSettingsKeyStatement;
    private static PreparedStatement insertSecSettingsStatement;
    private static PreparedStatement updateSecSettingsStatement;
    
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

            LOGGER.debug("Creating tables security.settings...");

            session.execute("CREATE TABLE IF NOT EXISTS security.settings(setting_id int, setting_key text, setting_val text, record_date bigint,  PRIMARY KEY (setting_id, setting_key));");

            LOGGER.debug("Preparing statements...");

            selectSecSettingsStatement = session.prepare("SELECT * FROM security.settings;");
            selectSecSettingsKeyStatement = session.prepare("SELECT * FROM security.settings WHERE setting_key = ? ALLOW FILTERING;");
            insertSecSettingsStatement = session.prepare("INSERT INTO security.settings (setting_id, setting_key, setting_val, record_date) VALUES (?, ?, ?, ?);");
            updateSecSettingsStatement = session.prepare("UPDATE security.settings SET setting_val = ?, record_date = ? WHERE setting_id = ? and setting_key = ?;");
    
            LOGGER.debug("Initializing statements...");

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
    
    static void insertSecSettings(SecuritySettings securitySettings) {

        Integer settingId = securitySettings.getSettingKey().hashCode();
        String settingKey = securitySettings.getSettingKey();
        String settingValue = securitySettings.getSettingValue();
        Long recordDate = securitySettings.getRecordDate();
 
        System.out.println("CassandraHelper.insertSecSettings() -- > " + settingId + " -- " + settingKey);
        BoundStatement statement = insertSecSettingsStatement.bind(settingId, settingKey, settingValue, recordDate);

        session.execute(statement);
    }
    
    private static SecuritySettings createSecuritySettings(Row securitySettingsRow) {
        LOGGER.debug("createSecuritySettings::resultSet size: {}", (securitySettingsRow != null));
        if(securitySettingsRow != null){
            Integer settingId = securitySettingsRow.getInt("setting_id");
            String settingKey = securitySettingsRow.getString("setting_key");
            String settingValue = securitySettingsRow.getString("setting_val");
            Long recordDate = securitySettingsRow.getLong("record_date");

            SecuritySettings securitySettings = new SecuritySettings(settingId, settingKey, settingValue, recordDate);

            return securitySettings;
        }
        else
            return null;
        
    }
    
    static Set<SecuritySettings> listSecuritySettings() {

        BoundStatement statement = selectSecSettingsStatement.bind();

        List<Row> resultList = session.execute(statement).all();

        Set<SecuritySettings> securitySettings = new HashSet<>();

        for (Row settRow : resultList) {

            SecuritySettings securitySetting = createSecuritySettings(settRow);

            if (securitySetting != null) {
                LOGGER.debug("Added Key: {} --> Value: {}", securitySetting.getSettingKey(), securitySetting.getSettingValue());
                securitySettings.add(securitySetting);
            }
        }

        LOGGER.debug("Found {} security settings.", securitySettings.size());

        return Collections.unmodifiableSet(securitySettings);
    }
    
    static SecuritySettings securitySettingExists(String key) {

        BoundStatement statement = selectSecSettingsKeyStatement.bind(key);

        Row row = session.execute(statement).one();
        
        LOGGER.debug("Key: Row --> {}: {}", key, row);

        if(row == null || row.isNull("setting_id"))
            return null;
        else{
            
            Integer settingId = row.getInt("setting_id");
            String settingKey = row.getString("setting_key");
            String settingValue = row.getString("setting_val");
            Long recordDate = row.getLong("record_date");
            
            SecuritySettings secSettings = new SecuritySettings(settingId, settingKey, settingValue, recordDate);
            
            return secSettings;
            
        }
            
    }

    static void updateSecuritySettings(SecuritySettings securitySettings) {

        Integer settingId = securitySettings.getSettingId();
        String settingKey = securitySettings.getSettingKey();
        String settingValue = securitySettings.getSettingValue();
        Long recordDate = securitySettings.getRecordDate();

        BoundStatement statement = updateSecSettingsStatement.bind(settingValue, recordDate, settingId, settingKey);
        
        session.execute(statement);
    }
    
}
