package q.app.qetaa.customer;

import java.io.Serializable;

import javax.faces.view.ViewScoped;
import javax.inject.Named;


@Named(value = "pastOrdersBean")
@ViewScoped
public class PastOrdersBean implements Serializable {

	/*

	private static final long serialVersionUID = 1L;

	private List<Cart> carts;

	@Inject
	private Requester reqs;

	@Inject
	private LoginBean loginBean;

	@PostConstruct
	public void init() {
		initActiveCarts();
		initCartVariables();
	}

	private void initActiveCarts() {
		Response r = reqs
				.getSecuredRequest(AppConstants.getCustomerArchivedCarts(loginBean.getAccess().getCustomer().getId()));
		if (r.getStatus() == 200) {
			this.carts = r.readEntity(new GenericType<List<Cart>>() {
			});
		} else {
			carts = new ArrayList<>();
		}
	}

	public void initCartVariables() {
		String header = reqs.getSecurityHeader();
		for (Cart cart : carts) {
			initCity(cart, header);
			initApprovedItems(cart, header);
			initPromo(cart, header);
			initReviews(cart, header);
		}
	}
	
	private void initReviews(Cart cart, String header) {
		try {
			Response r = PojoRequester.getSecuredRequest(AppConstants.getVisibleReview(cart.getId()), header);
			if(r.getStatus() == 200) {
				List<CartReview> reviews = r.readEntity(new GenericType<List<CartReview>>() {});
				cart.setReviews(reviews);
			}
		}catch(Exception ex) {
			cart.setReviews(new ArrayList<>());
		}
	}

	private void initApprovedItems(Cart cart, String header) {
		Response r = PojoRequester.getSecuredRequest(AppConstants.getCustomerPartsApprovedITems(cart.getId()), header);
		if (r.getStatus() == 200) {
			List<ApprovedQuotationItem> approved = r.readEntity(new GenericType<List<ApprovedQuotationItem>>() {
			});
			cart.setApprovedItems(approved);
		}

	}

	private void initPromo(Cart cart, String header) {
		if (cart.getPromotionCode() != null) {
			Response r = PojoRequester.getSecuredRequest(AppConstants.getPromoCode(cart.getPromotionCode()), header);
			if (r.getStatus() == 200) {
				cart.setPromoCodeObject(r.readEntity(PromotionCode.class));;
			}
		}

	}

	private void initCity(Cart cart, String header) {
		Response r = PojoRequester.getSecuredRequest(AppConstants.getCity(cart.getCityId()), header);
		if (r.getStatus() == 200) {
			City city = r.readEntity(City.class);
			cart.setCity(city);
		}
	}

	public List<Cart> getCarts() {
		return carts;
	}

	public void setCarts(List<Cart> carts) {
		this.carts = carts;
	}
	*/
}
