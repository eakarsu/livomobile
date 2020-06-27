package tr.com.eno.livo.server.web.controller;

import com.livo.companies.Companies;
import com.livo.companies.CompaniesService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import tr.com.eno.livo.server.application.Application;
import tr.com.eno.livo.server.application.ApplicationService;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;
import tr.com.eno.livo.server.users.UserQueryService;
//import tr.com.eno.livo.server.notification.AndroidNotification;
//import tr.com.eno.livo.server.notification.NotificationPushService;
//import tr.com.eno.livo.server.notification.Notifications;
import tr.com.eno.livo.server.web.ManagementHelper;
import tr.com.eno.livo.server.web.WebUser;
import tr.com.eno.livo.server.web.WebUserManager;

@Controller
public class HomeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HomeController.class);
    private final String USER_ICONS_PROP_FILE = "userIcons.properties";
    
    /*Comparator for sorting the list by group*/
    public Comparator<Map> groupNameComparator = new Comparator<Map>() {

        public int compare(Map s1, Map s2) {
            String groupName1 = s1.get("name").toString();
            String groupName2 = s2.get("name").toString();

            //ascending order
            return groupName1.compareTo(groupName2);

        }
    };

    /**
     * 
     * executed after the login process, and
     * gets and sends the several necessary information:
     * 1- the current user
     * 2- checking whether the user is admin or not?
     * 3- the group list of the clients users
     * 4- the application list of the current user or all applications if th user is Administrator
     * 5- the source path of the current user's icon
     * 6- the company list
     * 7- the web user(Developer) list
     * 
     * @param modelMap holds the whole information to send them to jsp
     * @return
     * @throws InstanceNotFoundException
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String show(ModelMap modelMap) throws InstanceNotFoundException, MalformedObjectNameException, IOException {
        // Get the AEON home environment variable       
        String homePath = System.getenv("AEON_HOME");
        
        ApplicationService service = ManagementHelper.getApplicationService();

        UserQueryService userQueryService = ManagementHelper.getUserQueryService();

        ArrayList<Map> groupList = new ArrayList<>();
        Set<UserGroup> groups = userQueryService.listGroups();
        for (UserGroup group : groups) {
            Map<String, String>  groupMap = new HashMap();
            groupMap.put("name", group.getName());
            groupList.add(groupMap);
        }
        Collections.sort(groupList, groupNameComparator);
        
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        WebUser user = (WebUser) authentication.getPrincipal();

//      Set<Application> applications = service.listApplications("", SecurityUtils.getSubject().getPrincipal().toString());
        
        Set<Application> applications = service.listApplications(Application.DEFAULT_DOMAIN);
        List<Application> list = new ArrayList<>();

        for (Application application : applications) {
            if(application.getOwnerId() == user.getUserId() && !user.getUserName().equals("Administrator"))
                list.add(application);
            else
            if(user.getUserName().equals("Administrator"))
                list.add(application);
        }

        Collections.sort(list, new Comparator<Application>() {

            public int compare(Application s1, Application s2) {
                String appName1 = s1.getName().toUpperCase();
                String appName2 = s2.getName().toUpperCase();

                //ascending order
                return appName1.compareTo(appName2);

            }
        });
        
        System.out.println("HomeController.show() -- > " + applications.size());

        File userIconsPropertiesFile = new File(homePath + File.separator + USER_ICONS_PROP_FILE);

        String userIconSrc = getProperties(userIconsPropertiesFile, user.getUserName());

        boolean checkAdmin = true;
        
        try {
            //user role Checking..
            SecurityUtils.getSubject().checkRole("Developer");
            //User role is developer 
            checkAdmin = false;
        } catch (Exception ex) {
            LOGGER.error("User role is not Developer.", ex.getMessage());
            //User Role is administrator
            checkAdmin = true;
        }
        
        WebUserManager manager = new WebUserManager(user);
        List<WebUser> users = manager.getWebUsers(null);
        List<Integer> userCompanies = manager.getCompanies4CurrentUser();
        LOGGER.debug("userCompanies size: " + userCompanies.size());
        
        ArrayList<Map> companyList = new ArrayList<>();
        CompaniesService companiesService = ManagementHelper.getCompaniesService();
        Set<Companies> companies = companiesService.listCompanies();
        boolean companyExists = false;
        for (Companies company : companies) {
            
            if(company.getCompanyId().equals("LivoAny") || company.getCompanyId().equals("LivoUsers"))
                    continue;
            
            for (Integer compId : userCompanies) if(company.getRecordId().equals(compId)) companyExists = true;
            
            if(companyExists || user.getUserName().equals("Administrator")){
                
                Map<String, String>  companyMap = new HashMap();
                companyMap.put("recordId", company.getRecordId().toString());
                companyMap.put("companyId", company.getCompanyId());

                companyList.add(companyMap);
            }
            companyExists = false;
            
        }
        Collections.sort(companyList, compNameComparator);
        
        LOGGER.debug("Companies size: " + companyList.size());
        
        modelMap.put("user", user);
        modelMap.put("checkAdmin", checkAdmin);
        modelMap.put("groups", groupList);
        modelMap.put("applications", list);
        modelMap.put("authentication", authentication);
        modelMap.put("userIconSrc", userIconSrc);
        modelMap.put("companies", companyList);
        modelMap.put("webusers", users);
        
//        NotificationPushService pushService = ManagementHelper.getNotificationPushService();
//        
//        Notifications nots = new Notifications();
//        
//        AndroidNotification anot = new AndroidNotification();
//        
//        anot.setCollapseKey("message");
//        
//        anot.addData("message", "Livo the Hope.");
//        
//        nots.setGcmNotification(anot);
//        
//        pushService.push("test", nots);

        return "home";
    }

    @RequestMapping(value = "/welcome", method = RequestMethod.POST)
    public String welcome(ModelMap modelMap) {

        return "body.welcome";
    }

    /**
     * 
     * gets the value of the defined key from the properties file
     * 
     * @param propertiesFile
     * @param key
     * @return
     * @throws IOException 
     */
    private String getProperties(File propertiesFile, String key) throws IOException {

        Properties prop = new Properties();
        prop.load(new FileInputStream(propertiesFile));

        if (prop.get(key) != null) {
            return String.valueOf(prop.get(key));
        } else {
            return String.valueOf(prop.get("default"));
        }
    }
    
    public Comparator<Map> compNameComparator = new Comparator<Map>() {

        public int compare(Map s1, Map s2) {
            String compName1 = s1.get("companyId").toString().toUpperCase();
            String compName2 = s2.get("companyId").toString().toUpperCase();

            //ascending order
            return compName1.compareTo(compName2);

        }
    };
    
}
