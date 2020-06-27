package tr.com.eno.livo.cloud.dao;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.User;

/**
 * @author arslan
 * This interface will be used to communicate with the
 * Database
 */
public interface PlatformDao
{
	public Platform getPlatformListByUser(User user) throws SQLException;
	
	public boolean insertPlatform(Platform platform) throws SQLException;
	
	public boolean updatePlatform(Platform platform) throws SQLException;

	public boolean deletePlatform(Platform platform) throws SQLException;
 

}
