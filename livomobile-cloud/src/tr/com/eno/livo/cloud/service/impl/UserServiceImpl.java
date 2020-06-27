package tr.com.eno.livo.cloud.service.impl;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.dao.UserDao;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.service.UserService;

public class UserServiceImpl implements UserService
{

		private UserDao userDao;

		public UserDao getUserDao()
		{
				return this.userDao;
		}

		public void setUserDao(UserDao userDao)
		{
				this.userDao = userDao;
		}

		@Override
		public User getUserByEmail(String userMail) throws SQLException {
			// TODO Auto-generated method stub
			return  userDao.getUserByEmail(userMail);
		}
		
		@Override
		public User isValidUser(String username, String password, int status) throws SQLException
		{
				return userDao.isValidUser(username, password,status);
		}

		@Override
		public boolean insertUser(String password, String email) throws SQLException {
			// TODO Auto-generated method stub
			return userDao.insertUser(password, email);
		}

		@Override
		public boolean updateUser(User user) throws SQLException {
			// TODO Auto-generated method stub
			return userDao.updateUser(user);
		}

		@Override
		public boolean updateUserStateByEmail(String email,int state) throws SQLException {
			// TODO Auto-generated method stub
			return userDao.updateUserStateByEmail(email, state);
		}


}
