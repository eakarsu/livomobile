package tr.com.eno.livo.cloud.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;

import tr.com.eno.livo.cloud.dao.TransactionHistoryDao;
import tr.com.eno.livo.cloud.entity.TransactionHistory;
import tr.com.eno.livo.cloud.service.TransactionHistoryService;

public class TransactionHistoryServiceImpl implements TransactionHistoryService
{

		private TransactionHistoryDao transactionHistoryDao;
		 

		public TransactionHistoryDao getTransactionHistoryDao() {
			return transactionHistoryDao;
		}

		public void setTransactionHistoryDao(TransactionHistoryDao transactionHistoryDao) {
			this.transactionHistoryDao = transactionHistoryDao;
		}

		@Override
		public boolean insertTransactionHistory(TransactionHistory transactionHistory) throws SQLException {
			// TODO Auto-generated method stub
			return transactionHistoryDao.insertTransactionHistory(transactionHistory);
		}

		@Override
		public ArrayList<TransactionHistory> getTransactionHistoryList(int userId) throws SQLException {
			// TODO Auto-generated method stub
			return transactionHistoryDao.getTransactionHistoryList(userId);
		}


}
