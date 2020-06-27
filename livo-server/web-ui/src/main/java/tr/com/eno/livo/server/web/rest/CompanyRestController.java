/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.com.eno.livo.server.web.rest;

import com.livo.companies.Companies;
import com.livo.companies.CompaniesService;
import com.livo.companies.CompanyAlreadyExistsException;
import com.livo.companies.CompanyNotFoundException;
import com.livo.companies.InvalidCompanyException;
import com.livo.companies.InvalidPasswordException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.management.InstanceNotFoundException;
import javax.management.MalformedObjectNameException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import tr.com.eno.livo.server.web.ManagementHelper;

/**
 *
 * @author Macintosh
 */
@Controller
public class CompanyRestController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompanyRestController.class);
    
    @RequestMapping(value = RestURIConstants.GET_COMPANIES, method = RequestMethod.GET)
    public @ResponseBody List<Companies> getAllCompanies() {
        logger.info("Start getAllCompanies");
        List<Companies> companyList = new ArrayList<>();
        //Get current authentication.
        CompaniesService companiesService = null;
        try {
            companiesService = ManagementHelper.getCompaniesService();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (InstanceNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (MalformedObjectNameException ex) {
            logger.error(ex.getMessage());
        }

        Set<Companies> companies = companiesService.listCompanies();

        for (Companies company : companies) {
            Date date = new Date(company.getCreateDate());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
            Companies tmpCompany = new Companies(company.getRecordId(), company.getCompanyId(), company.getCompanyTitle(), company.getCreateUser(), company.getCreateDate(), true);
            tmpCompany.setFormatedDate(sdf.format(date));
            companyList.add(tmpCompany);
        }
        
        return companyList;
        
    }
    
    @RequestMapping(value = RestURIConstants.GET_COMPANY, method = RequestMethod.GET)
    public @ResponseBody Companies getCompany(@PathVariable("id") String companyId) {
        logger.info("Start getCompany");
        //Get current authentication.
        CompaniesService companiesService = null;
        try {
            companiesService = ManagementHelper.getCompaniesService();
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (InstanceNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (MalformedObjectNameException ex) {
            logger.error(ex.getMessage());
        }

        Set<Companies> companies = companiesService.listCompanies();
        Companies currCompany = null;

        for (Companies company : companies) {
            
            if(company.getCompanyId().equals(companyId)){
                Date date = new Date(company.getCreateDate());
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss");
                currCompany = new Companies(company.getRecordId(), company.getCompanyId(), company.getCompanyTitle(), company.getCreateUser(), company.getCreateDate(), true);
                currCompany.setFormatedDate(sdf.format(date));
            }
            
        }
        
        return currCompany;
        
    }
    
    @RequestMapping(value = RestURIConstants.CRT_COMPANY, method = RequestMethod.POST)
    public @ResponseBody Companies setNewCompany(@RequestBody Companies currCompany) {
        logger.info("Start setNewCompany");
        //Get current authentication.
        CompaniesService companiesService = null;
        try {
            companiesService = ManagementHelper.getCompaniesService();
            companiesService.createCompany(currCompany, currCompany.getPassword());
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (InstanceNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (MalformedObjectNameException ex) {
            logger.error(ex.getMessage());
        } catch (InvalidCompanyException ex) {
            logger.error(ex.getMessage());
        } catch (InvalidPasswordException ex) {
            logger.error(ex.getMessage());
        } catch (CompanyAlreadyExistsException ex) {
            logger.error(ex.getMessage());
        }
        
        return currCompany;
        
    }
    
    @RequestMapping(value = RestURIConstants.UPD_COMPANY, method = RequestMethod.PUT)
    public @ResponseBody Companies updCompany(@RequestBody Companies currCompany) {
        logger.info("Start updCompany");
        //Get current authentication.
        CompaniesService companiesService = null;
        try {
            companiesService = ManagementHelper.getCompaniesService();
            companiesService.updateCompany(currCompany);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
        } catch (InstanceNotFoundException ex) {
            logger.error(ex.getMessage());
        } catch (MalformedObjectNameException ex) {
            logger.error(ex.getMessage());
        } catch (CompanyNotFoundException ex) {
            logger.error(ex.getMessage());
        }
        
        return currCompany;
        
    }
    
}
