package tr.com.eno.livo.server.management.jmx;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Map;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.livo.devices.DevicesService;

public class DevicesServicePublisher {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(DevicesServicePublisher.class);

    private ObjectName objectName;
    private MBeanServer server;
    private DevicesService service;

    protected void registerService(DevicesService service,
            Map<String, Object> serviceProps) {

        String pid = (String) serviceProps.get(Constants.SERVICE_PID);

        LOGGER.debug(
                "Registering the service instance with the PID '{}' to the platform MBean server...",
                pid);

        this.service = service;
    }

    protected void start(Map<String, Object> config) throws IOException,
            InstanceAlreadyExistsException, MBeanRegistrationException,
            NotCompliantMBeanException, MalformedObjectNameException {

        LOGGER.info("Starting the JMX publisher for the DEVICES Service ---> {}", ObjectNames.DEVICES_SERVICE);

        this.objectName = new ObjectName(ObjectNames.DEVICES_SERVICE);

        this.server = ManagementFactory.getPlatformMBeanServer();

        this.server
                .registerMBean(this.service, this.objectName);
    }

    protected void stop() throws MBeanRegistrationException,
            InstanceNotFoundException, MalformedObjectNameException {

        LOGGER.info("Stopping the JMX publisher for the DEVICES Service...");

        this.server.unregisterMBean(this.objectName);
    }

    protected void unregisterService(DevicesService service,
            Map<String, Object> serviceProps) {

        this.service = null;

        String pid = (String) serviceProps.get(Constants.SERVICE_PID);

        LOGGER.debug(
                "Unregistering the service instance with the PID '{}' from the platform MBean server...",
                pid);
    }
}
