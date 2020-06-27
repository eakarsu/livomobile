package tr.com.eno.livo.cloud.dao;

import java.sql.SQLException;
import java.util.List;

import tr.com.eno.livo.cloud.entity.Support;

/**
 * @author arslan
 * This interface will be used to communicate with the
 * Database
 */
public interface SupportDao
{

	public List<Support> findAllSupport() throws SQLException;
	
    public List<Support> findSupportByEmail(String userEmail) throws SQLException;
    
    public Support findSupport(int supportId) throws SQLException;

	public boolean insertSupport(String email, String title, String desc, int userId) throws SQLException;
	
	public boolean updateSupport(Support supp) throws SQLException;

}
