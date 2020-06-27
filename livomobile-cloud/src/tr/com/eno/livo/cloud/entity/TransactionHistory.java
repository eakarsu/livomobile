package tr.com.eno.livo.cloud.entity;

import java.sql.Date;
import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class TransactionHistory {

	private int transactionHistoryId;

	private int userId;
	
	private String amount;
	
	private Timestamp invoiceDate;
	
	private String paymentMethod;
	
	private String payerId;
	
	private String token;
	
	private int planId;
	
	private String referenceId;

	private int promoCode;

	public int getTransactionHistoryId() {
		return transactionHistoryId;
	}

	public void setTransactionHistoryId(int transactionHistoryId) {
		this.transactionHistoryId = transactionHistoryId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Timestamp getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(Timestamp timestamp) {
		this.invoiceDate = timestamp;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public int getPromoCode() {
		return promoCode;
	}

	public void setPromoCodeId(int promoCodeId) {
		this.promoCode = promoCodeId;
	}

 
	 
}
