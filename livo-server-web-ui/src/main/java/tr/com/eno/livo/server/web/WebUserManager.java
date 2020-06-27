package tr.com.eno.livo.server.web;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.NoHostAvailableException;
import com.datastax.driver.core.exceptions.QueryExecutionException;
import com.datastax.driver.core.exceptions.QueryValidationException;
import com.datastax.driver.core.exceptions.UnsupportedFeatureException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.codec.CodecException;
import org.apache.shiro.crypto.UnknownAlgorithmException;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import tr.com.eno.livo.server.application.Deployments;
import tr.com.eno.livo.server.users.User;

public class WebUserManager {

    private final static Logger LOGGER = LoggerFactory.getLogger(WebUserManager.class);

    private final WebUser user;

    public WebUserManager(WebUser user) {

        this.user = user;

    }

    public void createNewWebUser(String userName, String userPassword, String userMail, String companyList, Long expirationDate, boolean fromRest) {

        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();
        
                //Save authorized companies
        LOGGER.debug("Save authorized companies: " + companyList.length());

        try {

            if(!fromRest)
                SecurityUtils.getSubject().checkPermissions("create:webuser", "create:developer");

            PreparedStatement statement = session.prepare("INSERT INTO web.users (userid, username, userpassword, passwordsalt, usermail,creationtime,expiration_date,creator,login_permission) VALUES (?, ?, ?, ?, ?,?,?,?,?) IF NOT EXISTS USING TTL 31536000;");

            session.execute(statement.bind(userName.hashCode(), userName, ByteBuffer.wrap(new SimpleHash("SHA-256", userPassword, ("Settar" + userName).getBytes("UTF-8")).getBytes()), ByteBuffer.wrap(("Settar" + userName).getBytes("UTF-8")), userMail, new Date().getTime(), expirationDate, "LivoTeam",true));

            statement = session.prepare("INSERT INTO web.userroles(roleid, username,userrole,time) VALUES (?,?,?,?) IF NOT EXISTS USING TTL 31536000;");

            session.execute(statement.bind((userName+userName).hashCode(), userName, "Developer", new Date().getTime()));

            LOGGER.debug("Inserting authorized companies: " + companyList + " -- userName: " + userName);
            if(companyList.length() > 1){

                String[] companyArr = companyList.split(",");
                for (String compId : companyArr) {
                    if(compId.length() > 0)
                        session.execute(session.prepare("INSERT INTO web.companies(userid, company_id, create_user, create_date) VALUES (?,?,?,?);")
                                           .bind(userName.hashCode(), Integer.parseInt(compId), this.user != null ? this.user.getUserId() : null, new Date().getTime()));
                }
                
            }

            connector.disconnect();

        } catch (UnsupportedEncodingException ex) {
            connector.disconnect();

            LOGGER.error("Encoding exception: {}", ex);

            throw new RuntimeException("Encoding problem.");

        } catch (AuthorizationException ex) {

            connector.disconnect();

            throw new SecurityException("User has no permission!");
        } catch (NoHostAvailableException ex) {

            connector.disconnect();

            throw new RuntimeException("Host is unavailable");
        } catch (QueryExecutionException ex) {

            connector.disconnect();

            LOGGER.error("Query error: {}", ex);

            throw new RuntimeException(ex.getCause());
        } catch (QueryValidationException ex) {

            connector.disconnect();

            LOGGER.error("Validation error:{}", ex);

            throw new RuntimeException("Validation problem.");
        } catch (UnsupportedFeatureException ex) {

            connector.disconnect();

            LOGGER.error(ex.getMessage() + " :{}", ex);

            throw new RuntimeException("Critical problem!");
        }

    }
    
    public void createNewServiceUsers(Integer userid, String serviceName) {
        
        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();
        
                //Save authorized companies
        LOGGER.debug("createNewServiceUsers --->  " + userid + " -- " + serviceName);

        try {

            session.execute(session.prepare("INSERT INTO web.service_users(userid, service_name, create_user, create_date) VALUES (?,?,?,?);")
                                           .bind(userid, serviceName, this.user.getUserId(), new Date().getTime()));

            connector.disconnect();

        }catch (NoHostAvailableException ex) {

            connector.disconnect();

            throw new RuntimeException("Host is unavailable");
        } catch (QueryExecutionException ex) {

            connector.disconnect();

            LOGGER.error("Query error: {}", ex);

            throw new RuntimeException(ex.getCause());
        } catch (QueryValidationException ex) {

            connector.disconnect();

            LOGGER.error("Validation error:{}", ex);

            throw new RuntimeException("Validation problem.");
        } catch (UnsupportedFeatureException ex) {

            connector.disconnect();

            LOGGER.error(ex.getMessage() + " :{}", ex);

            throw new RuntimeException("Critical problem!");
        }
        
    }

