/**
 *
 */
package tr.com.eno.livo.cloud.service;

import java.sql.SQLException;
import java.util.ArrayList;

import tr.com.eno.livo.cloud.entity.TransactionHistory;


/**
 * @author ARSLAN
 *
 */
public interface TransactionHistoryService
{
	
	public boolean insertTransactionHistory(TransactionHistory transactionHistory) throws SQLException;

	public ArrayList<TransactionHistory> getTransactionHistoryList(int userId) throws SQLException;
		
}
