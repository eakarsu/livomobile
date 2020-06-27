package tr.com.eno.livo.cloud.entity;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class User {

	String user;
	
	private int userId;

	private String name;
	
	private String userPassword;
	
	private String email;
	
	private int accountState;
	
	private String accountType;
	
	private String companyName;
	
	private String addressCountry;
	
	private String billingAddress;
	
	private String taxNumber;
	
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUserPassword() {
		return userPassword;
	}
	
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public int getAccountState() {
		return accountState;
	}
	
	public void setAccountState(int accountState) {
		this.accountState = accountState;
	}
	
	public String getAccountType() {
		return accountType;
	}
	
	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getAddressCountry() {
		return addressCountry;
	}
	
	public void setAddressCountry(String addressCountry) {
		this.addressCountry = addressCountry;
	}
	
	public String getBillingAddress() {
		return billingAddress;
	}
	
	public void setBillingAddress(String billingAddress) {
		this.billingAddress = billingAddress;
	}
	
	public String getTaxNumber() {
		return taxNumber;
	}
	
	public void setTaxNumber(String taxNumber) {
		this.taxNumber = taxNumber;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
