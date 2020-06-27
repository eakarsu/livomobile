package com.livo.devices;

import java.beans.ConstructorProperties;
import java.io.Serializable;

/**
 *
 * @author Macintosh
 */
public class MobileDevices implements Serializable, Comparable<MobileDevices> {
    
    private final String companyId;
    private final String userPrincipal;
    private final String deviceId;
    private final Long recordDate;

    @ConstructorProperties({"companyId", "userPrincipal", "deviceId", "recordDate"})
    public MobileDevices(String companyId, String userPrincipal, String deviceId, Long recordDate) {
        this.companyId = companyId;
        this.userPrincipal = userPrincipal;
        this.deviceId = deviceId;
        this.recordDate = recordDate;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getUserPrincipal() {
        return userPrincipal;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public Long getRecordDate() {
        return recordDate;
    }
    
    @Override
    public int compareTo(MobileDevices o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.companyId == null) {
            throw new NullPointerException();
        }

        if (this.userPrincipal == null) {
            throw new NullPointerException();
        }

        int companyIdComparison = this.companyId.compareTo(o.companyId);
        int userPrincipalComparison = this.userPrincipal.compareTo(o.userPrincipal);
        
        return companyIdComparison == 0 ? userPrincipalComparison : companyIdComparison;
    }
    
}

