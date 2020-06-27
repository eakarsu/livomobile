package tr.com.eno.livo.cloud.delegate;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.service.PlatformService;

public class PlatformDelegate {

	private PlatformService platformService;

	public PlatformService getPlatformService() {
		return this.platformService;
	}

	public void setPlatformService(PlatformService userService) {
		this.platformService = userService;
	}

	public Platform getPlatformListByUser(User user) throws SQLException {
		return platformService.getPlatformListByUser(user);
	}

	public boolean insertPlatform(Platform platform) throws SQLException {
		return platformService.insertPlatform(platform);
	}

	public boolean updatePlatform(Platform platform) throws SQLException {
		return platformService.updatePlatform(platform);
	}
	
	public boolean deletePlatform(Platform platform) throws SQLException {
		return platformService.deletePlatform(platform);
	}
}
