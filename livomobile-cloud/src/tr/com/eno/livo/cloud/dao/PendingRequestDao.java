package tr.com.eno.livo.cloud.dao;

import java.sql.SQLException;

/**
 * @author arslan
 * This interface will be used to communicate with the
 * Database
 */
public interface PendingRequestDao
{
	
	public boolean insertToken(String email, String token) throws SQLException;
	
	public boolean isValidToken(String email, String token) throws SQLException;

	public boolean updateTokenStatus(String email, int status) throws SQLException;

	public boolean deleteToken(String email, String token) throws SQLException;

}
