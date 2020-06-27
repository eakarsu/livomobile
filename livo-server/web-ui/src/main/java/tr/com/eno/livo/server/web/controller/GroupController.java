package tr.com.eno.livo.server.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import tr.com.eno.livo.server.users.GroupCompanies;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserGroup;
import tr.com.eno.livo.server.users.UserManagementService;
import tr.com.eno.livo.server.users.UserQueryService;
import tr.com.eno.livo.server.web.ManagementHelper;
import tr.com.eno.livo.server.web.WebUser;
import tr.com.eno.livo.server.web.WebUserManager;

@Controller
public class GroupController {

    //TODO this user value will be from login information, login service must be prepared. Change 'USER' variable in every method.
    private static final Logger LOGGER = LoggerFactory.getLogger(GroupController.class);

    public static final String DOMAIN = "System";

    /**
     * 
     * gets the whole groups of companies authorized by the current web user(Developer), and 
     * separately gets the companies of every returned group
     * 
     * @param map holds the group list and group company list, and sends them to jsp
     * @return "groups"
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/groupTable", method = RequestMethod.POST)
    public String showGroups(ModelMap map) throws MalformedObjectNameException, IOException {
        LOGGER.debug("showGroups() is started.");
        try {
            
            //Get current authentication.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //Get current user.
            WebUser user = (WebUser) auth.getPrincipal();

            WebUserManager manager = new WebUserManager(user);
            List<Integer> userCompanies = manager.getCompanies4CurrentUser();
            
            //Get the connection to user query service.
            UserQueryService service = ManagementHelper.getUserQueryService();
            //Get group list from connection.
            Set<UserGroup> groups = service.listGroups();
            Set<GroupCompanies> groupCompanies = service.listGroupCompanies(groups);
            List<Map> groupCompanyMap = new ArrayList<>();
            Map<String, String>  companyMap = new HashMap();
            boolean companyExists = false;
            int gLen = groups.size() * groupCompanies.size();
            String[] removedGroups = new String[gLen];
            for (GroupCompanies groupCompany : groupCompanies) {
                if(!user.getUserName().equals("Administrator")){
                    for (Integer compId : userCompanies) if(groupCompany.getCompanyId().equals(compId)) companyExists = true;
                    if(!companyExists){
                        for(int i = 0; i < gLen; i++) 
                            if(removedGroups[i] == null){
                                removedGroups[i] = groupCompany.getGroupName();
                                LOGGER.debug("removedGroups add: " + groupCompany.getGroupName() + " -- " + i);
                                break;
                            }
                        continue;
                    }
                    for(int i = 0; i < gLen; i++) 
                        if(groupCompany.getGroupName().equals(removedGroups[i])){
                            LOGGER.debug("removedGroups minus: " + removedGroups[i] + " -- " + i);
                            removedGroups[i] = null;
                        }
                    companyExists = false;
                }
                if(companyMap.get(groupCompany.getGroupName()) == null)
                    companyMap.put(groupCompany.getGroupName(), groupCompany.getCompanyId().toString());
                else
                    companyMap.put(groupCompany.getGroupName(), companyMap.get(groupCompany.getGroupName()) + "," + groupCompany.getCompanyId().toString());
            }
            for (Map.Entry<String, String> entry : companyMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Map<String, String>  companyMap2 = new HashMap();
                companyMap2.put("groupName", key);
                companyMap2.put("group_companies", value);
                groupCompanyMap.add(companyMap2);
            }
            LOGGER.debug("removedGroups count: " + removedGroups.length);
            if(!user.getUserName().equals("Administrator")){
                for(int i = 0; i < gLen; i++)
                    for (UserGroup group : groups){
                        LOGGER.debug("remove groups: " + removedGroups[i]  + " == " + group.getName());
                        if(removedGroups[i] != null && group.getName().equals(removedGroups[i])){
                            groups.remove(group);
                            break;
                        }
                    }
                        
            }
            
//            Set<UserGroup> groups = new HashSet<UserGroup>();
//            UserGroup userGroup = new UserGroup("group-1", "System", null);
//            groups.add(userGroup);
//
//            userGroup = new UserGroup("group-2", "System", null);
//            groups.add(userGroup);
//          TODO put current WebUser to WebUserManager object below.
//          users.addAll(new WebUserManager(user).getWebUsers());
            LOGGER.debug("Group count: " + groups.size() + " --- group companies: " + groupCompanies.size());
//          Put them into modelmap
            map.put("groups", groups);
            map.put("group_companies", groupCompanyMap);

            return "groups";

        } catch (Exception e) {
            LOGGER.error("Exception has been occured in showGroups(): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * creates new web user group and new group companies  using the posted data
     * 
     * @param groupName
     * @param groupDescription
     * @param groupCompanyList
     * @param response
     * @return 
     */
    @RequestMapping(value = "/createNewGroup", method = RequestMethod.POST)
    @ResponseBody
    public String createNewGroup(@RequestParam("groupName") String groupName, @RequestParam("groupDescription") String groupDescription, @RequestParam("groupCompanyList") String groupCompanyList,HttpServletResponse response) {
        LOGGER.debug("createNewGroup() is started with: '{}'" + " groupName : " + groupName + " -- groupDescription : " + groupDescription + " -- groupCompanyList : " + groupCompanyList);

        //Get the connection to user query service.
        UserManagementService service;
        Set<User> users = new HashSet<User>();
        try {
            
            if(groupCompanyList.indexOf("null") != -1)
                return "Failed: company list is null";
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();
            
            service = ManagementHelper.getUserManagementService();
            UserGroup userGroup = new UserGroup(groupName, DOMAIN, groupDescription, users);
            //create group list from connection.
            service.createGroup(userGroup);
            
            String[] groupCompanyArr = groupCompanyList.split(",");
            List<GroupCompanies> groupCompanies = new ArrayList<>();
            for (String company : groupCompanyArr) {
                GroupCompanies groupCompany = new GroupCompanies(groupName, Integer.parseInt(company), user.getUserId(), new Date().getTime());
                groupCompanies.add(groupCompany);
            }
            
            service.createGroupCompanies(groupCompanies);

        } catch (Exception ex) {
            LOGGER.error("New group cannot be created. " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
        }
        LOGGER.debug("Group succesfully created.", groupName);

        return "Succesfully created.";

    }

    /**
     * 
     * updates new user group and new group companies using the posted data
     * 
     * @param groupName
     * @param newGroupName
     * @param newGroupDescription
     * @param groupCompanyList
     * @param response
     * @return 
     */
    @RequestMapping(value = "/updateGroup", method = RequestMethod.POST)
    @ResponseBody
    public String updateGroup(@RequestParam("groupName") String groupName, @RequestParam("newGroupName") String newGroupName, @RequestParam("newGroupDescription") String newGroupDescription, @RequestParam("groupCompanyList") String groupCompanyList, HttpServletResponse response) {
        LOGGER.debug("updateGroup() is started with: '{}'" + " groupName : " + groupName + " newGroupName : " + newGroupName + " newGroupDescription : " + newGroupDescription + " -- groupCompanyList : " + groupCompanyList);
        boolean groupNameUpdated = false;
        //Get the connection to user query service.
        UserManagementService service;
        UserQueryService qService;
    
        try {
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();
            
            service = ManagementHelper.getUserManagementService();
            qService = ManagementHelper.getUserQueryService();
            UserGroup userGroup = qService.findGroup(groupName, DOMAIN);

            /*if (userGroup.getName().equalsIgnoreCase(newGroupName) && userGroup.getDescription().equalsIgnoreCase(newGroupDescription)) {

                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

                return "Failed: At least one modification is required.";
            }*/

            if (!userGroup.getName().equalsIgnoreCase(newGroupName)) {

                service.updateGroupName(userGroup, newGroupName);
                groupNameUpdated = true;
            }

            if (!userGroup.getDescription().equalsIgnoreCase(newGroupDescription)) {
                if (groupNameUpdated) {
                    UserGroup group = qService.findGroup(newGroupName, DOMAIN);
                    UserGroup newUserGroup = new UserGroup(group.getName(), group.getDomain(), newGroupDescription, group.getUsers());
                    service.updateGroup(newUserGroup);

                } else {
                    UserGroup group = new UserGroup(userGroup.getName(), userGroup.getDomain(), newGroupDescription, userGroup.getUsers());
                    service.updateGroup(group);
                }

            }
            
            String[] groupCompanyArr = groupCompanyList.split(",");
            List<GroupCompanies> groupCompanies = new ArrayList<>();
            for (String company : groupCompanyArr) {
                GroupCompanies groupCompany = new GroupCompanies(newGroupName, Integer.parseInt(company), user.getUserId(), new Date().getTime());
                groupCompanies.add(groupCompany);
            }
            
            service.updateGroupCompanies(groupName, groupCompanies);
 
        } catch (Exception ex) {
            LOGGER.error("User group cannot be updated. " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
        }
        LOGGER.debug("Group succesfully updated.", newGroupName);

        return "Succesfully updated.";

    }
    
    /**
     * 
     * if the selected group has at least one user, firstly deletes all of the user(s).
     * lastly deletes the selected group.
     * 
     * @param groupName
     * @param response
     * @return 
     */
    @RequestMapping(value = "/deleteGroup", method = RequestMethod.POST)
    @ResponseBody
    public String deleteGroup(@RequestParam("groupName") String groupName , HttpServletResponse response) {
        LOGGER.debug("deleteGroup() is started with: '{}'" + " groupName : " + groupName );
       
        //Get the connection to user query service.
        UserManagementService service;
        UserQueryService qService;
    
        try {
            service = ManagementHelper.getUserManagementService();
            qService = ManagementHelper.getUserQueryService();
           
            UserGroup userGroup = qService.findGroup(groupName, DOMAIN);
            Set<User> users = userGroup.getUsers();

            //if the user group has users, then firstly delete all of them
            if(users.size() != 0){
                
                Iterator iter = users.iterator();
                while (iter.hasNext()) {
                    service.deleteUser((User) iter.next());
                }
                
            }
            service.deleteGroup(userGroup);
 
        } catch (Exception ex) {
            LOGGER.error("User group cannot be deleted. " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
        }
        LOGGER.debug("Group succesfully deleted.", groupName);

        return "Succesfully deleted.";

    }

}
