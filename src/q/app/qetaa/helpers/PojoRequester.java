package q.app.qetaa.helpers;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

public class PojoRequester {
	
	public static Response getSecuredRequest(String link, String header) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, header);
		Response r = b.get();
		return r;
	}

	public static <T> Response postSecuredRequest(String link, T t, String header) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, header);
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}
	
	public static <T> Response putSecuredRequest(String link, T t, String header) {
		Builder b = ClientBuilder.newClient().target(link).request();
		b.header(HttpHeaders.AUTHORIZATION, header);
		Response r = b.put(Entity.entity(t, "application/json"));// not secured
		return r;
	}

}