    public void modifyWebUserName(String oldName, String newName) {

        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();

        if (oldName.equalsIgnoreCase("Administrator")) {
            throw new SecurityException("Administrator account name can not be changed!");
        }
        try {
            SecurityUtils.getSubject().checkPermission("*");

            session.execute("UPDATE web.users USING TTL 31536000 SET username=" + newName + " where userid=" + oldName.hashCode());

            connector.disconnect();
        } catch (Exception ex) {

            connector.disconnect();

            LOGGER.error("Error during changing username, ERROR: {}", ex);

            throw new RuntimeException("Error during changing user attribute!");

        }

    }

    public void modifyWebUserPassword(String userName, String newPassword) {

        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();

        Subject subject = SecurityUtils.getSubject();
        try {

            if (userName.equalsIgnoreCase("Administrator")) {
                subject.checkPermission("*");
            } else {
                subject.checkPermissions(DeveloperPermissions.MODIFY_SELF.getPermission());
            }

            PreparedStatement statement = session.prepare("UPDATE web.users USING TTL 31536000 SET userpassword=?,passwordsalt=? where userid=?");
            session.execute(statement.bind(ByteBuffer.wrap(new SimpleHash("SHA-256", newPassword, ("Settar" + userName).getBytes("UTF-8")).getBytes()), ByteBuffer.wrap(("Settar" + userName).getBytes("UTF-8")), userName.hashCode()));

            connector.disconnect();
        } catch (Exception ex) {

            connector.disconnect();

            LOGGER.error("Error during changing password, ERROR: {}", ex);

            throw new RuntimeException("Error during changing user attribute!");

        }
    }

    public void modifyWebUserMail(String userName, String newMailAdress) {

        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();

        try {

            if (userName.equalsIgnoreCase("Administrator")) {
                SecurityUtils.getSubject().checkPermission("*");
            } else {
                SecurityUtils.getSubject().checkPermissions(DeveloperPermissions.MODIFY_SELF.getPermission());
            }

            PreparedStatement statement = session.prepare("UPDATE web.users SET usermail=? where userid=?");

            session.execute(statement.bind(newMailAdress, userName.hashCode()));

            connector.disconnect();
        } catch (Exception ex) {

            connector.disconnect();

            LOGGER.error("Error during changing username, ERROR: {}", ex);

            throw new RuntimeException("Error during changing user attribute!");

        }
    }
    
    public void modifyWebUserCompanies(String userName, String companyList) {

        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();

        try {

            if (userName.equalsIgnoreCase("Administrator")) {
                SecurityUtils.getSubject().checkPermission("*");
            } else {
                SecurityUtils.getSubject().checkPermissions(DeveloperPermissions.MODIFY_SELF.getPermission());
            }
            
            LOGGER.debug("modifyWebUserCompanies: Delete all companies for " + userName + " and then insert " + companyList);
            session.execute(session.prepare("DELETE FROM web.companies WHERE userid = ?;").bind(userName.hashCode()));
            
            if(companyList.length() > 1){

                String[] companyArr = companyList.split(",");
                for (String compId : companyArr) {
                    if(compId.length() > 0)
                        session.execute(session.prepare("INSERT INTO web.companies(userid, company_id, create_user, create_date) VALUES (?,?,?,?);")
                                               .bind(userName.hashCode(), Integer.parseInt(compId), this.user.getUserId(), new Date().getTime()));
                }
                
            }

            connector.disconnect();
        } catch (Exception ex) {

            connector.disconnect();

            LOGGER.error("Error during changing username, ERROR: {}", ex);

            throw new RuntimeException("Error during changing user attribute!");

        }
    }

    public List<WebUser> getWebUsers(Integer userId) {

        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        PreparedStatement statement = session.prepare("Select userid, username, usermail from web.users allow filtering;");
        if(userId != null)
            statement = session.prepare("Select userid, username, usermail from web.users where userid = ?;");

        // "Select userpassword, passwordsalt from web.users where username = ? allow filtering"
        try {

            List<WebUser> users = new LinkedList<>();

            List<Row> wUserList = session.execute(userId != null ? statement.bind(userId) : statement.bind()).all();

            WebUser webUser = null;
            for (Row row : wUserList) {

                webUser = new WebUser(row.getString("username"), "", row.getString("usermail"));
                webUser.setUserId(row.getInt("userid"));
                users.add(webUser);
                
            }

            connector.disconnect();
            return users;
        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
            connector.disconnect();
            return null;
        } finally {
            LOGGER.debug("Finally");
        }

    }
    
