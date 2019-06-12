package q.app.qetaa.beans;

import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.model.cart.Bank;
import q.app.qetaa.reqs.Requester;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;


@Named(value="bankBean")
@SessionScoped
public class BankBean implements Serializable{

	private static final long serialVersionUID = 1L;
	private List<Bank> banks;
	@Inject
	private Requester reqs;
	
	@PostConstruct
	private void init() {
		initBanks();
	}
	
	private void initBanks() {
		banks = new ArrayList<>();
		Response r = reqs.getSecuredRequest(AppConstants.GET_BANKS);
		if(r.getStatus() == 200) {
			this.banks= r.readEntity(new GenericType<List<Bank>>() {});
		}
	}
	

	public List<Bank> getBanks() {
		return banks;
	}

	public void setBanks(List<Bank> banks) {
		this.banks = banks;
	}


}
