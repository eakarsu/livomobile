/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.appsettings;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 *
 * @author Macintosh
 */
public class AppSettings implements Serializable, Comparable<AppSettings> {
    
    private final Integer settingId;
    private final String settingKey;
    private final String settingValue;
    private final String applicationName;
    private final Integer recordUser;
    private final Long recordDate;
    
    @ConstructorProperties({"settingId", "settingKey", "settingValue", "applicationName", "recordUser", "recordDate"})
    public AppSettings(Integer settingId, String settingKey, String settingValue, String applicationName, Integer recordUser, Long recordDate) {
        this.settingId = settingId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
        this.applicationName = applicationName;
        this.recordUser = recordUser;
        this.recordDate = recordDate;
    }

    public Integer getSettingId() {
        return settingId;
    }

    public String getSettingKey() {
        return settingKey;
    }

    public String getSettingValue() {
        return settingValue;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Integer getRecordUser() {
        return recordUser;
    }

    public Long getRecordDate() {
        return recordDate;
    }

    @Override
    public int compareTo(AppSettings as) {

        if (as == null) {
            throw new NullPointerException();
        }

        if (this.settingId == null) {
            throw new NullPointerException();
        }

        if (this.settingKey == null) {
            throw new NullPointerException();
        }

        int settingIdComparison = this.settingId.compareTo(as.settingId);
        int settingKeyComparison = this.settingKey.compareTo(as.settingKey);
        
        return settingIdComparison == 0 ? settingIdComparison : settingKeyComparison;
    }
    
}
