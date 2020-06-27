package tr.com.eno.livo.server.web.controller;

import com.livo.companies.Companies;
import com.livo.companies.CompaniesService;
import com.livo.companies.CompanyAlreadyExistsException;
import com.livo.companies.CompanyNotFoundException;
import com.livo.companies.InvalidCompanyException;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
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
import org.springframework.web.multipart.MultipartFile;
import tr.com.eno.livo.server.authc.DeviceAuthenticationService;
import com.livo.devices.DevicesService;
import com.livo.devices.MobileDevices;
import java.text.SimpleDateFormat;
import java.util.Map;
import tr.com.eno.livo.server.mail.MailProperties;
import tr.com.eno.livo.server.mail.MailService;
import tr.com.eno.livo.server.users.InvalidPasswordException;
import tr.com.eno.livo.server.web.LivoSession;
import tr.com.eno.livo.server.web.ManagementHelper;
import tr.com.eno.livo.server.users.User;
import tr.com.eno.livo.server.users.UserAlreadyExistsException;
import tr.com.eno.livo.server.users.UserGroup;
import tr.com.eno.livo.server.users.UserGroupNotFoundException;
import tr.com.eno.livo.server.users.UserManagementService;
import tr.com.eno.livo.server.users.UserNotFoundException;
import tr.com.eno.livo.server.users.UserQueryService;
import tr.com.eno.livo.server.web.SecurityHelper;
import tr.com.eno.livo.server.web.UserImportManager;
import tr.com.eno.livo.server.web.WebUser;
import tr.com.eno.livo.server.web.WebUserManager;

@Controller
public class UserController {

    //TODO this user value will be from login information, login service must be prepared. Change 'USER' variable in every method.
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private String MAIL_SENDER_NAME = "Livo Mobile";
    private String MAIL_CREATE_USER_SUBJECT = "Your mobile client account has been created!";
    private String MAIL_USER_PASSWORD_CHANGE_SUBJECT = "Mobile client password change";
    private final static String MAIL_CONFIG_FILE = "mailConfig.properties";

    private String homePath = System.getenv("AEON_HOME");

    public static final String DOMAIN = "System";

