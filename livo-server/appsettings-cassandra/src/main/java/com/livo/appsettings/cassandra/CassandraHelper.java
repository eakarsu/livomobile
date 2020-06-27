/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.appsettings.cassandra;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.livo.appsettings.AppSettings;
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
    private static PreparedStatement selectAppSettingsStatement;
    private static PreparedStatement selectAppSettingsKeyStatement;
    private static PreparedStatement selectAppSettings4ApplicationStatement;
    private static PreparedStatement insertAppSettingsStatement;
    private static PreparedStatement updateAppSettingsStatement;
    private static PreparedStatement deleteAppSettingsStatement;
    
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

            LOGGER.debug("Creating table application.settings...");

            session.execute("CREATE TABLE IF NOT EXISTS application.settings(setting_id int, setting_key text, setting_val text, application_name text, record_user int, record_date bigint,  PRIMARY KEY (setting_key,application_name));");

            LOGGER.debug("Preparing statements...");

            selectAppSettingsStatement = session.prepare("SELECT * FROM application.settings;");
            selectAppSettingsKeyStatement = session.prepare("SELECT * FROM application.settings WHERE setting_key = ? and application_name = ?;");
            selectAppSettings4ApplicationStatement = session.prepare("SELECT * FROM application.settings WHERE application_name = ? ALLOW FILTERING;");
            insertAppSettingsStatement = session.prepare("INSERT INTO application.settings (setting_id, setting_key, setting_val, application_name, record_user, record_date) VALUES (?, ?, ?, ?, ?, ?);");
            updateAppSettingsStatement = session.prepare("UPDATE application.settings SET setting_val = ?, record_user = ?, record_date = ? WHERE application_name = ? and setting_key = ?;");
            deleteAppSettingsStatement = session.prepare("DELETE FROM application.settings WHERE application_name = ? and setting_key = ?;");
    
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
    
    static void insertAppSettings(AppSettings appSettings) {

        Integer settingId = appSettings.getSettingKey().hashCode();
        String settingKey = appSettings.getSettingKey();
        String settingValue = appSettings.getSettingValue();
        String applicationName = appSettings.getApplicationName();
        Integer recordUser = appSettings.getRecordUser();
        Long recordDate = appSettings.getRecordDate();
 
        LOGGER.debug("CassandraHelper.insertAppSettings() -- > " + settingId + " -- " + settingKey);
        BoundStatement statement = insertAppSettingsStatement.bind(settingId, settingKey, settingValue, applicationName, recordUser, recordDate);
        
        Set<AppSettings> allAppSettings = listAppSettingsFor(applicationName, settingKey.split("\\.").length > 1 ? settingKey.split("\\.")[0] : settingKey);
        if(allAppSettings.size() != 0){
            for (AppSettings appSetting : allAppSettings) {
                LOGGER.debug("CassandraHelper.insertAppSettings():deleted -- > " + applicationName + " -- " + appSetting.getSettingKey());
                session.execute(deleteAppSettingsStatement.bind(applicationName,appSetting.getSettingKey()));
            }
        }

        session.execute(statement);
    }
    
    private static AppSettings createAppSettings(Row appSettingsRow) {
        LOGGER.debug("createAppSettings::resultSet size: {}", (appSettingsRow != null));
        if(appSettingsRow != null){
            Integer settingId = appSettingsRow.getInt("setting_id");
            String settingKey = appSettingsRow.getString("setting_key");
            String settingValue = appSettingsRow.getString("setting_val");
            String applicationName = appSettingsRow.getString("application_name");
            Integer recordUser = appSettingsRow.getInt("record_user");
            Long recordDate = appSettingsRow.getLong("record_date");

            AppSettings appSettings = new AppSettings(settingId, settingKey, settingValue, applicationName, recordUser, recordDate);

            return appSettings;
        }
        else
            return null;
        
    }
    
    static Set<AppSettings> listAppSettings() {

        BoundStatement statement = selectAppSettingsStatement.bind();

        List<Row> resultList = session.execute(statement).all();

        Set<AppSettings> appSettings = new HashSet<>();

        for (Row settRow : resultList) {

            AppSettings appSetting = createAppSettings(settRow);

            if (appSetting != null) {
                LOGGER.debug("Added Key: {} --> Value: {} for {}", appSetting.getSettingKey(), appSetting.getSettingValue(), appSetting.getApplicationName());
                appSettings.add(appSetting);
            }
        }

        LOGGER.debug("Found {} app settings.", appSettings.size());

        return Collections.unmodifiableSet(appSettings);
    }
    
    static Set<AppSettings> listAppSettingsFor(String appName) {

        BoundStatement statement = selectAppSettings4ApplicationStatement.bind(appName);

        List<Row> resultList = session.execute(statement).all();

        Set<AppSettings> appSettings = new HashSet<>();

        for (Row settRow : resultList) {

            AppSettings appSetting = createAppSettings(settRow);

            if (appSetting != null) {
                LOGGER.debug("Added Key: {} --> Value: {} for {}", appSetting.getSettingKey(), appSetting.getSettingValue(), appSetting.getApplicationName());
                appSettings.add(appSetting);
            }
        }

        LOGGER.debug("Found {} app settings.", appSettings.size());

        return Collections.unmodifiableSet(appSettings);
    }
    
    static Set<AppSettings> listAppSettingsFor(String appName, String startWith) {

        BoundStatement statement = selectAppSettings4ApplicationStatement.bind(appName);

        List<Row> resultList = session.execute(statement).all();

        Set<AppSettings> appSettings = new HashSet<>();

        for (Row settRow : resultList) {

            AppSettings appSetting = createAppSettings(settRow);

            if (appSetting != null && appSetting.getSettingKey().startsWith(startWith + ".")) {
                LOGGER.debug("Added Key: {} --> Value: {} for {}", appSetting.getSettingKey(), appSetting.getSettingValue(), appSetting.getApplicationName());
                appSettings.add(appSetting);
            }
        }

        LOGGER.debug("Found {} app settings.", appSettings.size());

        return Collections.unmodifiableSet(appSettings);
    }
    
    static AppSettings appSettingExists(String keyBase, String source, String appName) {
        
        BoundStatement allAppStatement = selectAppSettings4ApplicationStatement.bind(appName);
        BoundStatement statement = selectAppSettingsKeyStatement.bind(keyBase + source, appName);
        BoundStatement delStatement = deleteAppSettingsStatement.bind(keyBase + source, appName);
        
        AppSettings appSettings = null;
        List<Row> allAppSettings = session.execute(allAppStatement).all();
        for (Row sett : allAppSettings) {
            
            if(sett.getString("setting_key").startsWith(keyBase)){
                statement = selectAppSettingsKeyStatement.bind(sett.getString("setting_key"), appName);

                Row row = session.execute(statement).one();
                if(row != null && !row.isNull("setting_key") && !row.isNull("application_name")){

                    LOGGER.debug("Key: Val --> {}: {}", row.getString("setting_key"), row.getString("setting_val"));
                    if(row.getString("setting_key").equals(keyBase + source)){
                        Integer settingId = row.getInt("setting_id");
                        String settingKey = row.getString("setting_key");
                        String settingValue = row.getString("setting_val");
                        String applicationName = row.getString("application_name");
                        Integer recordUser = row.getInt("record_user");
                        Long recordDate = row.getLong("record_date");

                        appSettings = new AppSettings(settingId, settingKey, settingValue, applicationName, recordUser, recordDate);
                    }
                    else{//An application must have only one authorization setting. So, if there exists an another one, delete it.
                        
                        LOGGER.debug("delStatement --> {} -- {}", row.getString("setting_key"), appName);
                        delStatement = deleteAppSettingsStatement.bind(appName, row.getString("setting_key"));
                        session.execute(delStatement);
                        
                    }

                }
            }

        }
        
        return appSettings;
            
    }

    static void updateAppSettings(AppSettings appSettings) {

        String settingKey = appSettings.getSettingKey();
        String settingValue = appSettings.getSettingValue();
        String applicationName = appSettings.getApplicationName();
        Integer recordUser = appSettings.getRecordUser();
        Long recordDate = appSettings.getRecordDate();

        BoundStatement statement = updateAppSettingsStatement.bind(settingValue, recordUser, recordDate, applicationName, settingKey);
        
        session.execute(statement);
    }
    
    static void deleteAllAppSettings4(String appName) {

        LOGGER.debug("CassandraHelper.deleteAllAppSettings4() -- > " + appName);

        Set<AppSettings> allAppSettings = listAppSettingsFor(appName);
        if(allAppSettings.size() != 0){
            for (AppSettings appSetting : allAppSettings) {
                LOGGER.debug("CassandraHelper.deleteAllAppSettings4():deleted -- > " + appName + " -- " + appSetting.getSettingKey());
                session.execute(deleteAppSettingsStatement.bind(appName,appSetting.getSettingKey()));
            }
        }

    }
    
}
