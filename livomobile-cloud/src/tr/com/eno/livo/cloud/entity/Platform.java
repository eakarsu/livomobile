package tr.com.eno.livo.cloud.entity;

import org.springframework.stereotype.Component;

@Component
public class Platform {

	private int platformId;

	private int userId;
	
	private String platformType;
	
	private String platformName;
	
	private String cloudProvider;
	
	private String ipaddr;
	
	private String hostname;
	
	private String port;

	
	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public String getPlatformType() {
		return platformType;
	}

	public void setPlatformType(String platformType) {
		this.platformType = platformType;
	}

	public String getPlatformName() {
		return platformName;
	}

	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}

	public String getCloudProvider() {
		return cloudProvider;
	}

	public void setCloudProvider(String cloudProvider) {
		this.cloudProvider = cloudProvider;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
	 
}
