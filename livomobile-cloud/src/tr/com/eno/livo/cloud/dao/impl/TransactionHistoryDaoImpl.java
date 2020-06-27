package tr.com.eno.livo.cloud.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.eno.livo.cloud.dao.TransactionHistoryDao;
import tr.com.eno.livo.cloud.entity.TransactionHistory;
/**
 * @author arslan
 */
public class TransactionHistoryDaoImpl implements TransactionHistoryDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(TransactionHistoryDaoImpl.class);

	DataSource dataSource;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean insertTransactionHistory(TransactionHistory transactionHistory) throws SQLException {
		// TODO Auto-generated method stub
		String insertTableSQL = "INSERT INTO transaction_history(user_id ,amount, invoice_date, payment_method ,payer_id ,token ,reference_id ,promocode ) VALUES (?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(insertTableSQL);
		pstmt.setInt(1, transactionHistory.getUserId());
		pstmt.setString(2, transactionHistory.getAmount());
		pstmt.setTimestamp(3, transactionHistory.getInvoiceDate());
		pstmt.setString(4, transactionHistory.getPaymentMethod());
		pstmt.setString(5, transactionHistory.getPayerId());
		pstmt.setString(6, transactionHistory.getToken());
		pstmt.setString(7, transactionHistory.getReferenceId());
		pstmt.setInt(8, transactionHistory.getPromoCode());

		try {
			// execute insert SQL stetement
			pstmt.executeUpdate();
		} catch (Exception e) {
			System.out.println("Transaction History can not be inserted." + e.getMessage());
			LOGGER.error("Transaction History  can not be inserted.  " + e.getMessage());
			return false;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}

		return true;
	}

	@Override
	public ArrayList<TransactionHistory> getTransactionHistoryList(int userId) throws SQLException {
		// TODO Auto-generated method stub
		ArrayList<TransactionHistory> transactionHistories = new ArrayList<TransactionHistory>();

		String query = "Select * from transaction_history where user_id = ?  ORDER BY id  DESC";
		PreparedStatement pstmt = dataSource.getConnection().prepareStatement(query);
		pstmt.setInt(1, userId);

		try {
			ResultSet resultSet = pstmt.executeQuery();
			while (resultSet.next()) {
				TransactionHistory transactionHistory = new TransactionHistory();
				transactionHistory.setUserId(resultSet.getInt("user_id"));
				transactionHistory.setAmount(resultSet.getString("amount"));
				transactionHistory.setInvoiceDate(resultSet.getTimestamp("invoice_date"));
				transactionHistory.setPaymentMethod(resultSet.getString("payment_method"));
				transactionHistory.setPayerId(resultSet.getString("payer_id"));
				transactionHistory.setToken(resultSet.getString("token"));
				transactionHistory.setReferenceId(resultSet.getString("reference_id"));
				transactionHistory.setReferenceId(resultSet.getString("promocode_id"));
				transactionHistories.add(transactionHistory);

			}
		} catch (Exception e) {
			System.out.println("getUserByEmail()  query exception  : " + e.getMessage());
			LOGGER.error("getUserByEmail()  query exception  :" + e.getMessage());
			return transactionHistories;

		} finally {

			if (pstmt != null) {
				pstmt.close();
			}

		}
		return transactionHistories;
	}

}