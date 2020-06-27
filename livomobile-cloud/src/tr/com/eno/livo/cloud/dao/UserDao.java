package tr.com.eno.livo.cloud.dao;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.entity.User;

/**
 * @author arslan
 * This interface will be used to communicate with the
 * Database
 */
public interface UserDao
{
	public User getUserByEmail(String userMail) throws SQLException;
	
	public User isValidUser(String username, String password, int status) throws SQLException;
	
	public boolean insertUser(String password, String email) throws SQLException;
	
	public boolean updateUser(User user) throws SQLException;
	
	public boolean updateUserStateByEmail(String email,int state) throws SQLException;


}