    public List<Map> getWebUserCompanies(List<WebUser> userList) {
        
        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        ArrayList<Map> companyList = new ArrayList<>();
        
        String userCompanies;
        try {

            PreparedStatement statement = session.prepare("select * from web.companies where userid = ?;");
            for (WebUser user : userList) {

                List<Row> cList = session.execute(statement.bind(user.getUserId())).all();
                
                userCompanies = ",";
                for (Row row : cList) {

                    userCompanies += row.getInt("company_id") + ",";
                    
                }
                
                Map<String, String>  companyMap = new HashMap();
                companyMap.put("username", user.getUserName());
                companyMap.put("company_list", userCompanies);
                companyList.add(companyMap);

            }
            connector.disconnect();
            
        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
            connector.disconnect();
            return null;
        } finally {
            LOGGER.debug("Finally");
        }
        
        return companyList;
        
    }
    
    public List<Integer> getCompanies4CurrentUser() {
        
        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        ArrayList<Integer> companyList = new ArrayList<>();
        
        try {

            List<Row> cList = session.execute(session.prepare("Select * from web.companies where userid = ?;").bind(this.user.getUserId())).all();

            for (Row row : cList) {

                companyList.add(row.getInt("company_id"));

            }

            connector.disconnect();
            
        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
            connector.disconnect();
            return null;
        } finally {
            LOGGER.debug("Finally");
        }
        
        return companyList;
        
    }
    
    public List<Integer> getWebUsers4Company(int companyID) {
        
        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        ArrayList<Integer> userList = new ArrayList<>();
        
        try {

            List<Row> cList = session.execute(session.prepare("Select * from web.companies where company_id = ? allow filtering;").bind(companyID)).all();

            for (Row row : cList) {

                userList.add(row.getInt("userid"));

            }

            connector.disconnect();
            
        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
            connector.disconnect();
            return null;
        } finally {
            LOGGER.debug("Finally");
        }
        
        return userList;
        
    }
    
