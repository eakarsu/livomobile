/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tr.com.eno.livo.server.web.entities;

import com.livo.companies.Companies;
import java.io.Serializable;

/**
 *
 * @author Macintosh
 */
public class RCompanies extends Companies implements Serializable {

    public RCompanies(Integer recordId, String companyId, String companyTitle, Integer createUser, Long createDate, Boolean loginPerm) {
        super(recordId, companyId, companyTitle, createUser, createDate, loginPerm);
    }

    
}
