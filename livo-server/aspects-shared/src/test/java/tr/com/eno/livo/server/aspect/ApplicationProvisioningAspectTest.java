package tr.com.eno.livo.server.aspect;

import java.util.HashMap;
import static org.testng.Assert.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.Callable;
import org.apache.commons.lang3.RandomStringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.testng.annotations.Test;
import tr.com.eno.livo.server.application.Application;
import tr.com.eno.livo.server.application.ApplicationAlreadyExistsException;
import tr.com.eno.livo.server.application.ApplicationDeploymentFailedException;
import tr.com.eno.livo.server.application.ApplicationNotFoundException;
import tr.com.eno.livo.server.application.ApplicationService;
import tr.com.eno.livo.server.application.AuthorizationPolicy;
import tr.com.eno.livo.server.application.Companies;
import tr.com.eno.livo.server.application.DeploymentNotFoundException;
import tr.com.eno.livo.server.application.Deployments;
import tr.com.eno.livo.server.application.IconsSplashes;
import tr.com.eno.livo.server.application.InvalidApplicationException;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;

/**
 *
 * @author dacay
 */
@Test(groups = "application")
public class ApplicationProvisioningAspectTest {

    private static class ApplicationServiceImpl implements ApplicationService {

        private final EventAdmin eventAdmin;
        private final Callable deployCallable;

        public ApplicationServiceImpl(EventAdmin eventAdmin, Callable deployCallable) {
            this.eventAdmin = eventAdmin;
            this.deployCallable = deployCallable;
        }

        @Override
        public void createApplication(Application application) throws ApplicationAlreadyExistsException, InvalidApplicationException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deleteApplication(String domain, String applicationName) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Integer deployApplication(Application application,int userId) throws ApplicationNotFoundException, ApplicationDeploymentFailedException {

            try {
                
                deployCallable.call();
                
            } catch (Exception ex) {
                
                if (ex instanceof ApplicationNotFoundException) {
                    
                    throw (ApplicationNotFoundException) ex;
                    
                } else if (ex instanceof ApplicationDeploymentFailedException) {
                    
                    throw (ApplicationDeploymentFailedException) ex;
                    
                } else {
                    
                    throw new RuntimeException(ex);
                }
            }
            
            return -2;
        }

        @Override
        public List<Deployments> getAppHistory(String domain, String name, int limit) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public SortedSet<Application> listApplications(String domain) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public Application loadApplication(String domain, String name) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void saveApplication(Application application) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void authorizeUsers(String domain, String name, User... users) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deauthorizeUsers(String domain, String name, User... users) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void authorizeGroups(String domain, String name, UserGroup... groups) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deauthorizeGroups(String domain, String name, UserGroup... groups) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void setAuthorizationPolicy(String domain, String name, AuthorizationPolicy policy) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    
        public EventAdmin getEventAdmin() {
            return eventAdmin;
        }

        @Override
        public void authorizeCompanies(String domain, String name, Companies... companies) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public SortedSet<Companies> listAppCompanies(String appName) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void updateOwner(Application currentApp, Integer userId) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<IconsSplashes> listIconsSplashes4App(String appName) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void saveIconsSplashes4App(String appName, String imgId, String platform, String fileName, Integer userId) throws ApplicationNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public List<Deployments> getAllDeploymentHistory(String domain) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void deleteDeployment(Integer deployId, String domain, String applicationName, String timeUUID) throws DeploymentNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void activateDeployment(Integer deployId, String domain, String applicationName, String timeUUID) throws DeploymentNotFoundException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
