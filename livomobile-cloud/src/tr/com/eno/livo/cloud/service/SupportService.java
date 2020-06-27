/**
 *
 */
package tr.com.eno.livo.cloud.service;

import java.sql.SQLException;
import java.util.List;

import tr.com.eno.livo.cloud.entity.Support;

/**
 * @author ARSLAN
 *
 */
public interface SupportService
{

	public List<Support> findAllSupport() throws SQLException;
	
    public List<Support> findSupportByEmail(String userEmail) throws SQLException;
    
    public Support findSupport(int supportId) throws SQLException;

	public boolean insertSupport(String email, String title, String desc, int userId) throws SQLException;
	
	public boolean updateSupport(Support supp) throws SQLException;
		
}
