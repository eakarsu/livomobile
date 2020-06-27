package tr.com.eno.livo.cloud.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.eno.livo.cloud.dao.UserDao;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.utility.AppConstants;
/**
 * @author arslan
 */
public class UserDaoImpl implements UserDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserDaoImpl.class);

	DataSource dataSource;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public User getUserByEmail(String userMail) throws SQLException {
		// TODO Auto-generated method stub
		String query = "Select * from users where email = ?  ORDER BY user_id DESC limit 1";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
		pstmt.setString(1, userMail);
	
		User user = new User();
		user.setUserId(0);
		try {
			ResultSet resultSet = pstmt.executeQuery();
			if (resultSet.next()){
				user.setUserId(resultSet.getInt("user_id"));
				user.setEmail(resultSet.getString("email"));
				user.setAccountType(resultSet.getString("account_type"));
				user.setCompanyName(resultSet.getString("company_name"));
				user.setAddressCountry(resultSet.getString("address_country"));
				user.setBillingAddress(resultSet.getString("billing_address"));
				user.setTaxNumber(resultSet.getString("tax_number"));
				user.setName(resultSet.getString("name"));
				user.setAccountState(resultSet.getInt("account_state"));
				user.setUserPassword(resultSet.getString("user_password"));

				return user;
			}else
				return user;
		} catch (Exception e) {
			System.out.println("getUserByEmail()  query exception  : " + e.getMessage());
			LOGGER.error("getUserByEmail()  query exception  :" + e.getMessage());
			return user;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
	}
 
	@Override
	public User isValidUser(String email, String userpassword, int status) throws SQLException {
		User user = new  User();
		user.setUserId(0);
		
		String query = "Select * from users where email = ? and user_password = ? and account_state= ?  ORDER BY user_id DESC limit 1";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
		pstmt.setString(1, email);
		pstmt.setString(2, userpassword);
		pstmt.setInt(3, status);
		
	
		try {
			ResultSet resultSet = pstmt.executeQuery();
			if (resultSet.next()){
				user.setUserId(resultSet.getInt("user_id"));
				user.setEmail(resultSet.getString("email"));
				user.setAccountType(resultSet.getString("account_type"));
				user.setCompanyName(resultSet.getString("company_name"));
				user.setAddressCountry(resultSet.getString("address_country"));
				user.setBillingAddress(resultSet.getString("billing_address"));
				user.setTaxNumber(resultSet.getString("tax_number"));
				user.setName(resultSet.getString("name"));
				user.setAccountState(resultSet.getInt("account_state"));
				user.setUserPassword(userpassword);

				return user;
			}else
				return user;
		} catch (Exception e) {
			System.out.println("isValidUser()  query exception  : " + e.getMessage());
			LOGGER.error("isValidUser()  query exception  :" + e.getMessage());
			return user;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
	}
	@Override
	public boolean insertUser(String name, String email) throws SQLException {

		String insertTableSQL = "INSERT INTO users(name, email ,account_state, account_type  ) VALUES (?,?,?,?)";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertTableSQL);
		pstmt.setString(1, name);
		pstmt.setString(2, email);
		pstmt.setInt(3, AppConstants.UserState.SIGNIN.value);
		pstmt.setString(4, "developer");

		try {
			// execute insert SQL stetement
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("User can not be inserted." + e.getMessage());
			LOGGER.error("User can not be inserted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}

		return true;

	}

	@Override
	public boolean updateUser(User user) throws SQLException {
		// TODO Auto-generated method stub
		String updateString = "Update USERS set user_password=? , account_state= ? ,company_name=? ,address_country=? , billing_address = ? , tax_number = ?  where user_id = ? ";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(updateString);
		try {

			pstmt.setString(1, user.getUserPassword());
			pstmt.setInt(2, user.getAccountState());
			pstmt.setString(3, user.getCompanyName());
			pstmt.setString(4, user.getAddressCountry());
			pstmt.setString(5, user.getBillingAddress());
			pstmt.setString(6, user.getTaxNumber());
			pstmt.setInt(7, user.getUserId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("User can not be updated.  " + e.getMessage());
			LOGGER.error("User can not be updated.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return true;
	}

	@Override
	public boolean updateUserStateByEmail(String email,int state) throws SQLException {
		// TODO Auto-generated method stub
		String updateString = "Update USERS set account_state= ?  where email = ? ";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(updateString);
		try {
			pstmt.setInt(1, state );
			pstmt.setString(2, email);
			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("User state can not be updated.  " + e.getMessage());
			LOGGER.error("User state can not be updated.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		System.out.println("User account_state was updated.  ");
		return true;
	}



}