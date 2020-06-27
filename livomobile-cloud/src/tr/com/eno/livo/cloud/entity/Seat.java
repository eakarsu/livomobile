package tr.com.eno.livo.cloud.entity;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

@Component
public class Seat {

	private int seatId;

	private int platformId;

	private int transactionHistoryId;

	private Timestamp startDate;

	private Timestamp endDate;

	private int deploymentStatus;

	public int getSeatId() {
		return seatId;
	}

	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}

	public int getPlatformId() {
		return platformId;
	}

	public void setPlatformId(int platformId) {
		this.platformId = platformId;
	}

	public int getTransactionHistoryId() {
		return transactionHistoryId;
	}

	public void setTransactionHistoryId(int transactionHistoryId) {
		this.transactionHistoryId = transactionHistoryId;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public int getDeploymentStatus() {
		return deploymentStatus;
	}

	public void setDeploymentStatus(int deploymentStatus) {
		this.deploymentStatus = deploymentStatus;
	}

}
