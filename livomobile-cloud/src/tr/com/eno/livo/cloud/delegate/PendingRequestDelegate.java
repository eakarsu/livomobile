package tr.com.eno.livo.cloud.delegate;

import java.sql.SQLException;

import tr.com.eno.livo.cloud.service.PendingRequestService;

public class PendingRequestDelegate {
	
	private PendingRequestService pendingRequestService;

	public PendingRequestService getPendingRequestService() {
		return pendingRequestService;
	}

	public void setPendingRequestService(PendingRequestService pendingRequestService) {
		this.pendingRequestService = pendingRequestService;
	}

	public boolean insertToken(String email, String token) throws SQLException {
		return pendingRequestService.insertToken(email, token);
	}
	
	public boolean updateStatus(String email, int status) throws SQLException {
		return pendingRequestService.updateStatus(email, status);
	}

	public boolean isValidToken(String email, String token) throws SQLException {
		return pendingRequestService.isValidToken(email, token);
	}
	
	public boolean deleteToken(String email, String token) throws SQLException {
		return pendingRequestService.deleteToken(email, token);
	}

}
