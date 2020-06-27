package com.livo.devices;

import java.util.Set;
import javax.management.MXBean;

@MXBean
public interface DevicesService {
    
    public Set<MobileDevices> listMobileDevices();
    
    public void deleteDeviceId(String companyId, String userPrincipal, String deviceId);
    
}
