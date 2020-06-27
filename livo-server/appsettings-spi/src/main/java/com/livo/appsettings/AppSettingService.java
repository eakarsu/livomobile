/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.appsettings;

import java.util.Set;
import javax.management.MXBean;

/**
 *
 * @author omur
 */
@MXBean
public interface AppSettingService {
    
    public static final String AUTHENTICATION_SETTING_EVENT_TOPIC = "com/livo/appsettings";
    
    public void createAppSettings(AppSettings appSettings);
    
    public AppSettings appSettingExists(String keyBase, String source, String appName);
    
    public Set<AppSettings> listAppSettings();
    public Set<AppSettings> listAppSettingsFor(String appName);
    
    public void saveAppSettings(AppSettings appSettings);
    public void delAppSettings(String appName);
    
}
