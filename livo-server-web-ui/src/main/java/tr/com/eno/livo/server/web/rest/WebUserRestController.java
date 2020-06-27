/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.com.eno.livo.server.web.rest;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tr.com.eno.livo.server.web.WebUser;
import tr.com.eno.livo.server.web.WebUserManager;

/**
 *
 * @author Macintosh
 */
@Controller
public class WebUserRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyRestController.class);
    
    @RequestMapping(value = RestURIConstants.GET_WEBUSER, method = RequestMethod.GET)
    public @ResponseBody WebUser getWebUser(@PathVariable("id") String userId) {
        logger.info("Start getWebUser");
        //Get current authentication.
        WebUserManager manager = new WebUserManager(null);

        List<WebUser> webUserList = manager.getWebUsers(userId.hashCode());
        WebUser currUser = webUserList.size() == 1 ? webUserList.get(0) : null;
        
        return currUser;
        
    }
    
    @RequestMapping(value = RestURIConstants.CRT_WEBUSER, method = RequestMethod.POST)
    public @ResponseBody WebUser setNewWebUser(@RequestBody WebUser webUser) {
        logger.info("Start setNewWebUser");
        //Get current authentication.
        WebUserManager manager = new WebUserManager(null);

        logger.info("setNewWebUser --> " + webUser.getUserName() + " -- " + webUser.getUserPassword() + " -- " + webUser.getUserMail() + " -- "
                                         + webUser.getCompanyList() + " -- " + webUser.getExpirationDate());
        manager.createNewWebUser(webUser.getUserName(), webUser.getUserPassword(), webUser.getUserMail(), webUser.getCompanyList(), webUser.getExpirationDate(), true);
        
        return webUser;
        
    }
    
    @RequestMapping(value = RestURIConstants.UPD_WEBUSER, method = RequestMethod.POST)
    public @ResponseBody WebUser updWebUser(@RequestBody WebUser webUser) {
        logger.info("Start setNewWebUser");
        //Get current authentication.
        WebUserManager manager = new WebUserManager(null);

        manager.modifyDeveloper(webUser.getUserName(), webUser.getUserPassword(), webUser.getUserMail());
        
        return webUser;
        
    }
    
}
