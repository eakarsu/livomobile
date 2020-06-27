package tr.com.eno.livo.cloud.entity;

import java.io.Serializable;

public class WebUser implements Serializable {

    private static final long serialVersionUID = -5103304352023604581L;
    
    private int userId;
    private String userName;
    private String userPassword;
    private String userMail;
    private Long expirationDate;
    private String companyList;

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public boolean getUserActivation() {

        return true;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public Long getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Long expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getCompanyList() {
        return companyList;
    }

    public void setCompanyList(String companyList) {
        this.companyList = companyList;
    }

}
