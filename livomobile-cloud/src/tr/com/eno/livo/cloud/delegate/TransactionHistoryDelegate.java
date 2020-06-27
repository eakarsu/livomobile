package tr.com.eno.livo.cloud.delegate;

import java.sql.SQLException;
import java.util.ArrayList;

import tr.com.eno.livo.cloud.entity.TransactionHistory;
import tr.com.eno.livo.cloud.service.TransactionHistoryService;

public class TransactionHistoryDelegate {
	
	private TransactionHistoryService transactionHistoryService;

	public TransactionHistoryService getTransactionHistoryService() {
		return transactionHistoryService;
	}

	public void setTransactionHistoryService(TransactionHistoryService transactionHistoryService) {
		this.transactionHistoryService = transactionHistoryService;
	}

	
	public boolean insertTransactionHistory(TransactionHistory transactionHistory) throws SQLException{
		
		return transactionHistoryService.insertTransactionHistory(transactionHistory);
	}

	public ArrayList<TransactionHistory> getTransactionHistoryList(int userId) throws SQLException {

		return transactionHistoryService.getTransactionHistoryList(userId);
	}

	
}
