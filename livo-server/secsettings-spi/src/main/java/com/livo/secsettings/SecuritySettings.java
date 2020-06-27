/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.secsettings;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 *
 * @author Macintosh
 */
public class SecuritySettings implements Serializable, Comparable<SecuritySettings> {
    
    private final Integer settingId;
    private final String settingKey;
    private final String settingValue;
    private final Long recordDate;

    @ConstructorProperties({"settingId", "settingKey", "settingValue", "recordDate"})
    public SecuritySettings(Integer settingId, String settingKey, String settingValue, Long recordDate) {
        this.settingId = settingId;
        this.settingKey = settingKey;
        this.settingValue = settingValue;
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

    public Long getRecordDate() {
        return recordDate;
    }
    
    @Override
    public int compareTo(SecuritySettings ss) {

        if (ss == null) {
            throw new NullPointerException();
        }

        if (this.settingId == null) {
            throw new NullPointerException();
        }

        if (this.settingKey == null) {
            throw new NullPointerException();
        }

        int settingIdComparison = this.settingId.compareTo(ss.settingId);
        int settingKeyComparison = this.settingKey.compareTo(ss.settingKey);
        
        return settingIdComparison == 0 ? settingIdComparison : settingKeyComparison;
    }
    
}
