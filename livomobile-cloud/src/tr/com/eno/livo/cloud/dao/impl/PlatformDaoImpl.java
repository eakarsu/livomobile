package tr.com.eno.livo.cloud.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.eno.livo.cloud.dao.PlatformDao;
import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.utility.AppConstants;
/**
 * @author arslan
 */
public class PlatformDaoImpl implements PlatformDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(PlatformDaoImpl.class);

	DataSource dataSource;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public Platform getPlatformListByUser(User user) throws SQLException {

		String query = "Select * from platforms where user_id = ?  ORDER BY platform_id DESC limit 1";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
		pstmt.setInt(1, user.getUserId());
		
		Platform platform = new Platform();
		try {
			ResultSet resultSet = pstmt.executeQuery();
			if (resultSet.next()){
				platform.setUserId(resultSet.getInt("user_id"));
				platform.setPlatformId(resultSet.getInt("platform_id"));
				platform.setPlatformType(resultSet.getString("platform_type"));

				return platform;
			}else
				return null;
		} catch (Exception e) {
			System.out.println("getPlatformListByUser()  query exception  : " + e.getMessage());
			LOGGER.error("getPlatformListByUser()  query exception  :" + e.getMessage());
			return null;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
	}

	@Override
	public boolean insertPlatform(Platform platform) throws SQLException {
		// TODO Auto-generated method stub
		String insertTableSQL = "INSERT INTO PLATFORMS(user_id, platform_type ,platform_name,cloud_provider, ipaddr,hostname,port) VALUES (?,?,?,?,?,?,?)";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertTableSQL);
		pstmt.setInt(1, platform.getUserId());
		pstmt.setString(2, platform.getPlatformType());
		pstmt.setString(3, platform.getPlatformName());
		pstmt.setString(4, platform.getCloudProvider());
		pstmt.setString(5, platform.getIpaddr());
		pstmt.setString(6, platform.getHostname());
		pstmt.setString(7, platform.getPort());

		try {
			// execute insert SQL stetement
			pstmt.executeUpdate();
			
		} catch (Exception e) {
			System.out.println("Platform infos can not be inserted." + e.getMessage());
			LOGGER.error("Platform infos can not be inserted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}

		return true;

	}

	@Override
	public boolean updatePlatform(Platform platform) throws SQLException {
		// TODO Auto-generated method stub
		String updateString = "Update PLATFORMS set user_id=? , platform_type=?  ,platform_name=? ,cloud_provider=? , ipaddr=? ,hostname=? ,port=? where platform_id = ? ";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(updateString);
		try {
			pstmt.setInt(1, platform.getUserId());
			pstmt.setString(2, platform.getPlatformType());
			pstmt.setString(3, platform.getPlatformName());
			pstmt.setString(4, platform.getCloudProvider());
			pstmt.setString(5, platform.getIpaddr());
			pstmt.setString(6, platform.getHostname());
			pstmt.setString(7, platform.getPort());
			pstmt.setInt(8, platform.getPlatformId());

			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("PLATFORM can not be updated.  " + e.getMessage());
			LOGGER.error("PLATFORM can not be updated.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return true;
	}

	@Override
	public boolean deletePlatform(Platform platform) throws SQLException {
		// TODO Auto-generated method stub
		String deleteString = "DELETE FROM PLATFORMS where platform_id = ?";

		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(deleteString);
		try {
			
			pstmt.setInt(1, platform.getPlatformId()); // Platform is delete with Platform id.
			pstmt.executeUpdate();

		} catch (Exception e) {
			System.out.println("PLATFORM can not be deleted.  " + e.getMessage());
			LOGGER.error("PLATFORM can not be deleted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return true;
	}

}