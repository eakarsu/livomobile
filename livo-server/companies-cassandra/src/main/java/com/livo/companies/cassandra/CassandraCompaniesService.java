/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.companies.cassandra;

import com.livo.companies.Companies;
import com.livo.companies.CompaniesService;
import com.livo.companies.CompanyAlreadyExistsException;
import com.livo.companies.CompanyNotFoundException;
import com.livo.companies.InvalidCompanyException;
import com.livo.companies.InvalidPasswordException;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Macintosh
 */
public class CassandraCompaniesService implements CompaniesService{
    
    private static final String CASSANDRA_HOST_CONFIGURATION_KEY = "cassandra.host";
    private static final String CASSANDRA_PORT_CONFIGURATION_KEY = "cassandra.port";
    private static final Logger LOGGER = LoggerFactory.getLogger(CassandraCompaniesService.class);
    
    public void start(Map<String, Object> config) throws IOException {

        LOGGER.info("Starting Cassandra-based CompaniesService implementation...");

        String host = config.containsKey(CASSANDRA_HOST_CONFIGURATION_KEY) && config.get(CASSANDRA_HOST_CONFIGURATION_KEY) != null ? (String) config
                .get(CASSANDRA_HOST_CONFIGURATION_KEY) : "localhost";

        LOGGER.debug("Using Cassandra host '{}'...", host);

        int port = config.containsKey(CASSANDRA_PORT_CONFIGURATION_KEY) && config.get(CASSANDRA_PORT_CONFIGURATION_KEY) != null ? (Integer) config
                .get(CASSANDRA_PORT_CONFIGURATION_KEY) : 9042;

        LOGGER.debug("Using Cassandra port {}...", port);
        
        LOGGER.debug("Connecting to Cassandra...");

        CassandraHelper.connect(host, port);

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {

            @Override
            public void run() {

                CassandraHelper.disconnect();
            }
        }));

        LOGGER.info("Successfully started Cassandra-based CompaniesService implementation.");
    }

    public void stop() {

        LOGGER.info("Stopping Cassandra-based CompaniesService implementation...");
        
        CassandraHelper.disconnect();

        LOGGER.info("Successfully stopped Cassandra-based CompaniesService implementation.");
    }

    @Override
    public void createCompany(Companies company, String password) throws InvalidCompanyException, InvalidPasswordException, CompanyAlreadyExistsException {
        
        if (company == null) {

            LOGGER.debug("Passed company is null.");

            throw new NullPointerException();
        }

        if (company.getCompanyId() == null) {

            LOGGER.debug("Passed company's id is null.");

            throw new NullPointerException();
        }
        
        if (company.getCompanyTitle() == null) {

            LOGGER.debug("Passed company's title is null.");

            throw new NullPointerException();
        }
        
        this.validatePassword(password);
        
        CassandraHelper.insertCompany(company, password);
        
    }

    @Override
    public void updateCompany(Companies company) throws CompanyNotFoundException {
        
        if (company == null) {

            LOGGER.debug("Passed company is null.");

            throw new NullPointerException();
        }

        if (company.getCompanyId() == null) {

            LOGGER.debug("Passed company's id is null.");

            throw new NullPointerException();
        }
        
        if (company.getCompanyTitle() == null) {

            LOGGER.debug("Passed company's title is null.");

            throw new NullPointerException();
        }
        
        Companies currCompany = CassandraHelper.findCompany(company.getRecordId());
        
        if(currCompany == null){
            
            throw new CompanyNotFoundException();
            
        }
        
        CassandraHelper.updateCompany(company);
        
    }

    @Override
    public void updateCompanyPassword(Companies company, String password) throws CompanyNotFoundException, InvalidPasswordException {
        
        if (company == null) {

            LOGGER.debug("Passed company is null.");

            throw new NullPointerException();
        }

        if (company.getCompanyId() == null) {

            LOGGER.debug("Passed company's id is null.");

            throw new NullPointerException();
        }
        
        if (company.getCompanyTitle() == null) {

            LOGGER.debug("Passed company's title is null.");

            throw new NullPointerException();
        }
        
        Companies currCompany = CassandraHelper.findCompany(company.getRecordId());
        
        if(currCompany == null){
            
            throw new CompanyNotFoundException();
            
        }
        
        this.validatePassword(password);

        LOGGER.debug("Updating password for company '{}'...", company.getCompanyId());

        CassandraHelper.updateCompanyPassword(company, password);
        
    }

    @Override
    public void deleteCompany(Integer recordId) {

        if (recordId == null) {

            LOGGER.debug("Passed company's recordId is null.");

            throw new NullPointerException();
        }
        
        CassandraHelper.deleteCompany(recordId);
        
    }
    
    private void validatePassword(String password) throws InvalidPasswordException {

        if (password == null || password.trim().isEmpty()) {

            LOGGER.debug("Password is null or empty.");

            throw new InvalidPasswordException("Password cannot be empty.");
        }

        String trimmedPassword = password.trim();
        
        if (trimmedPassword.length() < 4) {
            
            throw new InvalidPasswordException("Password must be at least 4 characters long.");
        }
    }

    @Override
    public Set<Companies> listCompanies() {
        
        LOGGER.debug("listCompanies");
        return CassandraHelper.listCompanies();
        
    }
    
}
