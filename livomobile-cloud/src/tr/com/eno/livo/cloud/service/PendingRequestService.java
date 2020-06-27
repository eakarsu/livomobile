/**
 *
 */
package tr.com.eno.livo.cloud.service;

import java.sql.SQLException;

/**
 * @author ARSLAN
 *
 */
public interface PendingRequestService
{
		public boolean insertToken(String email, String token) throws SQLException;
		
		public boolean isValidToken(String email, String token) throws SQLException;
		
		public boolean deleteToken(String email, String token) throws SQLException;
		
		public boolean updateStatus(String email, int status) throws SQLException;
		
}
