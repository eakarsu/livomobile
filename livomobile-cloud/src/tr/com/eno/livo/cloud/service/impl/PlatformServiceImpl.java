package tr.com.eno.livo.cloud.service.impl;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.dao.PlatformDao;
import tr.com.eno.livo.cloud.entity.Platform;
import tr.com.eno.livo.cloud.entity.User;
import tr.com.eno.livo.cloud.service.PlatformService;

public class PlatformServiceImpl implements PlatformService
{

		private PlatformDao platformDao;

		public PlatformDao getPlatformDao()
		{
				return this.platformDao;
		}

		public void setPlatformDao(PlatformDao userDao)
		{
				this.platformDao = userDao;
		}

		@Override
		public Platform getPlatformListByUser(User user) throws SQLException {
			// TODO Auto-generated method stub
			return platformDao.getPlatformListByUser(user);
		}

		@Override
		public boolean insertPlatform(Platform platform) throws SQLException {
			// TODO Auto-generated method stub
			return platformDao.insertPlatform(platform);
		}

		@Override
		public boolean updatePlatform(Platform platform) throws SQLException {
			// TODO Auto-generated method stub
			return platformDao.updatePlatform(platform);
		}

		@Override
		public boolean deletePlatform(Platform platform) throws SQLException {
			// TODO Auto-generated method stub
			return platformDao.deletePlatform(platform);
		}


}
