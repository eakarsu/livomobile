package tr.com.eno.livo.server.aspect;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tr.com.eno.livo.server.application.Application;
import tr.com.eno.livo.server.application.ApplicationDeploymentFailedException;
import tr.com.eno.livo.server.application.ApplicationService;

/**
 *
 * @author dacay
 */
@Aspect
public class ApplicationProvisioningAspect {

    @Pointcut("execution(Integer tr.com.eno..ApplicationService+.deployApplication(tr.com.eno.livo.server.application.Application, int)) && args(application, userId)")
    public void deployApplicationPointcut(Application application, int userId) {}

    @Pointcut("execution(void tr.com.eno..ApplicationService+.deleteDeployment(..)) && args(deployId, domain, applicationName, timeUUID)")
    public void deleteDeploymentPointcut(Integer deployId, String domain, String applicationName, String timeUUID) {}
    
    @Pointcut("execution(void tr.com.eno..ApplicationService+.activateDeployment(..)) && args(deployId, domain, applicationName, timeUUID)")
    public void activateDeploymentPointcut(Integer deployId, String domain, String applicationName, String timeUUID) {}

    @Around("deployApplicationPointcut(application,userId)")
    public Integer deployApplicationAdvice(ProceedingJoinPoint joinPoint, Application application, int userId) throws IntrospectionException, Throwable {

        Class declaringType = joinPoint.getSignature().getDeclaringType();

        Logger logger = LoggerFactory.getLogger(declaringType.getName());

        BeanInfo beanInfo = Introspector.getBeanInfo(declaringType);

        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

        EventAdmin eventAdmin = null;

        for (PropertyDescriptor descriptor : propertyDescriptors) {

            if (EventAdmin.class.isAssignableFrom(descriptor.getPropertyType())) {

                Method readMethod = descriptor.getReadMethod();

                eventAdmin = (EventAdmin) readMethod.invoke(joinPoint.getTarget());

                break;
            }
        }

        if (eventAdmin == null) {

            logger.error("EventAdmin reference is needed in ApplicationService implementations.");

            throw new RuntimeException("EventAdmin reference is missing in ApplicationService implementation.");
        }

        try {

            Integer deployId = (Integer) joinPoint.proceed(new Object[]{application,userId});

            logger.debug("Firing event for the deployment of application '{}' of domain '{}'...", application.getName(), application.getDomain());

            Map<String, Object> eventParams = new HashMap<>();
            eventParams.put("application", application);
            eventParams.put("userId", userId);
            eventParams.put("deployId", deployId);
            Event event = new Event(ApplicationService.APPLICATION_DEPLOYED_EVENT_TOPIC, eventParams);

            eventAdmin.sendEvent(event);
            
            return deployId;

        } catch (Throwable ex) {

            logger.error("Failed to deploy application '{}' of domain '{}'.", application.getName(), application.getDomain());

            logger.error(ex.getMessage(), ex);

            throw new ApplicationDeploymentFailedException();
        }
    }

    @Around("deleteDeploymentPointcut(deployId, domain, applicationName, timeUUID)")
    public void deleteDeploymentAdvice(final ProceedingJoinPoint joinPoint, Integer deployId, String domain, String applicationName, String timeUUID) throws IntrospectionException, Throwable {
        
        Class declaringType = joinPoint.getSignature().getDeclaringType();
        final Logger logger = LoggerFactory.getLogger(declaringType);
        logger.debug("Calling deleteDeploymentAdvice for application '{}' of domain '{}' at the time '{}'", applicationName, domain, timeUUID);
        
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(declaringType).getPropertyDescriptors();

        EventAdmin eventAdmin = null;

        for (PropertyDescriptor descriptor : propertyDescriptors) {

            if (EventAdmin.class.isAssignableFrom(descriptor.getPropertyType())) {

                Method readMethod = descriptor.getReadMethod();

                eventAdmin = (EventAdmin) readMethod.invoke(joinPoint.getTarget());

                break;
            }
        }

        if (eventAdmin == null) {

            logger.error("EventAdmin reference is needed in ApplicationService implementations.");

            throw new RuntimeException("EventAdmin reference is missing in ApplicationService implementation.");
        }
        
        logger.debug("Calling eventAdmin from '{}'", joinPoint.getTarget());
        
        // we can pass parameters as an object array - you must get the correct number of 
        // parameters as the target is expecting, or you get a runtime exception.
        joinPoint.proceed(joinPoint.getArgs());
        
        try {

            logger.debug("Firing event for deleting the deployment of application '{}' of domain '{}' at time '{}'...", applicationName, domain, timeUUID);

            Map<String, Object> eventParams = new HashMap<>();
            eventParams.put("deployId", deployId);
            eventParams.put("appName", applicationName);
            Event event = new Event(ApplicationService.APPLICATION_DELETE_DEPLOYMENT_EVENT_TOPIC, eventParams);

            eventAdmin.sendEvent(event);

        } catch (Throwable ex) {

            logger.error("Failed to delete the deployment for application '{}' of domain '{}'.", applicationName, domain);

            logger.error(ex.getMessage(), ex);

            throw new ApplicationDeploymentFailedException();
        }
        
    }
    
    @Around("activateDeploymentPointcut(deployId, domain, applicationName, timeUUID)")
    public void activateDeploymentAdvice(final ProceedingJoinPoint joinPoint, Integer deployId, String domain, String applicationName, String timeUUID) throws IntrospectionException, Throwable {
        
        Class declaringType = joinPoint.getSignature().getDeclaringType();
        final Logger logger = LoggerFactory.getLogger(declaringType);
        logger.debug("Calling activateDeploymentAdvice for application '{}' of domain '{}' at the time '{}'", applicationName, domain, timeUUID);
        
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(declaringType).getPropertyDescriptors();

        EventAdmin eventAdmin = null;

        for (PropertyDescriptor descriptor : propertyDescriptors) {

            if (EventAdmin.class.isAssignableFrom(descriptor.getPropertyType())) {

                Method readMethod = descriptor.getReadMethod();

                eventAdmin = (EventAdmin) readMethod.invoke(joinPoint.getTarget());

                break;
            }
        }

        if (eventAdmin == null) {

            logger.error("EventAdmin reference is needed in ApplicationService implementations.");

            throw new RuntimeException("EventAdmin reference is missing in ApplicationService implementation.");
        }
        
        logger.debug("Calling eventAdmin from '{}'", joinPoint.getTarget());
        
        // we can pass parameters as an object array - you must get the correct number of 
        // parameters as the target is expecting, or you get a runtime exception.
        joinPoint.proceed(joinPoint.getArgs());
        
        try {

            logger.debug("Firing event to activate the deployment of application '{}' of domain '{}' at time '{}'...", applicationName, domain, timeUUID);

            Map<String, Object> eventParams = new HashMap<>();
            eventParams.put("deployId", deployId);
            eventParams.put("appName", applicationName);
            Event event = new Event(ApplicationService.APPLICATION_ACTIVATE_DEPLOYMENT_EVENT_TOPIC, eventParams);

            eventAdmin.sendEvent(event);

        } catch (Throwable ex) {

            logger.error("Failed to activate the deployment for application '{}' of domain '{}'.", applicationName, domain);

            logger.error(ex.getMessage(), ex);

            throw new ApplicationDeploymentFailedException();
        }
        
    }
    
}
