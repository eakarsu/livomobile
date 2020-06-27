package tr.com.eno.livo.cloud.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.eno.livo.cloud.dao.PendingRequestDao;
import tr.com.eno.livo.cloud.dao.UserDao;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.utility.AppConstants;
/**
 * @author arslan
 */
public class PendingRequestDaoImpl implements PendingRequestDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(PendingRequestDaoImpl.class);

	DataSource dataSource;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean insertToken(String email, String token) throws SQLException {

		String insertTableSQL = "INSERT INTO pending_requests(email,token,status) VALUES (?,?,?)";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertTableSQL);
		pstmt.setString(1, email);
		pstmt.setString(2, token);
		pstmt.setInt(3, 1);

		try {
			// execute insert SQL stetement
			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("Pending_requests can not be inserted.  " + e.getMessage());
			LOGGER.error("Pending_requests can not be inserted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}
 
		}

		return true;
	}

	@Override
	public boolean isValidToken(String email, String token) throws SQLException {
		// TODO Auto-generated method stub
		String isValidTokenQuery = "Select * from pending_requests where email = ? and token = ? and status = ? ORDER BY req_id desc limit 1";
		boolean isValidToken = false;

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(isValidTokenQuery);
		try {
			pstmt.setString(1, email);
			pstmt.setString(2, token);
			pstmt.setInt(3, 1); // Token status active = 1
			// pstmt.setTimestamp(4, new Timestamp(milis), AppConstants.tzUTC);
			ResultSet resultSet = pstmt.executeQuery();

			if (resultSet.next()) {
				Timestamp tokenExpireDate = resultSet.getTimestamp("token_expire_date");
				Date date = new Date();
				Timestamp currentTimestamp = new Timestamp(date.getTime());

				if (currentTimestamp.getTime() - tokenExpireDate.getTime() > 0) {

					System.out.println("Token expire date is expired.");
					isValidToken = false;

					deleteToken(email, token);

					return isValidToken;

				} else {

					isValidToken = true;
				}
			} else {
				isValidToken = false;
			}

			if (isValidToken) {

				// boolean updateToken = updateTokenStatus(email, token, 2);
				boolean deleteToken = deleteToken(email, token);

				if (deleteToken) {
					System.out.println("Token was deleted succesfull.");
					LOGGER.debug("Token was deleted succesfull.");

				} else {
					System.out.println("Token can not be deleted.");
					LOGGER.debug("Token can not be deleted.");

				}
				pstmt.close();

			}

		} catch (Exception e) {
			System.out.println("isValidToken() exception : " + e.getMessage());
			LOGGER.error("isValidToken() exception : " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return isValidToken;
	}

	@Override
	public boolean updateTokenStatus(String email, int status) throws SQLException {
		// TODO Auto-generated method stub
		String updateString = "Update pending_requests set status= ?  where email = ?";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(updateString);
		try {
			pstmt.setInt(1, status); // Token status used = 2
			pstmt.setString(2, email);
			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("Token status can not be updated.  " + e.getMessage());
			LOGGER.error("Token status can not be updated.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return true;
	}

	@Override
	public boolean deleteToken(String email, String token) throws SQLException {
		// TODO Auto-generated method stub
		String deleteSQL = "DELETE FROM pending_requests where email = ? and token = ? and status = ?";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(deleteSQL);
		try {
			pstmt.setString(1, email);
			pstmt.setString(2, token);
			pstmt.setInt(3, 1); // Token status
			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("Pending_requests can not be deleted.  " + e.getMessage());
			LOGGER.error("Pending_requests can not be deleted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return true;
	}

}