    public List<Deployments> setWebUsers4Deployments(List<Deployments> deployments) {
        
        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        try {
            
            for (Deployments deployment : deployments) {
                if(deployment.getUserId() > 0){
                    Row user = session.execute(session.prepare("select userid, username from web.users where userid = ?;").bind(deployment.getUserId())).one();
                    deployment.setUserName(user.getString("username"));
                    LOGGER.debug("userName for " + deployment.getUserId() + " is " + user.getString("username"));
                }
                else
                    deployment.setUserName("Undefined");
                
                Date date = new Date(deployment.getCreateDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                deployment.setFormatedDate(sdf.format(date));
        
            }

            connector.disconnect();
            
        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
            connector.disconnect();
            return null;
        } finally {
            LOGGER.debug("Finally");
        }
        
        return deployments;
        
    }
    
    /**
     * 
     * gets the map containing relation informations about services and their users
     * 
     * if userid is equal to -1, it means that the service can be seen and used by everyone
     * if userid is the id of current user or create user, then the service can be seen and used by him/her
     * if the current user is 'Administrator', then all services can be seen and used by him/her
     * 
     * @param serviceList
     * @return 
     */
    public List<Map> getWebUserServices(List<String> serviceList) {
        LOGGER.debug("getWebUserServices ---> " + this.user.getUserName());
        
        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        ArrayList<Map> serviceUserList = new ArrayList<>();
        
        String serviceUsers;
        boolean userExists = false;
        try {

            for (String sName : serviceList) {

                List<Row> usList = session.execute(session.prepare("Select * from web.service_users where service_name = ? allow filtering;").bind(sName)).all();
                
                serviceUsers = ",";
                for (Row row : usList) {

                    userExists = (row.getInt("userid") == -1 || this.user.getUserId() == row.getInt("userid") || this.user.getUserId() == row.getInt("create_user")) ? true : false;
                    serviceUsers += row.getInt("userid") + ",";
                    
                }
                userExists = this.user.getUserName().equals("Administrator") ? true : userExists;
                
                Map<String, String>  userServiceMap = new HashMap();
                userServiceMap.put("service_name", sName);
                userServiceMap.put("user_list", serviceUsers);
                userServiceMap.put("user_exists", userExists ? "yes" : "no");
                LOGGER.debug("getWebUserServices ---> " + sName + " -- " + serviceUsers + " -- " + userExists);
                serviceUserList.add(userServiceMap);

            }
            connector.disconnect();
            
        } catch (Exception ex) {
            LOGGER.error("Cannot run the query, ERROR: {}", ex);
            connector.disconnect();
            return null;
        } finally {
            LOGGER.debug("Finally");
        }
        
        return serviceUserList;
        
    }

    public void deleteWebUser(String userName) {

        if (userName.equalsIgnoreCase("administrator")) {

            throw new SecurityException("Administrator account cannot be deleted.");
        }

        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();

        try {
            SecurityUtils.getSubject().checkPermissions("*");

            LOGGER.debug("Deleting the user: " + userName);
            PreparedStatement statement = session.prepare("DELETE FROM web.users  WHERE userid=?");

            session.execute(statement.bind(userName.hashCode()));
            
            LOGGER.debug("Delete all related companies and service users for " + userName);
            session.execute(session.prepare("DELETE FROM web.companies WHERE userid = ?;").bind(userName.hashCode()));
            session.execute(session.prepare("DELETE FROM web.service_users WHERE userid = ?;").bind(userName.hashCode()));

            connector.disconnect();

        } catch (AuthorizationException ex) {

            connector.disconnect();

            throw new SecurityException("User has no permission!");
        } catch (NoHostAvailableException ex) {

            connector.disconnect();

            throw new RuntimeException("Host is unavailable");
        } catch (QueryExecutionException ex) {

            connector.disconnect();

            LOGGER.error("Query error: {}", ex);

            throw new RuntimeException(ex.getCause());
        } catch (QueryValidationException ex) {

            connector.disconnect();

            LOGGER.error("Validation error:{}", ex);

            throw new RuntimeException("Validation problem.");
        } catch (UnsupportedFeatureException ex) {

            connector.disconnect();

            LOGGER.error(ex.getMessage() + " :{}", ex);

            throw new RuntimeException("Critical problem!");
        }

    }
    
    public void deleteUsers4Service(String serviceName) {
        
        CassandraConnector connector = new CassandraConnector();
        
        Session session = connector.getSession();
        
        List<Row> usList = session.execute(session.prepare("Select * from web.service_users where service_name = ? allow filtering;").bind(serviceName)).all();
                
        for (Row row : usList) {

            session.execute(session.prepare("DELETE FROM web.service_users WHERE userid = ?;").bind(row.getInt("userid")));

        }
        
    }

    public void modifyDeveloper(String userName, String password, String userMail) {
        CassandraConnector connector = new CassandraConnector();

        Session session = connector.getSession();

        try {

            if (userName.equalsIgnoreCase("Administrator")) {
                SecurityUtils.getSubject().checkPermission("*");
            } else {
                SecurityUtils.getSubject().checkPermissions(DeveloperPermissions.MODIFY_SELF.getPermission());
            }

            if (!"".equals(userMail) && !"".equals(password)) {

                PreparedStatement statement = session.prepare("UPDATE web.users  USING TTL 31536000 SET userpassword=?,passwordsalt=?, usermail=? where userid=?");

                session.execute(statement.bind(ByteBuffer.wrap(new SimpleHash("SHA-256", password, ("Settar" + userName).getBytes("UTF-8")).getBytes()), ByteBuffer.wrap(("Settar" + userName).getBytes("UTF-8")), userMail, userName.hashCode()));

            } else if (!"".equals(userMail)) {

                PreparedStatement statement = session.prepare("UPDATE web.users  USING TTL 31536000 SET  usermail=? where userid=?");

                session.execute(statement.bind(userMail, userName.hashCode()));

            } else if (!"".equals(password)) {

                PreparedStatement statement = session.prepare("UPDATE web.users  USING TTL 31536000 SET userpassword=?,passwordsalt=? where userid=?");

                session.execute(statement.bind(ByteBuffer.wrap(new SimpleHash("SHA-256", password, ("Settar" + userName).getBytes("UTF-8")).getBytes()), ByteBuffer.wrap(("Settar" + userName).getBytes("UTF-8")), userName.hashCode()));

            }

            connector.disconnect();
        } catch (AuthorizationException | UnsupportedEncodingException | CodecException | UnknownAlgorithmException ex) {

            connector.disconnect();

            LOGGER.error("Error during changing username, ERROR: {}", ex);

            throw new RuntimeException("Error during changing user attribute!");

        }
    }
}
