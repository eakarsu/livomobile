package tr.com.eno.livo.cloud.dao;

import java.sql.SQLException;
import java.util.ArrayList;

import tr.com.eno.livo.cloud.entity.TransactionHistory;

/**
 * @author arslan
 * This interface will be used to communicate with the
 * Database
 */
public interface TransactionHistoryDao
{
	public boolean insertTransactionHistory(TransactionHistory transactionHistory) throws SQLException;

	public ArrayList<TransactionHistory> getTransactionHistoryList(int userId) throws SQLException;

}
