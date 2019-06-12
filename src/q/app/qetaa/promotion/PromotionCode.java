package q.app.qetaa.promotion;

import java.io.Serializable;
import java.util.Date;

public class PromotionCode implements Serializable{
	private static final long serialVersionUID = 1L;
	private int id;
	private Date created;
	private Date expire;
	private String code;
	private PromotionProvider provider;
	private Date used;
	private Long cartId;
	private Long customerId;
	private boolean reusable;
	private boolean discountPromo;
	private Double discountPercentage;

	public Date getUsed() {
		return used;
	}

	public void setUsed(Date used) {
		this.used = used;
	}

	public Long getCartId() {
		return cartId;
	}

	public void setCartId(Long cartId) {
		this.cartId = cartId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getExpire() {
		return expire;
	}

	public void setExpire(Date expire) {
		this.expire = expire;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public PromotionProvider getProvider() {
		return provider;
	}

	public void setProvider(PromotionProvider provider) {
		this.provider = provider;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + ((created == null) ? 0 : created.hashCode());
		result = prime * result + ((expire == null) ? 0 : expire.hashCode());
		result = prime * result + id;
		result = prime * result + ((provider == null) ? 0 : provider.hashCode());
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
		PromotionCode other = (PromotionCode) obj;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (created == null) {
			if (other.created != null)
				return false;
		} else if (!created.equals(other.created))
			return false;
		if (expire == null) {
			if (other.expire != null)
				return false;
		} else if (!expire.equals(other.expire))
			return false;
		if (id != other.id)
			return false;
		if (provider == null) {
			if (other.provider != null)
				return false;
		} else if (!provider.equals(other.provider))
			return false;
		return true;
	}

	public boolean isReusable() {
		return reusable;
	}

	public void setReusable(boolean reusable) {
		this.reusable = reusable;
	}

	public boolean isDiscountPromo() {
		return discountPromo;
	}

	public void setDiscountPromo(boolean discountPromo) {
		this.discountPromo = discountPromo;
	}

	public Double getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(Double discountPercentage) {
		this.discountPercentage = discountPercentage;
	}
	
	
	
	
	
}
