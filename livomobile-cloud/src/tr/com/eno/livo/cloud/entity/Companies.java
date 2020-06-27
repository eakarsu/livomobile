package tr.com.eno.livo.cloud.entity;

import java.beans.ConstructorProperties;
import java.io.Serializable;
import java.util.Objects;

public class Companies implements Serializable, Comparable<Companies> {
    
    private final Integer recordId;
    private final String companyId;
    private final String companyTitle;
    private final Integer createUser;
    private final Long createDate;
    private String formatedDate;
    private String password;
    private final Boolean loginPerm;

    @ConstructorProperties({"recordId", "companyId", "companyTitle", "createUser", "createDate", "loginPerm"})
    public Companies(Integer recordId, String companyId, String companyTitle, Integer createUser, Long createDate, Boolean loginPerm) {
        this.recordId = recordId;
        this.companyId = companyId;
        this.companyTitle = companyTitle;
        this.createUser = createUser;
        this.createDate = createDate;
        this.loginPerm = loginPerm;
    }

    public Integer getRecordId() {
        return recordId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public String getCompanyTitle() {
        return companyTitle;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public Long getCreateDate() {
        return createDate;
    }

    public Boolean getLoginPerm() {
        return loginPerm;
    }

    public String getFormatedDate() {
        return formatedDate;
    }

    public void setFormatedDate(String formatedDate) {
        this.formatedDate = formatedDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    @Override
    public int compareTo(Companies o) {

        if (o == null) {
            throw new NullPointerException();
        }

        if (this.recordId == null) {
            throw new NullPointerException();
        }

        if (this.companyId == null) {
            throw new NullPointerException();
        }

        int companyIdComparison = this.companyId.compareTo(o.companyId);
        int recordIdComparison = this.recordId.compareTo(o.recordId);
        
        return companyIdComparison == 0 ? recordIdComparison : companyIdComparison;
    }
    
}
