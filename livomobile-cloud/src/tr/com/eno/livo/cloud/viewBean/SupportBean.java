package tr.com.eno.livo.cloud.viewBean;

public class SupportBean {
	
private int supportId;
	
	private String userEmail;

	private String title;
	
	private String description;
	
	private int userId;
	
	int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getSupportId() {
		return supportId;
	}

	public void setSupportId(int supportId) {
		this.supportId = supportId;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	 
}
