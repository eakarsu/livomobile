package tr.com.eno.livo.cloud.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.eno.livo.cloud.dao.SupportDao;
import tr.com.eno.livo.cloud.entity.Support;
import tr.com.eno.livo.cloud.utility.AppConstants;
/**
 * @author arslan
 */
public class SupportDaoImpl implements SupportDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(SupportDaoImpl.class);

	DataSource dataSource;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public List<Support> findSupportByEmail(String userEmail) throws SQLException {
		// TODO Auto-generated method stub
		String query = "Select * from supports where email = ?  ORDER BY support_id DESC";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
		pstmt.setString(1, userEmail);
	
		List<Support> supportList = new ArrayList<>();
		try {
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()){
				Support support = new Support();
				support.setSupportId(resultSet.getInt("support_id"));
				support.setTitle(resultSet.getString("title"));
				support.setDescription(resultSet.getString("description"));
				support.setUserEmail(resultSet.getString("user_email"));
				support.setUserId(resultSet.getInt("user_id"));
				support.setCreateDate(resultSet.getTimestamp("create_date"));

				supportList.add(support);
			}
		} catch (Exception e) {
			System.out.println("getUserByEmail()  query exception  : " + e.getMessage());
			LOGGER.error("getUserByEmail()  query exception  :" + e.getMessage());

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		
		return supportList;
	}
	
	@Override
	public Support findSupport(int supportId) throws SQLException {
		// TODO Auto-generated method stub
		String query = "Select * from supports where support_id = ?  ORDER BY support_id DESC";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
		pstmt.setInt(1, supportId);
	
		Support support = new Support();
		try {
			ResultSet resultSet = pstmt.executeQuery();
			if (resultSet.next()){
				support.setSupportId(resultSet.getInt("support_id"));
				support.setTitle(resultSet.getString("title"));
				support.setDescription(resultSet.getString("description"));
				support.setUserEmail(resultSet.getString("user_email"));
				support.setUserId(resultSet.getInt("user_id"));
				support.setCreateDate(resultSet.getTimestamp("create_date"));
			}
		} catch (Exception e) {
			System.out.println("getUserByEmail()  query exception  : " + e.getMessage());
			LOGGER.error("getUserByEmail()  query exception  :" + e.getMessage());

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		
		return support;
	}
 
	@Override
	public List<Support> findAllSupport() throws SQLException {
		String query = "Select * from supports ORDER BY support_id DESC";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
	
		List<Support> supportList = new ArrayList<>();
		try {
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()){
				Support support = new Support();
				support.setSupportId(resultSet.getInt("support_id"));
				support.setTitle(resultSet.getString("title"));
				support.setDescription(resultSet.getString("description"));
				support.setUserEmail(resultSet.getString("user_email"));
				support.setUserId(resultSet.getInt("user_id"));
				support.setCreateDate(resultSet.getTimestamp("create_date"));

				supportList.add(support);
			}
		} catch (Exception e) {
			System.out.println("getUserByEmail()  query exception  : " + e.getMessage());
			LOGGER.error("getUserByEmail()  query exception  :" + e.getMessage());

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		
		return supportList;
	}
	
	@Override
	public boolean insertSupport(String email, String title, String desc, int userId) throws SQLException {

		String insertTableSQL = "INSERT INTO supports( user_email, title , description, user_id, create_date ) VALUES (?,?,?,?,?)";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertTableSQL);
		pstmt.setString(1, email);
		pstmt.setString(2, title);
		pstmt.setString(3, desc);
		pstmt.setInt(4, userId);
		pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));

		try {
			// execute insert SQL stetement
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("Support can not be inserted." + e.getMessage());
			LOGGER.error("Support can not be inserted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}

		return true;

	}

	@Override
	public boolean updateSupport(Support supp) throws SQLException {
		// TODO Auto-generated method stub
		String updateString = "Update supports set title=? , description= ? ,user_id=? ,user_email=? , create_date = ?  where support_id = ? ";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(updateString);
		try {

			pstmt.setString(1, supp.getTitle());
			pstmt.setString(2, supp.getDescription());
			pstmt.setInt(3, supp.getUserId());
			pstmt.setString(4, supp.getUserEmail());
			pstmt.setTimestamp(5, supp.getCreateDate());
			pstmt.setInt(6, supp.getSupportId());

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



}