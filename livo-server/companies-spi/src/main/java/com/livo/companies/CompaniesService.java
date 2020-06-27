/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.livo.companies;

import java.util.Set;
import javax.management.MXBean;

/**
 *
 * @author Macintosh
 */
@MXBean
public interface CompaniesService {
    
    public Set<Companies> listCompanies();
    
    /**
     *
     * @param company
     * @param password
     * @throws InvalidCompanyException
     * @throws InvalidPasswordException
     * @throws CompanyAlreadyExistsException
     */
    public void createCompany(Companies company, String password) throws InvalidCompanyException, InvalidPasswordException, CompanyAlreadyExistsException;

    public void updateCompany(Companies company) throws CompanyNotFoundException;

    public void updateCompanyPassword(Companies company, String password) throws CompanyNotFoundException, InvalidPasswordException;

    public void deleteCompany(Integer recordId);
    
}
