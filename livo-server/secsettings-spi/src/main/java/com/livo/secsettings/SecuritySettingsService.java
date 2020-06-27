/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.secsettings;

import java.util.Set;
import javax.management.MXBean;

/**
 *
 * @author omur
 */
@MXBean
public interface SecuritySettingsService {
    
    public void createSecuritySettings(SecuritySettings secSettings);
    
    public SecuritySettings securitySettingExists(String settingKey);
    
    public Set<SecuritySettings> listSecuritySettings();
    
    public void saveSecuritySettings(SecuritySettings secSettings);
    
}
