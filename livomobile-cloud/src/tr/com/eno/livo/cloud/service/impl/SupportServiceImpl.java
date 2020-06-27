package tr.com.eno.livo.cloud.service.impl;

import java.sql.SQLException;
import java.util.List;

import tr.com.eno.livo.cloud.dao.SupportDao;
import tr.com.eno.livo.cloud.entity.Support;
import tr.com.eno.livo.cloud.service.SupportService;

public class SupportServiceImpl implements SupportService
{

		private SupportDao supportDao;

		public SupportDao getSupportDao() {
			return supportDao;
		}

		public void setSupportDao(SupportDao supportDao) {
			this.supportDao = supportDao;
		}

		@Override
		public List<Support> findSupportByEmail(String userMail) throws SQLException {
			// TODO Auto-generated method stub
			return  supportDao.findSupportByEmail(userMail);
		}
		
		@Override
		public List<Support> findAllSupport() throws SQLException
		{
				return supportDao.findAllSupport();
		}

		@Override
		public Support findSupport(int supportId) throws SQLException {
			// TODO Auto-generated method stub
			return supportDao.findSupport(supportId);
		}

		@Override
		public boolean insertSupport(String email, String title, String desc, int userId) throws SQLException {
			// TODO Auto-generated method stub
			return supportDao.insertSupport(email, title, desc, userId);
		}

		@Override
		public boolean updateSupport(Support supp) throws SQLException {
			// TODO Auto-generated method stub
			return supportDao.updateSupport(supp);
		}


}
