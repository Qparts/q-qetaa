package q.app.qetaa.beans;

import java.io.Serializable;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import q.app.qetaa.beans.LoginBean;
import q.app.qetaa.customer.HitActivity;
import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.model.customer.PublicCustomer;
import q.app.qetaa.reqs.NotLoggedRequester;

@Named(value = "activityMonitorBean")
@SessionScoped
public class ActivityMonitorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<HitActivity> activities;

	@Inject
	private NotLoggedRequester reqs;

	private String os;
	private String ip;
	private String sessionId;
	private String header;
	private Long visitIndex;

	@Inject
	private LoginBean loginBean;

	@PostConstruct
	private void init() {
		activities = new ArrayList<>();
		header = reqs.getSecurityHeader();
		this.initOS();
		this.initIp();
		this.initSessionId();
		this.initVisitIndex();
		this.addVisitCounter();
	}
	
	private void initVisitIndex() {
		/*
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, Object> map = context.getRequestCookieMap();
		Object vio = map.get("visitIndex");
		if(null == vio) {
			Response r = reqs.getSecuredRequest(AppConstants.GET_NEW_VISIT_INDEX);
			if (r.getStatus() == 200) {
				this.visitIndex = r.readEntity(Long.class);
				saveVisitIndexCookie();
			} else {
				System.out.println("could not get visit index");
			}
		}
		else {
			Cookie cookie = (Cookie) vio;
			this.visitIndex = Long.valueOf((String)cookie.getValue());
			System.out.println("retreived cookie index : " + visitIndex);
		}
		*/
	}
	
	private void saveVisitIndexCookie() {
		/*
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		Cookie cookie = new Cookie("visitIndex", visitIndex.toString());
		cookie.setMaxAge(60* 60 * 24 * 365 * 10);
		
		 HttpServletResponse response = (HttpServletResponse) context.getResponse();
		 response.addCookie(cookie);
		 */
	}

	private void initSessionId() {
		/*
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
		sessionId = session.getId();
		*/
	}

	private void initIp() {
		/*
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext()
				.getRequest();
		ip = request.getHeader("X-FORWARDED-FOR");
		if (ip != null) {
			// cares only about the first IP if there is a list
			ip = ip.replaceFirst(",.*", "");
		} else {
			ip = request.getRemoteAddr();
		}
		*/
	}

	private void initOS() {
		/*
		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		String userAgent = request.getHeader("user-agent");
		// =============OS
		if (userAgent.toLowerCase().contains("windows")) {
			os = "Windows";
		} else if (userAgent.toLowerCase().contains("mac")) {
			os = "Mac";
		} else if (userAgent.toLowerCase().contains("x11")) {
			os = "Unix";
		} else if (userAgent.toLowerCase().contains("android")) {
			os = "Android";
		} else if (userAgent.toLowerCase().contains("iphone") || userAgent.toLowerCase().contains("ipad")) {
			os = "iOS";
		} else {
			os = "UnKnown";
		}
		*/

	}

	public void addToActivity(String desc) {
		/*
		HitActivity hit = new HitActivity();
		hit.setActivityDes(desc);
		hit.setCreated(new Date());
		hit.setDevice(os);
		hit.setIp(ip);
		hit.setVisitIndex(visitIndex);
		hit.setSessionId(sessionId);
		this.activities.add(hit);
		*/
	}

	public void initCustomer() {
		/*
		if (null != loginBean.getLoginObject().getCustomer()) {
			header = reqs.getSecurityHeader();
			for (HitActivity hit : activities) {
				hit.setCustomer(loginBean.getLoginObject().getCustomer());
			}
		}
		*/
	}

	private void preDestroyInitCustomer() {
		/*
		PublicCustomer customer = null;
		for (HitActivity hi : activities) {
			if (null != hi.getCustomer()) {
				customer = hi.getCustomer();
				break;
			}
		}
		if (null != customer) {
			for (HitActivity hi : activities) {
				hi.setCustomer(customer);
			}
		}
		*/
	}

	private void addVisitCounter() {
		/*
		Map<String, String> map = new HashMap<String, String>();
		map.put("ip", ip);
		map.put("device", os);
		Response r = reqs.postSecuredRequest(AppConstants.POST_HIT_COUNTER, map);
		if (r.getStatus() == 201) {

		} else {

		}
		*/
	}

	@PreDestroy
	private void destroy() {
		/*
		preDestroyInitCustomer();
		if (activities.size() > 1) {
			Response r = reqs.postSecuredRequest(AppConstants.POST_HIT_ACTIVITIES, activities);
			if (r.getStatus() == 201) {
				
			} else {
				System.out.println("could not post activites");
			}
		}
		*/
	}

	public List<HitActivity> getActivities() {
		return activities;
	}

	public void setActivities(List<HitActivity> activities) {
		this.activities = activities;
	}

}
