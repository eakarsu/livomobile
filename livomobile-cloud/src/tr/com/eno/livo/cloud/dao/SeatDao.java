package tr.com.eno.livo.cloud.dao;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.entity.Seat;
import tr.com.eno.livo.cloud.entity.User;

/**
 * @author arslan
 * This interface will be used to communicate with the
 * Database
 */
public interface SeatDao
{
	public boolean insertSeat(Seat seat) throws SQLException;
	
	public boolean updateSeat(Seat seat) throws SQLException;
	
	public boolean deleteSeat(Seat seat) throws SQLException;


}
