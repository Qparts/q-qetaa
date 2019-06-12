package q.app.qetaa.customer;

import q.app.qetaa.model.customer.PublicCustomer;

import java.io.Serializable;
import java.util.Date;

public class HitActivity implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id;
	private String sessionId;
	private Date created;
	private String ip;
	private String device;
	private String activityDes;
	private PublicCustomer customer;
	private Long visitIndex;
	
	public Long getVisitIndex() {
		return visitIndex;
	}
	public void setVisitIndex(Long visitIndex) {
		this.visitIndex = visitIndex;
	}
	public PublicCustomer getCustomer() {
		return customer;
	}
	public void setCustomer(PublicCustomer customer) {
		this.customer = customer;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	public String getActivityDes() {
		return activityDes;
	}
	public void setActivityDes(String activityDes) {
		this.activityDes = activityDes;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activityDes == null) ? 0 : activityDes.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((device == null) ? 0 : device.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + ((sessionId == null) ? 0 : sessionId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HitActivity other = (HitActivity) obj;
		if (activityDes == null) {
			if (other.activityDes != null)
				return false;
		} else if (!activityDes.equals(other.activityDes))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (customer == null) {
			if (other.customer != null)
				return false;
		} else if (!customer.equals(other.customer))
			return false;
		if (device == null) {
			if (other.device != null)
				return false;
		} else if (!device.equals(other.device))
			return false;
		if (id != other.id)
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		return true;
	}
	
	
	
	
	
	

}
