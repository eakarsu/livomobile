package tr.com.eno.livo.cloud.service.impl;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.dao.PendingRequestDao;
import tr.com.eno.livo.cloud.service.PendingRequestService;

public class PendingRequestServiceImpl implements PendingRequestService
{

		private PendingRequestDao pendingRequestDao;

		public PendingRequestDao getPendingRequestDao() {
			return pendingRequestDao;
		}

		public void setPendingRequestDao(PendingRequestDao pendingRequestDao) {
			this.pendingRequestDao = pendingRequestDao;
		}

		@Override
		public boolean insertToken(String email, String token) throws SQLException {
			// TODO Auto-generated method stub
			return pendingRequestDao.insertToken(email, token);
		}

		@Override
		public boolean isValidToken(String email, String token) throws SQLException {
			// TODO Auto-generated method stub
			return pendingRequestDao.isValidToken(email, token);
		}
		
		@Override
		public boolean deleteToken(String email, String token) throws SQLException {
			// TODO Auto-generated method stub
			return pendingRequestDao.deleteToken(email, token);
		}

		@Override
		public boolean updateStatus(String email, int status) throws SQLException {
			// TODO Auto-generated method stub
			return pendingRequestDao.updateTokenStatus(email, status);
		}


}
