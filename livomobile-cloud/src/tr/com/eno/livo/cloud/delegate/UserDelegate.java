package tr.com.eno.livo.cloud.delegate;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.service.UserService;

public class UserDelegate {
	private UserService userService;

	public UserService getUserService() {
		return this.userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public User getUserByEmail(String userMail ) throws SQLException {
		return userService.getUserByEmail(userMail);
	}
	
	public User isValidUser(String username, String password, int status) throws SQLException {
		return userService.isValidUser(username, password, status);
	}

	public boolean insertUser(String name, String email) throws SQLException {
		return userService.insertUser(name, email);
	}
	
	public boolean updateUser(User user) throws SQLException {
		return userService.updateUser(user);
	}

	public boolean updateUserStateByEmail(String email,int state) throws SQLException {
		return userService.updateUserStateByEmail(email,state);
	}
	
}
