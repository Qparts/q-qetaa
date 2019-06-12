package q.app.qetaa.reqs;

import q.app.qetaa.helpers.AppConstants;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

@Named(value="notLoggedRequester")
@RequestScoped
public class NotLoggedRequester implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public String getSecurityHeader() {
		return "Bearer no-token && no_id && " + AppConstants.APP_SECRET + " && " + "C";// from q.app.qetaa.customer
	}
	
	public Response getSecuredRequest(String link) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, getSecurityHeader());
		Response r = b.get();// not secured
		return r;
	}

	public <T> Response postSecuredRequest(String link, T t) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, getSecurityHeader());
		Response r = b.post(Entity.entity(t, "application/json"));
		return r;
	}

 
}
