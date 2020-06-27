package tr.com.eno.livo.cloud.delegate;

import java.sql.SQLException;
import java.util.List;

import tr.com.eno.livo.cloud.entity.Support;
import tr.com.eno.livo.cloud.service.SupportService;

public class SupportDelegate {
	private SupportService supportService;

	public SupportService getSupportService() {
		return supportService;
	}

	public void setSupportService(SupportService supportService) {
		this.supportService = supportService;
	}

	public List<Support> findAllSupport() throws SQLException{
		return this.supportService.findAllSupport();
	}
	
    public List<Support> findSupportByEmail(String userEmail) throws SQLException{
    	return this.supportService.findSupportByEmail(userEmail);
    }
    
    public Support findSupport(int supportId) throws SQLException{
    	return this.supportService.findSupport(supportId);
    }

	public boolean insertSupport(String email, String title, String desc, int userId) throws SQLException{
		return this.supportService.insertSupport(email, title, desc, userId);
	}
	
	public boolean updateSupport(Support supp) throws SQLException{
		return this.supportService.updateSupport(supp);
	}
	
}
