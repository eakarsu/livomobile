package tr.com.eno.livo.cloud.dao.impl;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.com.eno.livo.cloud.dao.SeatDao;
import tr.com.eno.livo.cloud.entity.Seat;
/**
 * @author arslan
 */
public class SeatDaoImpl implements SeatDao {

	private static final Logger LOGGER = LoggerFactory.getLogger(SeatDaoImpl.class);

	DataSource dataSource;

	public DataSource getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	public boolean insertSeat(Seat seat) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateSeat(Seat seat) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteSeat(Seat seat) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}



}