    /**
     * 
     * gets the current user and the LIVO session, and later sets the user to the LIVO session
     * 
     * @param response the server status
     */
    @RequestMapping(value = "/heartbeater", method = RequestMethod.POST)
    public void heartBeat(HttpServletResponse response) {

        WebUser user = (WebUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LivoSession session = new LivoSession();

        session.setWebUser(user);

        LOGGER.debug("Heart beated..." + new Date().toString());

        response.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

    /**
     * 
     * gets the client users and groups, and later sends to jsp
     * 
     * @param map holds the client users and groups, and sends to jsp
     * @return "users"
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/userTable", method = RequestMethod.POST)
    public String showUsers(ModelMap map) throws MalformedObjectNameException, IOException {
        LOGGER.debug("showUsers() is started.");
        try {
            //Get the connection to user management service.
            UserQueryService service = ManagementHelper.getUserQueryService();
            //Get users from connection.
            Set<User> users = service.listUsers();

            //Get group list
            Set<UserGroup> groups = service.listGroups();

//          TODO put current WebUser to WebUserManager object below.
//          users.addAll(new WebUserManager(user).getWebUsers());
            LOGGER.debug("Users count : " + users.size());
            LOGGER.debug("Groups count : " + groups.size());
//          Put them into modelmap
            map.put("users", users);
            map.put("groups", groups);

            return "users";

        } catch (Exception e) {
            LOGGER.error("Exception has been occured in showUsers(): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 
     * gets the web users(Developers) and their company list, and later sends them to jsp
     * 
     * @param map holds the web users and their company list, and sends them to jsp
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/webUserTable", method = RequestMethod.POST)
    public String showWebUsers(ModelMap map) throws MalformedObjectNameException, IOException {
        LOGGER.debug("showWebUsers() is started.");
        try {
            //Get current authentication.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            //Get current user.
            WebUser user = (WebUser) auth.getPrincipal();

            //Get the service manager to web user manager.
            WebUserManager manager = new WebUserManager(user);

            List<WebUser> users = manager.getWebUsers(null);
            //TODO put current WebUser to WebUserManager object below.
            LOGGER.debug("Users count : " + users.size());
            
            List<Map> companyList = manager.getWebUserCompanies(users);
            LOGGER.debug("showWebUsers ----------------> " + users.size() + " --- " + companyList.size());
            
            //Put them into modelmap
            map.put("users", users);
            map.put("user_companies", companyList);

            return "webusers";

        } catch (Exception e) {
            LOGGER.error("Exception has been occured in showWebUsers(): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * gets the whole companies, and sends them to jsp
     * 
     * @param map holds the companies and sends them to jsp
     * @return "companies"
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/cCompanies", method = RequestMethod.POST)
    public String showCompanies(ModelMap map) throws MalformedObjectNameException, IOException {
        LOGGER.debug("showCompanies() is started.");
        try {
            //Get current authentication.
            CompaniesService companiesService = ManagementHelper.getCompaniesService();
            
            Set<Companies> companies = companiesService.listCompanies();
            
            List<Companies> companyList = new ArrayList<>();
            for (Companies company : companies) {
                Date date = new Date(company.getCreateDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                company.setFormatedDate(sdf.format(date));
                companyList.add(company);
            }
            
            //TODO put current WebUser to WebUserManager object below.
            LOGGER.debug("Company count : " + companyList.size());
            //Put them into modelmap
            map.put("companies", companyList);

            return "companies";

        } catch (Exception e) {
            LOGGER.error("Exception has been occured in showWebUsers(): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * gets the whole devices, and sends them to jsp
     * 
     * @param map holds the devices and sends them to jsp
     * @return "devices"
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/mDevices", method = RequestMethod.POST)
    public String showMobileDevices(ModelMap map) throws MalformedObjectNameException, IOException {
        LOGGER.debug("showMobileDevices is started.");
        try {
            //Get current authentication.
            DevicesService deviceService = ManagementHelper.getDevicesService();

            Set<MobileDevices> devices = deviceService.listMobileDevices();
            
            List<MobileDevices> deviceList = new ArrayList<>();
            for (MobileDevices mobileDevice : devices) {
                deviceList.add(mobileDevice);
            }

            //TODO put current WebUser to WebUserManager object below.
            LOGGER.debug("Device count : " + deviceList.size());
            //Put them into modelmap
            map.put("devices", deviceList);

            return "devices";

        } catch (Exception e) {
            LOGGER.error("Exception has been occured in showMobileDevices(): " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 
     * creates a company using posted data
     * 
     * @param companyId the defined company id
     * @param companyTitle
     * @param loginPermSwitch
     * @param companyPassword
     * @param response
     * @return 
     */
    @RequestMapping(value = "/createNewCompany", method = RequestMethod.POST)
    @ResponseBody
    public String createNewCompany(@RequestParam("companyId") String companyId, @RequestParam("companyTitle") String companyTitle, 
                                   @RequestParam("loginPermSwitch") Boolean loginPermSwitch, @RequestParam("companyPassword") String companyPassword, HttpServletResponse response) {
        LOGGER.debug("createNewCompany() is started with: '{}'" + " companyId : " + companyId + " companyTitle : " + companyTitle + " companyPassword : ******");
        WebUser currentUser = null;
        CompaniesService companiesService = null;
        
        try {
            
            companiesService = ManagementHelper.getCompaniesService();
            
            currentUser = SecurityHelper.getCurrentUser();
        
            Companies company = new Companies(null, companyId, companyTitle, currentUser.getUserId(), new Date().getTime(), loginPermSwitch);
            companiesService.createCompany(company, companyPassword);
        
        } catch (IOException ex) {
            LOGGER.error("New company cannot be created. " + ex.getMessage(), ex);
        } catch (InstanceNotFoundException ex) {
            LOGGER.error("New company cannot be created. " + ex.getMessage(), ex);
        } catch (MalformedObjectNameException ex) {
            LOGGER.error("New company cannot be created. " + ex.getMessage(), ex);
        } catch (InvalidCompanyException ex) {
            LOGGER.error("New company cannot be created. " + ex.getMessage(), ex);
        } catch (com.livo.companies.InvalidPasswordException ex) {
            LOGGER.error("New company cannot be created. " + ex.getMessage(), ex);
        } catch (CompanyAlreadyExistsException ex) {
            LOGGER.error("New company cannot be created. " + ex.getMessage(), ex);
        }
    
        return "Succesfully created.";
        
    }
    
    @RequestMapping(value = "/deleteCompany", method = RequestMethod.POST)
    @ResponseBody
    public String deleteCompany(@RequestParam("recordId") Integer recordId, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Delete company with: '{}'", " recordId : " + recordId);
    
        CompaniesService companiesService = null;
        
        try {
            
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            WebUser user = (WebUser) authentication.getPrincipal();
            //Get the service manager to web user manager.
            WebUserManager manager = new WebUserManager(user);

            List<Integer> userIdList = manager.getWebUsers4Company(recordId);
            
            companiesService = ManagementHelper.getCompaniesService();
        
            if(userIdList.size() == 0)
                companiesService.deleteCompany(recordId);
            else
                return "The company wanted to delete is used.";
        
        } catch (IOException ex) {
            LOGGER.error("The Company cannot be deleted. " + ex.getMessage(), ex);
        } catch (InstanceNotFoundException ex) {
            LOGGER.error("The Company cannot be deleted. " + ex.getMessage(), ex);
        } catch (MalformedObjectNameException ex) {
            LOGGER.error("The Company cannot be deleted. " + ex.getMessage(), ex);
        }
        
        return "Succesfully deleted.";
    
    }
    
    /**
     * 
     * updates the company information
     * 
     * @param recordId the record id of the current company id
     * @param companyId
     * @param companyTitle
     * @param loginPermSwitch
     * @param companyPassword
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException
     * @throws CompanyNotFoundException 
     */
    @RequestMapping(value = "/updateCompany", method = RequestMethod.POST)
    @ResponseBody
    public String updateCompany(@RequestParam("recordId") Integer recordId, 
                                @RequestParam("companyId") String companyId, 
                                @RequestParam("companyTitle") String companyTitle,
                                @RequestParam("loginPermSwitch") Boolean loginPermSwitch,
                                @RequestParam("companyPassword") String companyPassword,
                                HttpServletResponse response) throws MalformedObjectNameException, IOException, CompanyNotFoundException {

        LOGGER.debug("Update company with: '{}'", " recordId: " + recordId + " -- " + loginPermSwitch);
    
        CompaniesService companiesService = null;
        WebUser currentUser = null;
        
        try {
            
            companiesService = ManagementHelper.getCompaniesService();
        
            currentUser = SecurityHelper.getCurrentUser();
        
            Companies company = new Companies(recordId, companyId, companyTitle, currentUser.getUserId(), new Date().getTime(), loginPermSwitch);
            companiesService.updateCompanyPassword(company, companyPassword);
        
        } catch (IOException ex) {
            LOGGER.error("Company cannot be updated. " + ex.getMessage(), ex);
        } catch (InstanceNotFoundException ex) {
            LOGGER.error("Company cannot be updated. " + ex.getMessage(), ex);
        } catch (MalformedObjectNameException ex) {
            LOGGER.error("Company cannot be updated. " + ex.getMessage(), ex);
        } catch (com.livo.companies.InvalidPasswordException ex) {
            LOGGER.error("Company cannot be updated. " + ex.getMessage(), ex);
        }
        
        return "Succesfully updated.";
    
    }

    /**
     * 
     * creates a new user using the posted data, 
     * adds the user to the user group if it selects,
     * sends an email to the user if the email configuration is done in the general settings.
     * 
     * @param userName
     * @param userMail
     * @param userGroupName
     * @param userPassword
     * @param response
     * @return 
     */
    @RequestMapping(value = "/createnewuser", method = RequestMethod.POST)
    @ResponseBody
    public String createNewUser(@RequestParam("userName") String userName, @RequestParam("userMail") String userMail, @RequestParam("userGroupName") String userGroupName, @RequestParam("userPassword") String userPassword, HttpServletResponse response) {
        LOGGER.debug("createNewUser() is started with: '{}'" + " userName : " + userName + " UserMail : " + userMail + " UserPassword : ******");

        boolean mailConfigured = true;
        boolean addGroup = false;
        if (!userGroupName.equals("0")) {
            addGroup = true;
            LOGGER.debug("User adding to " + userGroupName + " group.");
        }
        try {
            UserQueryService qService = ManagementHelper.getUserQueryService();

            UserManagementService mService = ManagementHelper.getUserManagementService();
            User newUser = new User(null, userName, DOMAIN, userMail, userGroupName, "First Name", "Last Name", true);
            mService.createUser(newUser, userPassword);

            // Find User Group
            UserGroup userGroup = qService.findGroup(userGroupName, DOMAIN);
            LOGGER.debug("userGroup size : " + userGroup.getUsers().size());
            // Add new user to User Group List
            Set<User> users = userGroup.getUsers();

            Set<User> newUsers = new HashSet<User>();

            if (users.size() < 1) {
                newUsers.add(newUser);
            } else {

                Iterator iter = users.iterator();
                while (iter.hasNext()) {
                    newUsers.add((User) iter.next());
                }
                newUsers.add(newUser);
            }
            UserGroup newUserGroup = new UserGroup(userGroup.getName(), userGroup.getDomain(), userGroup.getDescription(), newUsers);

            //Update user group 
            mService.updateGroup(newUserGroup);

            MailService mailService = ManagementHelper.getMailService();
            try {
                MailProperties mailProps = mailService.getMailProperties();
                if (mailProps.getHostName().isEmpty() || mailProps.getPort().isEmpty() || mailProps.getFrom().isEmpty()) {
                    mailConfigured = false;
                }
            } catch (Exception ex) {
                mailConfigured = false;
            }

            if (!mailConfigured) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return "New user created. Smtp settings is not configured yet.";
            } else {

                User user = qService.findUser(userName, DOMAIN);
                File mailConfigPropertiesFile = new File(homePath, MAIL_CONFIG_FILE);
                String mailHeader = getProperties(mailConfigPropertiesFile, "mailHeader");
                String mailContent = getProperties(mailConfigPropertiesFile, "userCreateMailContent");
                String senderName = getProperties(mailConfigPropertiesFile, "senderName");
                String userCreateMailSubject = getProperties(mailConfigPropertiesFile, "userCreateMailSubject");

                String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> \r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n   <head>\r\n      <meta name=\"viewport\" content=\"width=device-width\" />\r\n      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n      <title>Verify Livo Account</title>\r\n      <style type=\"text/css\"> img { max-width: 100%; } @media only screen and (max-width: 640px) {  h1 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h2 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h3 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h4 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h1 {  font-size: 22px !important;  }  h2 {  font-size: 18px !important;  }  h3 {  font-size: 16px !important;  }  .container {  width: 100% !important;  }  .content {  padding: 10px !important;  }  .content-wrapper {  padding: 10px !important;  }  .invoice {  width: 100% !important;  } } </style>\r\n   </head>\r\n   <body style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none; width: 100% !important; height: 100%; line-height: 1.6; background: #fafafa; margin: 0; padding: 0;\">\r\n      <table class=\"body-wrap\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; width: 100%; background: #fafafa; margin: 0; padding: 0;\">\r\n         <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n            <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\r\n            <td class=\"container\" width=\"600\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; display: block !important; max-width: 520px !important; clear: both !important; margin: 0 auto; padding: 0;\" valign=\"top\">\r\n               <div class=\"content\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; max-width: 540px; display: block; margin: 0 auto; padding: 10px;\">\r\n                  <table class=\"main\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; border-radius: 3px; background: #fff; margin: 0; padding: 0; border: 1px solid #e8e8e8; border-radius: 2px; box-shadow: 0 2px 4px #e5e5e5;\">\r\n                     <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                        <td class=\"aligncenter\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; vertical-align: top; text-align: center; margin: 0; \" align=\"center\" valign=\"top\">\r\n                           <table class=\"content-wrap\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; padding: 15px 10px 10px 10px; margin: 0;\">\r\n                              <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                                 <td class=\"content-block\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\r\n                                    <h1 style=\"font-family: \\'Helvetica Neue\\', Helvetica, Arial, \\'Lucida Grande\\', sans-serif; box-sizing: border-box; font-size: 14px; color: #0d4269; line-height: 1.2; font-weight: 300; margin: 0 0 10px 0; padding: 0;\">" + mailHeader + "</h1>\r\n      </td>\r\n                              </tr>\r\n                              <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                                 <td class=\"content-block\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\r\n                                    <table style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; text-align: left; margin:0; padding: 0; width:100%;\">\r\n                                       <tbody style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                          <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                             <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                                <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 20px 0; color: #595959;\">" + mailContent + "</p>\r\n     <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">User Name : " + userName + "</p>\r\n                                                <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">Password : " + userPassword + "</p>\r\n     												<p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">Thank You!</p>                                        </td>\r\n                                          </tr>\r\n                                       </tbody>\r\n                                    </table>\r\n                                 </td>\r\n                              </tr>\r\n                           </table>\r\n                        </td>\r\n                     </tr>\r\n                  </table>\r\n               </div>\r\n            </td>\r\n            <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\r\n         </tr>\r\n      </table>\r\n   </body>\r\n</html>";

                mailService.sendMail(user.getMail(), senderName, userCreateMailSubject, content);

                LOGGER.debug("Mail sent with succesfully to " + user.getMail());
            }
        } catch (Exception ex) {

            LOGGER.error("New user cannot be created. " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
        }
        LOGGER.debug("User succesfully created.", userName);

        return "Succesfully created.";

    }

    /**
     * 
     * updates the user name and id of the user
     * 
     * @param userId
     * @param userName
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/changeUserNameWithNew", method = RequestMethod.POST)
    @ResponseBody
    public String changeUserName(@RequestParam("userId") String userId, @RequestParam("userName") String userName, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("changeUserNameWithNew() is started with: '{}'", " userID : " + userId + " userName : " + userName);
        try {
            //Get current authentication principal.
//            LivoUser user = (LivoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            USER = new User(user.getUserId(), user.getUserName(), user.getUserPassword(), user.getUserMail(), user.getActivationStatus());

            UserManagementService service = ManagementHelper.getUserManagementService();

            UserQueryService qService = ManagementHelper.getUserQueryService();

            service.updateUserID(qService.findUser(userId, DOMAIN), DOMAIN);

        } catch (RuntimeException re) {

            LOGGER.error("UserName cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (UserAlreadyExistsException ex) {
            LOGGER.error("User Already Exists Exception  " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
        } catch (UserNotFoundException ex) {
            LOGGER.error("User Not Found Exception " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * updates the user password of the current user, and later
     * sends an email informed the change event of the user password if the email configuration is done previously in the general settings
     * 
     * @param userId
     * @param userName
     * @param userPassword
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/changeuserpassword", method = RequestMethod.POST)
    @ResponseBody
    public String changeUserPassword(@RequestParam("userId") String userId, @RequestParam("userName") String userName, @RequestParam("userPassword") String userPassword, HttpServletResponse response) throws MalformedObjectNameException, IOException {
        LOGGER.debug("changeUserPassword() is started with: '{}'", " userId : " + userId + " userPassword :******");
        boolean mailConfigured = true;
        try {

            UserManagementService service = ManagementHelper.getUserManagementService();

            UserQueryService qService = ManagementHelper.getUserQueryService();

            User user = qService.findUser(userId, DOMAIN);

            service.updateUserPassword(user, userPassword);

            MailService mailService = ManagementHelper.getMailService();
            try {
                MailProperties mailProps = mailService.getMailProperties();
                if (mailProps.getHostName().isEmpty() || mailProps.getPort().isEmpty() || mailProps.getFrom().isEmpty()) {
                    mailConfigured = false;
                }
            } catch (Exception ex) {
                mailConfigured = false;
            }

            if (!mailConfigured) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return "User password changed with succesfully. Smtp settings is not configured yet.";
            } else {

//              User user = service.getUser(userName);
                File mailConfigPropertiesFile = new File(homePath, MAIL_CONFIG_FILE);
                String mailHeader = getProperties(mailConfigPropertiesFile, "mailHeader");
                String mailContent = getProperties(mailConfigPropertiesFile, "passwordChangeMailContent");
                String senderName = getProperties(mailConfigPropertiesFile, "senderName");
                String passwordChangeMailSubject = getProperties(mailConfigPropertiesFile, "passwordChangeMailSubject");

                String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> \r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n   <head>\r\n      <meta name=\"viewport\" content=\"width=device-width\" />\r\n      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n      <title>Verify Livo Account</title>\r\n      <style type=\"text/css\"> img { max-width: 100%; } @media only screen and (max-width: 640px) {  h1 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h2 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h3 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h4 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h1 {  font-size: 22px !important;  }  h2 {  font-size: 18px !important;  }  h3 {  font-size: 16px !important;  }  .container {  width: 100% !important;  }  .content {  padding: 10px !important;  }  .content-wrapper {  padding: 10px !important;  }  .invoice {  width: 100% !important;  } } </style>\r\n   </head>\r\n   <body style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none; width: 100% !important; height: 100%; line-height: 1.6; background: #fafafa; margin: 0; padding: 0;\">\r\n      <table class=\"body-wrap\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; width: 100%; background: #fafafa; margin: 0; padding: 0;\">\r\n         <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n            <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\r\n            <td class=\"container\" width=\"600\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; display: block !important; max-width: 520px !important; clear: both !important; margin: 0 auto; padding: 0;\" valign=\"top\">\r\n               <div class=\"content\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; max-width: 540px; display: block; margin: 0 auto; padding: 10px;\">\r\n                  <table class=\"main\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; border-radius: 3px; background: #fff; margin: 0; padding: 0; border: 1px solid #e8e8e8; border-radius: 2px; box-shadow: 0 2px 4px #e5e5e5;\">\r\n                     <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                        <td class=\"aligncenter\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; vertical-align: top; text-align: center; margin: 0; \" align=\"center\" valign=\"top\">\r\n                           <table class=\"content-wrap\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; padding: 15px 10px 10px 10px; margin: 0;\">\r\n                              <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                                 <td class=\"content-block\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\r\n                                    <h1 style=\"font-family: \\'Helvetica Neue\\', Helvetica, Arial, \\'Lucida Grande\\', sans-serif; box-sizing: border-box; font-size: 14px; color: #0d4269; line-height: 1.2; font-weight: 300; margin: 0 0 10px 0; padding: 0;\">" + mailHeader + "</h1>\r\n      </td>\r\n                              </tr>\r\n                              <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                                 <td class=\"content-block\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\r\n                                    <table style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; text-align: left; margin:0; padding: 0; width:100%;\">\r\n                                       <tbody style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                          <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                             <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                                <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 20px 0; color: #595959;\">" + mailContent + "</p>\r\n     <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">User Name : " + userName + "</p>\r\n                                                <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">Password : " + userPassword + "</p>\r\n     												<p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">Thank You!</p>                                        </td>\r\n                                          </tr>\r\n                                       </tbody>\r\n                                    </table>\r\n                                 </td>\r\n                              </tr>\r\n                           </table>\r\n                        </td>\r\n                     </tr>\r\n                  </table>\r\n               </div>\r\n            </td>\r\n            <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\r\n         </tr>\r\n      </table>\r\n   </body>\r\n</html>";

                mailService.sendMail(user.getMail(), senderName, passwordChangeMailSubject, content);

                LOGGER.debug("Mail sent with succesfully to " + user.getMail());

            }
        } catch (RuntimeException re) {

            LOGGER.error("User password cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (UserNotFoundException ex) {
            LOGGER.error("User not found. : " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: User not found.";
        } catch (InvalidPasswordException ex) {
            LOGGER.error("User password invalid : " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: User password is invalid.";
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * randomly generates the user password 
     * 
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/autoGeneratePassword", method = RequestMethod.POST)
    @ResponseBody
    public String autoGeneratePassword(HttpServletResponse response) throws MalformedObjectNameException, IOException {
        LOGGER.debug("autoGeneratePassword() is started.");

        String userPassword = "";
        try {

            userPassword = new BigInteger(40, new SecureRandom()).toString(32);

        } catch (RuntimeException re) {

            LOGGER.error("User password cannot be auto generated. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return userPassword;
    }

    @RequestMapping(value = "/changeuserrole", method = RequestMethod.POST)
    @ResponseBody
    public String changeUserRole(@RequestParam("userId") int userId, @RequestParam("userRole") String userRole, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Changing user role with: '{}'", " userRole : " + userRole);
        int role = 0;

        if (userRole.equalsIgnoreCase("Developer")) {
            role = 1;
        }

        try {

            //Get current authentication principal.
//            LivoUser user = (LivoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//
//            USER = new User(user.getUserId(), user.getUserName(), user.getUserPassword(), user.getUserMail(), user.getActivationStatus());
            UserManagementService service = ManagementHelper.getUserManagementService();

            //service.changeUserRole(userId, role, SecurityHelper.getCurrentUser().getToken(), USER);
        } catch (RuntimeException re) {

            LOGGER.error("User Role cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * changes the email info of the selected user
     * 
     * @param userId
     * @param userMail
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/changeusermail", method = RequestMethod.POST)
    @ResponseBody
    public String changeUserMail(@RequestParam("userId") String userId, @RequestParam("userMail") String userMail, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Changing user mail with: '{}'", userMail);
        try {

            //Get current authentication principal.
//            LivoUser user = (LivoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            USER = new User(user.getUserId(), user.getUserName(), user.getUserPassword(), user.getUserMail(), user.getActivationStatus());
            UserManagementService service = ManagementHelper.getUserManagementService();

            UserQueryService qService = ManagementHelper.getUserQueryService();

            User user = qService.findUser(userId, DOMAIN);

            User newUser = new User(user.getUserId(), user.getId(), user.getDomain(), userMail, user.getGroupId(), user.getFirstName(), user.getLastName(), user.isActive());

            service.updateUser(newUser);

        } catch (RuntimeException re) {

            LOGGER.error("User email cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (UserNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Succesfully changed.";
    }
 
    /**
     * 
     * updates and changes the user group of the selected user
     * 
     * @param userId
     * @param userGroupId
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/changeusergroup", method = RequestMethod.POST)
    @ResponseBody
    public String changeUserGroup(@RequestParam("userId") String userId, @RequestParam("userGroup") String userGroupId, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Changing user group with: '{}'", userGroupId);
        try {

            //Get current authentication principal.
//            LivoUser user = (LivoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            USER = new User(user.getUserId(), user.getUserName(), user.getUserPassword(), user.getUserMail(), user.getActivationStatus());
            UserManagementService service = ManagementHelper.getUserManagementService();

            UserQueryService qService = ManagementHelper.getUserQueryService();

            User user = qService.findUser(userId, DOMAIN);

            User currUser = new User(user.getUserId(), user.getId(), user.getDomain(), user.getMail(), userGroupId, user.getFirstName(), user.getLastName(), user.isActive());

            service.updateUser(currUser);
            
            //if user group is changed, then previously delete the user from the old user group
            if(!user.getGroupId().equals(userGroupId)){
                // Find User Group
                UserGroup userGroup = qService.findGroup(user.getGroupId(), DOMAIN);
                LOGGER.debug("userGroup size : " + userGroup.getUsers().size());

                // Add new user to User Group List
                Set<User> users = userGroup.getUsers();

                Set<User> updatedUsers = new HashSet<User>();

                Iterator iter = users.iterator();
                while (iter.hasNext()) {
                    User usr = (User) iter.next();
                    if(!usr.getId().equals(currUser.getId()))
                        updatedUsers.add(usr);
                }
                
                UserGroup updatedUserGroup = new UserGroup(userGroup.getName(), userGroup.getDomain(), userGroup.getDescription(), updatedUsers);

                //Update user group 
                service.updateGroup(updatedUserGroup);
                
            }

            // Find User Group
            UserGroup userGroup = qService.findGroup(userGroupId, DOMAIN);
            LOGGER.debug("userGroup size : " + userGroup.getUsers().size());

            // Add new user to User Group List
            Set<User> users = userGroup.getUsers();

            Set<User> newUsers = new HashSet<User>();

            if (users.size() < 1) {
                newUsers.add(currUser);
            } else {

                Iterator iter = users.iterator();
                while (iter.hasNext()) {
                    newUsers.add((User) iter.next());
                }
                newUsers.add(currUser);
            }
            UserGroup newUserGroup = new UserGroup(userGroup.getName(), userGroup.getDomain(), userGroup.getDescription(), newUsers);

            //Update user group 
            service.updateGroup(newUserGroup);

        } catch (RuntimeException re) {

            LOGGER.error("User group cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (UserNotFoundException ex) {
            
            LOGGER.error("UserNotFoundException " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
            
        } catch (UserGroupNotFoundException ex) {
            
            LOGGER.error("UserGroupNotFoundException " + ex.getMessage(), ex);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + ex.getMessage();
            
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * deletes the client user
     * 
     * @param userId
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/deleteuser", method = RequestMethod.POST)
    @ResponseBody
    public String deleteUser(@RequestParam("userId") String userId, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Delete user with: '{}'", " userId : " + userId);
        try {

            //Get current authentication principal.
//            LivoUser user = (LivoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            USER = new User(user.getUserId(), user.getUserName(), user.getUserPassword(), user.getUserMail(), user.getActivationStatus());
            UserManagementService service = ManagementHelper.getUserManagementService();

            UserQueryService qService = ManagementHelper.getUserQueryService();

            User user = qService.findUser(userId, DOMAIN);

            service.deleteUser(user);

        } catch (RuntimeException re) {

            LOGGER.error("User cannot be deleted. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (UserNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * changes the active condition of the selected user.
     * 
     * @param userName
     * @param isActive
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/suspendUser", method = RequestMethod.POST)
    @ResponseBody
    public String suspendUser(@RequestParam("userName") String userName, @RequestParam("isActive") boolean isActive, HttpServletResponse response) throws MalformedObjectNameException, IOException {
        LOGGER.debug("SuspendUser with: '{}'", " userName :" + userName + " isActive :" + isActive);
        try {

            //Get current authentication principal.
//            AuthenticationToken token = SecurityHelper.getCurrentUser().getToken();
//            LivoUser aeonUser = (LivoUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//            USER = new User(aeonUser.getUserId(), aeonUser.getUserName(), aeonUser.getUserPassword(), aeonUser.getUserMail(), aeonUser.getActivationStatus());
            UserManagementService service = ManagementHelper.getUserManagementService();

            UserQueryService qService = ManagementHelper.getUserQueryService();

            User user = qService.findUser(userName, DOMAIN);
            if (isActive) {
                service.activateUser(user);
            } else {
                service.suspendUser(user);

            }

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (RuntimeException re) {

            LOGGER.error("Cannot be changed. " + re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (UserNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return "Succesfully changed.";
    }

    // Web user operations..
    /**
     * 
     * creates a new web user(Developer) using the posted data
     * 
     * @param userName
     * @param userMail
     * @param userPassword
     * @param companyList the authorized company list of the web user(developer)
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/createNewWebUser", method = RequestMethod.POST)
    @ResponseBody
    public String createNewWebUser(@RequestParam("userName") String userName, @RequestParam("userMail") String userMail,
                                   @RequestParam("userPassword") String userPassword, @RequestParam("companyList") String companyList, HttpServletResponse response) throws MalformedObjectNameException, IOException {
        LOGGER.debug("createNewWebUser() is started with: '{}'" + " userName : " + userName + " UserMail : " + userMail + " UserPassword : ****** -- companyList: " + companyList);
        
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);

            manager.createNewWebUser(userName, userPassword, userMail, companyList, new Long(-1), false);

//            MailService mailService = ManagementHelper.getMailService();
//            LOGGER.debug("mailService getFrom(): " + mailService.getMailProperties().getFrom());
//            String content = " <h3>WELCOME</h3><p>Your user account created.<br/><strong>User Name : </strong>" + userName + "<br /><strong>Password : </strong>" + userPassword + "</p>";
//            mailService.sendMail(userMail, "Livo Mobile Mail Service", "New User ", content);
            LOGGER.debug("Mail send succesfull");

        } catch (RuntimeException re) {

            LOGGER.error("New web user cannot be created. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }
        LOGGER.debug("Web User succesfully created.", userName);

        return "Succesfully created.";

    }

    /**
     * 
     * modifies the user name info of the selected web user
     * 
     * @param userName
     * @param newUserName
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/modifyWebUserName", method = RequestMethod.POST)
    @ResponseBody
    public String modifyWebUserName(@RequestParam("userName") String userName, @RequestParam("newUserName") String newUserName, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("modifyWebUserName() is started with: '{}'", " userID : " + userName + " userName : " + userName);
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);

            manager.modifyWebUserName(userName, newUserName);

        } catch (RuntimeException re) {

            LOGGER.error("UserName cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * updates the password of the selected web user
     * 
     * @param userName
     * @param userPassword
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/modifyWebUserPassword", method = RequestMethod.POST)
    @ResponseBody
    public String modifyWebUserPassword(@RequestParam("userName") String userName, @RequestParam("userPassword") String userPassword, HttpServletResponse response) throws MalformedObjectNameException, IOException {
        LOGGER.debug("modifyWebUserPassword() is started with: '{}'", " userName : " + userName + " userPassword :******");
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);

            manager.modifyWebUserPassword(userName, userPassword);

        } catch (RuntimeException re) {

            LOGGER.error("User password cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * updates the email of the selected web user
     * 
     * @param userName
     * @param newUserMail
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/modifyWebUserMail", method = RequestMethod.POST)
    @ResponseBody
    public String modifyWebUserMail(@RequestParam("userName") String userName, @RequestParam("newUserMail") String newUserMail, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Changing user name with: '{}'", userName);
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);

            manager.modifyWebUserMail(userName, newUserMail);

        } catch (RuntimeException re) {

            LOGGER.error("User email cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }
    
    /**
     * 
     * updates the authorized company list of the selected web user(Developer)
     * 
     * @param userName
     * @param companyList
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/modifyWebUserCompanies", method = RequestMethod.POST)
    @ResponseBody
    public String modifyWebUserCompanies(@RequestParam("userName") String userName, @RequestParam("companyList") String companyList, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Changing companyList '{}' for username: '{}'", companyList, userName);
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);

            manager.modifyWebUserCompanies(userName, companyList);

        } catch (RuntimeException re) {

            LOGGER.error("User companies cannot be changed. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * deletes the selected web user
     * 
     * @param userName
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/deleteWebUser", method = RequestMethod.POST)
    @ResponseBody
    public String deleteWebUser(@RequestParam("userName") String userName, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Delete web user with: '{}'", " userName : " + userName);
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);
            //TODO delete web user method calling
            manager.deleteWebUser(userName);

        } catch (RuntimeException re) {

            LOGGER.error("Web User cannot be deleted. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }
    
    /**
     * 
     * deletes the selected device
     * 
     * @param companyId
     * @param userPrincipal
     * @param deviceId
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/deleteDevice", method = RequestMethod.POST)
    @ResponseBody
    public String deleteDevice(@RequestParam("companyId") String companyId, @RequestParam("userPrincipal") String userPrincipal, @RequestParam("deviceId") String deviceId, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Delete device with: '{}'", "companyId: " + companyId + ", userPrincipal: " + userPrincipal + ", deviceId: " + deviceId);
        try {
            //Get current authentication principal.
            DevicesService deviceService = ManagementHelper.getDevicesService();

            deviceService.deleteDeviceId(companyId, userPrincipal, deviceId);

        } catch (RuntimeException re) {

            LOGGER.error("Device cannot be deleted. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        } catch (InstanceNotFoundException ex) {
            LOGGER.error("InstanceNotFoundException --> " + ex.getMessage(), ex);
        }

        return "Succesfully deleted";
    }

    @RequestMapping(value = "/modifySelfAccount", method = RequestMethod.POST)
    @ResponseBody
    public String modifySelfAccount(@RequestParam("userName") String userName, @RequestParam("password") String password, @RequestParam("userMail") String userMail, HttpServletResponse response) throws MalformedObjectNameException, IOException {

        LOGGER.debug("Delete web user with: userName : '{}' userMail : '{}'", userName, userMail);
        try {
            //Get current authentication principal.
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            WebUser user = (WebUser) authentication.getPrincipal();

            WebUserManager manager = new WebUserManager(user);
            //TODO delete web user method calling
            manager.modifyDeveloper(userName, password, userMail);

        } catch (RuntimeException re) {

            LOGGER.error("Web User cannot be deleted. " + re.getMessage(), re);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Failed: " + re.getMessage();
        }

        return "Succesfully changed.";
    }

    /**
     * 
     * creates the whole client users from the imported, formated file, and later
     * sends the emails with standard format to them one by one if the email configuration is done in the general settings
     * 
     * @param file
     * @param groupName
     * @param modelMap
     * @param response
     * @return
     * @throws InstanceNotFoundException
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/createUserFromFile", method = RequestMethod.POST)
    @ResponseBody
    public String createUserFromFile(@RequestParam(value = "importUserFile", required = true) MultipartFile file, @RequestParam(value = "group") String groupName, ModelMap modelMap, HttpServletResponse response) throws InstanceNotFoundException, MalformedObjectNameException, IOException {
        LOGGER.debug("createUserFromFile started with params, groupName : " + groupName);
        List<User> failUserList = null;
        List<User> importedUserList = null;
        boolean mailConfigured = true;

        File mailConfigPropertiesFile = new File(homePath, MAIL_CONFIG_FILE);

        String mailHeader = getProperties(mailConfigPropertiesFile, "mailHeader");
        String mailContent = getProperties(mailConfigPropertiesFile, "userCreateMailContent");
        String senderName = getProperties(mailConfigPropertiesFile, "senderName");
        String userCreateMailSubject = getProperties(mailConfigPropertiesFile, "userCreateMailSubject");

        File tmpFile;

        try {

            tmpFile = File.createTempFile(file.getOriginalFilename(), null);

            tmpFile.deleteOnExit();

            FileOutputStream fos = new FileOutputStream(tmpFile);

            BufferedInputStream bis = new BufferedInputStream(file.getInputStream());

            int nextByte;
            while ((nextByte = bis.read()) != -1) {

                fos.write(nextByte);
            }

            fos.close();

            UserImportManager manager = new UserImportManager(tmpFile);

            manager.importUsers();

            failUserList = manager.getFails();

            importedUserList = manager.getImportedUsers();

            List<User> sendMailList = importedUserList;
            List<User> emailFailList = new ArrayList<User>();
            List<User> emailSentList = new ArrayList<User>();

            LOGGER.debug("sendMailList.size() : " + sendMailList.size());
            MailService mailService = ManagementHelper.getMailService();
            try {
                MailProperties mailProps = mailService.getMailProperties();
                if (mailProps.getHostName().isEmpty() || mailProps.getPort().isEmpty()) {
                    mailConfigured = false;
                    LOGGER.debug("Mail Configured : " + mailConfigured);
                } else {
                    mailConfigured = true;
                    LOGGER.debug("Mail Configured : " + mailConfigured);
                }
            } catch (Exception ex) {
                mailConfigured = false;
                LOGGER.error("Get Mail properties error. '{}'", ex);
            }

            if (mailConfigured) {
                for (User user : sendMailList) {
                    try {
                        String content = "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"> \r\n<html xmlns=\"http://www.w3.org/1999/xhtml\">\r\n   <head>\r\n      <meta name=\"viewport\" content=\"width=device-width\" />\r\n      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\r\n      <title>Verify Livo Account</title>\r\n      <style type=\"text/css\"> img { max-width: 100%; } @media only screen and (max-width: 640px) {  h1 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h2 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h3 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h4 {  font-weight: 600 !important; margin: 20px 0 5px !important;  }  h1 {  font-size: 22px !important;  }  h2 {  font-size: 18px !important;  }  h3 {  font-size: 16px !important;  }  .container {  width: 100% !important;  }  .content {  padding: 10px !important;  }  .content-wrapper {  padding: 10px !important;  }  .invoice {  width: 100% !important;  } } </style>\r\n   </head>\r\n   <body style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; -webkit-font-smoothing: antialiased; -webkit-text-size-adjust: none; width: 100% !important; height: 100%; line-height: 1.6; background: #fafafa; margin: 0; padding: 0;\">\r\n      <table class=\"body-wrap\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; width: 100%; background: #fafafa; margin: 0; padding: 0;\">\r\n         <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n            <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\r\n            <td class=\"container\" width=\"600\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; display: block !important; max-width: 520px !important; clear: both !important; margin: 0 auto; padding: 0;\" valign=\"top\">\r\n               <div class=\"content\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; max-width: 540px; display: block; margin: 0 auto; padding: 10px;\">\r\n                  <table class=\"main\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; border-radius: 3px; background: #fff; margin: 0; padding: 0; border: 1px solid #e8e8e8; border-radius: 2px; box-shadow: 0 2px 4px #e5e5e5;\">\r\n                     <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                        <td class=\"aligncenter\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; vertical-align: top; text-align: center; margin: 0; \" align=\"center\" valign=\"top\">\r\n                           <table class=\"content-wrap\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; padding: 15px 10px 10px 10px; margin: 0;\">\r\n                              <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                                 <td class=\"content-block\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\r\n                                    <h1 style=\"font-family: \\'Helvetica Neue\\', Helvetica, Arial, \\'Lucida Grande\\', sans-serif; box-sizing: border-box; font-size: 14px; color: #0d4269; line-height: 1.2; font-weight: 300; margin: 0 0 10px 0; padding: 0;\">" + mailHeader + "</h1>\r\n      </td>\r\n                              </tr>\r\n                              <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0; padding: 0;\">\r\n                                 <td class=\"content-block\" style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0 0 20px;\" valign=\"top\">\r\n                                    <table style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; text-align: left; margin:0; padding: 0; width:100%;\">\r\n                                       <tbody style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                          <tr style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                             <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0;\">\r\n                                                <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 20px 0; color: #595959;\">" + mailContent + "</p>\r\n     <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">User Name : " + user.getId() + "</p>\r\n                                                <p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">Password : " + manager.getUserPassword(user) + "</p>\r\n     												<p style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; margin: 0 0 10px 0; color: #595959;\">Thank You!</p>                                        </td>\r\n                                          </tr>\r\n                                       </tbody>\r\n                                    </table>\r\n                                 </td>\r\n                              </tr>\r\n                           </table>\r\n                        </td>\r\n                     </tr>\r\n                  </table>\r\n               </div>\r\n            </td>\r\n            <td style=\"font-family: \\'Helvetica Neue\\', \\'Helvetica\\', Helvetica, Arial, sans-serif; box-sizing: border-box; font-size: 13px; vertical-align: top; margin: 0; padding: 0;\" valign=\"top\"></td>\r\n         </tr>\r\n      </table>\r\n   </body>\r\n</html>";

                        mailService.sendMail(user.getMail(), senderName, userCreateMailSubject, content);

                        LOGGER.debug("Mail sent with succesfully to " + user.getMail());
                        emailSentList.add(user);
                    } catch (RuntimeException ex) {
                        LOGGER.error("Mail send error. '{}'", ex.fillInStackTrace().toString(), ex);
                        emailFailList.add(user);
                    }
                }
            }
            if (!groupName.equals("0") ) {
                UserManagementService service = ManagementHelper.getUserManagementService();

                UserQueryService qService = ManagementHelper.getUserQueryService();

                UserGroup userGroup = qService.findGroup(groupName, DOMAIN);

                for (User user : sendMailList) {
                    userGroup.addUser(user);
                }
                service.updateGroup(userGroup);
            }
        } catch (UserGroupNotFoundException e) {
            LOGGER.error("File upload can not be completed, User group  is not found. '{}'", e.fillInStackTrace().toString(), e);
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return "File import can not be completed!";
        } catch (Exception e) {
            LOGGER.error("File upload can not be completed, User group is not found. '{}'", e.fillInStackTrace().toString(), e);
            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            return "File import can not be completed!";
        }

        return "Succesfully imported.";
    }

    /**
     * 
     * checks whether smtp settings are done or not
     * 
     * @param response
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/controlSmtpMailProperties", method = RequestMethod.POST)
    @ResponseBody
    public String controlSmtpMailProperties(HttpServletResponse response) throws MalformedObjectNameException, IOException {
        LOGGER.debug("controlSmtpMailProperties() is started.");

        try {

            MailService mailService = ManagementHelper.getMailService();

            MailProperties mailProps = mailService.getMailProperties();

            if (mailProps.getHostName().isEmpty() || mailProps.getPort().isEmpty() || mailProps.getFrom().isEmpty()) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return "Smtp settings is not configured yet. Users will not receive the password as email. Are you sure ?";
            }

            LOGGER.debug("Smtp settings control completed with succesfully.");

        } catch (MalformedObjectNameException | IOException | RuntimeException e) {

            LOGGER.error("Smtp settings control can not be  completed. " + e.getMessage(), e);

            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            return "Smtp settings is not configured yet. Users will not receive the password as email. Are you sure ?";
        }

        return "Smtp settings control completed with succesfully.";

    }

    private String getProperties(File propertiesFile, String key) {

//        LOGGER.debug("getProperties is started with {} " + "key : " + key);
        Properties prop = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(propertiesFile);
            prop.load(fis);
            if (prop.get(key) != null) {
                return String.valueOf(prop.get(key));
            } else if (prop.get("default") != null) {
                return String.valueOf(prop.get("default"));
            } else {
                throw new Exception("Value not found at " + propertiesFile.getName() + ". {} key :" + key);
            }
        } catch (Exception ex) {
            LOGGER.error("Exception", ex);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    LOGGER.error("Exception", e);
                }
            }

        }
        return null;

    }

    /**
     * 
     * gets the group users if a group is selected, or
     * gets all users if a user is not selected
     * 
     * @param map holds the selected users and sends them to jsp
     * @param group the selected group id
     * @return
     * @throws MalformedObjectNameException
     * @throws IOException 
     */
    @RequestMapping(value = "/userTableFromGroup", method = RequestMethod.POST)
    public String showUsersFromGroup(ModelMap map, @RequestParam("group") String group) throws MalformedObjectNameException, IOException {
        LOGGER.debug("showUsers() is started.");
        try {
            //Get the connection to user management service.
            UserQueryService service = ManagementHelper.getUserQueryService();
            //Get users from connection.
            Set<User> users = null;
            if (group.equals("all")) {
                //All users
                users = service.listUsers();

            } else {
                // Group's users
                users = service.findGroup(group, DOMAIN).getUsers();
            }
            Set<UserGroup> groups = service.listGroups();

            LOGGER.debug("Users count at group : " + users.size());
//          Put them into modelmap
            map.put("users", users);
            map.put("groups", groups);

            return "users";

        } catch (Exception e) {
            LOGGER.error("Exception has been occured in showUsers(): " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
