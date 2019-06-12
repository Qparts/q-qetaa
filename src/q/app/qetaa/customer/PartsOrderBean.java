package q.app.qetaa.customer;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named(value = "partsOrderBean")
@SessionScoped
public class PartsOrderBean implements Serializable {


	/*
	private static final long serialVersionUID = 1L;

	private Cart cart;
	private int step = 1;
	private List<City> cities;
	private City selectedCity;
	private int selectedCityId;
	private char paymentMethod;
	private MoyasarLocalPayment localPayment;
	private PaymentResponseCC creditResponse;
	private PaymentResponseSadad sadadResponse;
	private PartsOrder partOrder;
	private CustomerAddress address;
	private CreditCard visa;
	private CreditCard masterCard;
	private boolean havePromo;
	private boolean failed;
	private boolean promVerified;
	private String promCodeString;
	private int additionalDelivery;

	@Inject
	private Requester reqs;
	@Inject
	private LoginBean loginBean;
	@Inject
	private ActivityMonitorBean monitorBean;

	public void init() {
		try {
			paymentMethod = 'N';
			this.updateDelivery();
			String s = Helper.getParam("order");
			Long cartId = Long.parseLong(s);
			if (s != null) {
				if (null != this.cart && this.cart.getId() == cartId) {

				} else {
					this.cart = new Cart();
					initCart(cartId);
					initCartVariables();
					initBean();
				}
			} else {
				throw new Exception();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			this.cart = null;
			Helper.redirect("/my_orders");
		}
	}

	private void initCartVariables() {
		String header = reqs.getSecurityHeader();
		initCity(cart, header);
		initModelYear(cart, header);
		initApprovedItems(cart, header);
		cart.setCustomer(loginBean.getAccess().getCustomer());
		initPartsHolder();
	}

	private void initApprovedItems(Cart cart, String header) {
		String link = "";
		if (cart.getStatus() == 'T') {
			link = AppConstants.getCustomerPartsApprovedITems(cart.getId());
		} else {
			link = AppConstants.getCustomerApprovedItems(cart.getId());
		}
		Response r = PojoRequester.getSecuredRequest(link, header);
		if (r.getStatus() == 200) {
			List<ApprovedQuotationItem> approved = r.readEntity(new GenericType<List<ApprovedQuotationItem>>() {
			});
			cart.setApprovedItems(approved);
		}
	}

	private void initModelYear(Cart cart, String header) {
		Response r = PojoRequester.getSecuredRequest(AppConstants.getModelYear(cart.getVehicleYear()), header);
		if (r.getStatus() == 200) {
			PublicModelYear modelYear = r.readEntity(PublicModelYear.class);
			cart.setModelYear(modelYear);
		}
	}

	private void initCity(Cart cart, String header) {
		Response r = PojoRequester.getSecuredRequest(AppConstants.getCity(cart.getCityId()), header);
		if (r.getStatus() == 200) {
			City city = r.readEntity(City.class);
			cart.setCity(city);
		}
	}

	private void initCart(long cartId) throws Exception {
		Response r = reqs
				.getSecuredRequest(AppConstants.getQuotationCart(cartId, loginBean.getAccess().getCustomer().getId()));
		if (r.getStatus() == 200) {
			cart = r.readEntity(Cart.class);
		} else {
			throw new Exception();
		}
	}

	private void initBean() throws Exception {
		step = 1;
		initCities();
		initAddress();
		localPayment = new MoyasarLocalPayment();
		localPayment.setName("");
		localPayment.setNumber("");
		if (this.cart.getStatus() == 'T') {
			step = 4;
		} else if (this.cart.getStatus() != 'S') {
			throw new Exception();
		}
		visa = new CreditCard();
		masterCard = new CreditCard();
	}

	public double getGrandTotal() {
		double vat = cart.getVatPercentage();
		double dfees = this.getDeliveryCharges();
		double partsTotal = partOrder.getNewTotalPrice();
		double discount = 0;
		if (this.promVerified && this.cart.getPromoCodeObject().isDiscountPromo()) {
			double perc = this.cart.getPromoCodeObject().getDiscountPercentage();
			discount = (dfees * perc) + (partsTotal * perc);
		}
		return partsTotal + dfees + ((partsTotal + dfees) * vat) - discount;
	}

	private void initPartsHolder() {
		partOrder = new PartsOrder();
		partOrder.setPartsItems(new ArrayList<>());
		for (ApprovedQuotationItem approved : cart.getApprovedItems()) {
			PartsOrderItem poi = new PartsOrderItem();
			poi.setOrderedQuantity(approved.getQuantity());
			poi.setQuotationItemId(approved.getQuotationItemId());
			poi.setSalesPrice(approved.getUnitSales());
			poi.setItemDesc(approved.getItemDesc());
			poi.setNewQuantity(approved.getQuantity());
			partOrder.getPartsItems().add(poi);
		}
	}

	public double grandTotal() {
		double vat = cart.getVatPercentage();
		double dfees = this.getDeliveryCharges();
		double partsTotal = partOrder.getNewTotalPrice();
		double vatAmount = (partsTotal + dfees) * vat;
		double total = vatAmount + dfees + partsTotal;
		return total;

	}

	private void preparePartsOrder2(Long pid) {
		// prepare address
		partOrder.setCartId(this.cart.getId());
		partOrder.setSalesAmount(grandTotal());
		partOrder.adjustFinalQuantity();
		partOrder.setPaymentId(pid);
		partOrder.setAddress(this.address);
	}

	private void preparePartsOrder() {
		// prepare address
		partOrder.setCartId(this.cart.getId());
		partOrder.setSalesAmount(grandTotal());
		partOrder.adjustFinalQuantity();
		partOrder.setAddress(this.address);
	}

	private void initAddress() {
		address = new CustomerAddress();
		address.setCustomerId(loginBean.getAccess().getCustomer().getId());
		address.setCreatedBy(0);
		address.setCityId(this.selectedCityId);
		address.setLine1("");
		address.setLine2("");
	}

	private void initCities() {
		this.cities = new ArrayList<>();
		Response r = reqs.getSecuredRequest(AppConstants.getCountryCities(this.cart.getCustomer().getCountryId()));
		if (r.getStatus() == 200) {
			cities = r.readEntity(new GenericType<List<City>>() {
			});
		}
		this.selectedCity = cart.getCity();
		this.selectedCityId = cart.getCityId();
		// get saudi arabia cities only
	}

	public void next() {
		System.out.println(step);
		if (step == 1) {
			monitorBean.addToActivity("clicked next on parts order: " + cart.getId());
			if (partOrder.getNewTotalPrice() > 0) {
				monitorBean.addToActivity("moved to q.app.qetaa.payment page");
				for (PartsOrderItem pi : partOrder.getPartsItems()) {
					monitorBean.addToActivity(
							"selected item for purchase: " + pi.getItemDesc() + ", quantity: " + pi.getNewQuantity());
				}
				step = 2;
			} else {
				monitorBean.addToActivity("clicked next, but no parts selected for purchase");
				Helper.addErrorMessage(Bundler.getValue("partsNotSelected"));
			}
		} else if (step == 2) {
			monitorBean.addToActivity("verifying q.app.qetaa.payment");
			List<String> errors = verifyPayment();
			if (errors.isEmpty()) {
				prepareLocalPayment();
				if (this.paymentMethod == 'V' || this.paymentMethod == 'M') {
					monitorBean.addToActivity("making credit card q.app.qetaa.payment request");
					makeCreditCardRequest();
				} else if (this.paymentMethod == 'S') {
					makeSadadRequest();
				} else if (this.paymentMethod == 'W') {
					monitorBean.addToActivity("making wire transfer request");
					makeWireTransferRequest();
				} else if (this.paymentMethod == 'D') {
					monitorBean.addToActivity("cash on delivery request");
					makeCashOnDeliveryRequest();
					// send to quotation service to update q.app.qetaa.cart status as on delivery q.app.qetaa.payment.
					// this should require user to aprove after calling the q.app.qetaa.customer

				}
			} else {
				for (String error : errors) {
					monitorBean.addToActivity("q.app.qetaa.payment error: " + error);
					Helper.addErrorMessage(error);
				}
			}
		}
	}

	private List<String> verifyPayment() {
		List<String> errors = new ArrayList<>();
		if (this.paymentMethod == 'V' || this.paymentMethod == 'M') {
			CreditCard card = this.getCreditCard();
			card.getNumber().trim();
			Pattern pattern = Pattern.compile(this.regexCardNumber(this.paymentMethod));
			Matcher matcher = pattern.matcher(card.getNumber());
			// check card number
			if (!matcher.matches()) {
				errors.add("*" + Bundler.getValue("wrongCartNumber"));
			}
			// check card name
			card.getName().trim();
			if (card.getName().length() == 0 || !card.getName().contains(" ")) {
				errors.add("*" + Bundler.getValue("wrongName"));
			}
			// check cvc

			if (card.getCvc() == null || !(card.getCvc() > 0 && card.getCvc() < 1000)) {
				errors.add("*" + Bundler.getValue("wrongCVC"));
			}

			// check card expiry date
			Calendar c = Calendar.getInstance();
			int currentYear = c.get(Calendar.YEAR);
			int currentMonth = c.get(Calendar.MONTH) + 1;

			if (card.getYear() < currentYear || (card.getYear() == currentYear && card.getMonth() <= currentMonth)) {
				errors.add("*" + Bundler.getValue("cardExpired"));
			}
		}
		if (this.paymentMethod == 'N') {
			errors.add("*" + Bundler.getValue("selectPayment"));
		}

		if (this.address.getLine1() == null || this.address.getLine1().length() == 0) {
			errors.add("*" + Bundler.getValue("enterAddress"));
		}

		return errors;
	}

	private void makeWireTransferRequest() {
		Response r = reqs.putSecuredRequest(AppConstants.PUT_UPDATE_CART, cart);
		if (r.getStatus() == 201) {
			preparePartsOrder2(0L);
			Response r2 = reqs.postSecuredRequest(AppConstants.POST_WIRE_TRASNFER, partOrder);
			if (r2.getStatus() == 201) {
				monitorBean.addToActivity("successful wire transfer request");
				this.cart.setStatus('T');
				step = 4;
			} else {
				Helper.addErrorMessage("Something went wrong please try again later");
			}
		}
	}

discount/promocode/{param}
	public void verifyPromCode() {
		Response r = reqs.getSecuredRequest(AppConstants.getPromotionCodeFromCode(this.promCodeString));
		if (r.getStatus() == 200) {
			this.promVerified = true;
			PromotionCode pc = r.readEntity(PromotionCode.class);
			this.cart.setPromotionCode(pc.getId());
			this.cart.setPromoCodeObject(pc);
		} else if (r.getStatus() == 498) {
			Helper.addErrorMessage(Bundler.getValue("promCodeQuestion"), "form-3:ms");
		} else if (r.getStatus() == 410) {
			Helper.addErrorMessage(Bundler.getValue("promCodeUsed"), "form-3:ms");
		} else if (r.getStatus() == 404) {
			Helper.addErrorMessage(Bundler.getValue("promCodeNotFound"), "form-3:ms");
		} else {
			Helper.addErrorMessage(Bundler.getValue("errorOccured"));
		}
	}

	private void makeSadadRequest() {
		this.localPayment.setAmount(getGrandTotal());
		this.localPayment.setCallback("");
		this.localPayment.setCartId(cart.getId());
		this.localPayment.setPaymentIndex(1);
		this.localPayment.setType('S');
		Response r = reqs.postSecuredRequest(AppConstants.POST_PARTS_PAYMENT_MAYASAR, localPayment);
		if (r.getStatus() == 201) {
			this.sadadResponse = r.readEntity(PaymentResponseSadad.class);
		} else {
			Helper.addErrorMessage("Could not process transation! please try again");
		}
	}

	private void prepareLocalPayment() {
		if (paymentMethod == 'V' || paymentMethod == 'M') {
			CreditCard card = getCreditCard();
			this.localPayment.setCvc(card.getCvc());
			this.localPayment.setMonth(card.getMonth());
			this.localPayment.setName(card.getName());
			this.localPayment.setNumber(card.getNumber());
			this.localPayment.setYear(card.getYear());
		}
	}

	private CreditCard getCreditCard() {
		CreditCard card = new CreditCard();
		if (paymentMethod == 'V') {
			return visa;
		} else if (paymentMethod == 'M') {
			return masterCard;
		}
		return new CreditCard();
	}

	private void makeCreditCardRequest() {
		this.localPayment.setAmount(getGrandTotal());
		this.localPayment
				.setCallback(AppConstants.APP_HOST + "payment_response?order=" + cart.getId() + "&type=" + 'C');
		// this.localPayment
		 //.setCallback("http://localhost:8081/" + "payment_response?order=" +
		 //q.app.qetaa.cart.getId() + "&type=" + 'C');

		this.localPayment.setCartId(cart.getId());
		this.localPayment.setPaymentIndex(1);
		this.localPayment.setType('C');
		Response r = reqs.postSecuredRequest(AppConstants.POST_PARTS_PAYMENT_MAYASAR, localPayment);
		if (r.getStatus() == 200) {
			monitorBean.addToActivity("redirecting to q.app.qetaa.payment page");
			this.creditResponse = r.readEntity(PaymentResponseCC.class);
			Helper.redirect(creditResponse.getSource().getTransactionURL());
		} else {
			monitorBean.addToActivity("error in q.app.qetaa.payment from our server");
			Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
		}
	}

	private void makeCashOnDeliveryRequest() {
		Map<String, Object> map = new HashMap<String, Object>();
		preparePartsOrder();
		cart.setDeliveryFees(this.getDeliveryCharges());
		map.put("partsOrder", partOrder);
		map.put("q/app/qetaa/cart", cart);
		map.put("discountPercentage", getCalculatedDiscountPercentage());
		map.put("customerName",
				this.getCart().getCustomer().getFirstName() + " " + this.getCart().getCustomer().getLastName());
		Response r2 = reqs.postSecuredRequest(AppConstants.POST_CREATE_PARTS_ORDER_COD, map);
		if (r2.getStatus() == 201) {
			cart.setStatus('P');
			System.out.println("cash on delivery created");
			step = 5;
		}
	}

	public void processPaymentResponse() {
		try {
			monitorBean.addToActivity("received response from credit card q.app.qetaa.payment");
			String paymentId = Helper.getParam("id");// some string
			String status = Helper.getParam("status");// paid , failed
			String typeString = Helper.getParam("type");// C , S (creditCard or sadad).;
			if (status.equals("paid")) {
				monitorBean.addToActivity("successful q.app.qetaa.payment at credit card, q.app.qetaa.payment id: " + paymentId);
				failed = false;
				if (typeString.equals("C")) {
					Map<String, Object> map = new HashMap<String, Object>();
					preparePartsOrder();
					map.put("partsOrder", partOrder);
					map.put("q/app/qetaa/cart", cart);
					String ccCompany = "";
					if (this.paymentMethod == 'V') {
						ccCompany = "visa";
					} else if (this.paymentMethod == 'M') {
						ccCompany = "mastercard";
					}
					map.put("ccCompany", ccCompany);
					map.put("discountPercentage", getCalculatedDiscountPercentage());
					map.put("customerName", this.getCart().getCustomer().getFirstName() + " "
							+ this.getCart().getCustomer().getLastName());
					map.put("gateway", "Moyassar");
					map.put("transactionId", paymentId);
					Double fees = (getGrandTotal() * 0.024) + 1;
					map.put("creditFees", fees);
					Response r = reqs.postSecuredRequest(AppConstants.POST_CREATE_PARTS_ORDER_CC, map);
					if (r.getStatus() == 201) {
						cart.setStatus('P');
						System.out.println("paid and successfully processed " + localPayment);
						step = 3;
						Helper.redirect("parts_order?order=" + this.cart.getId());
					} else {
						cart.setStatus('P');
						step = 3;
						System.out.println("paid but failed to process " + localPayment);
						monitorBean.addToActivity("failed to created wallet, but credit card created " + paymentId);
						Helper.redirect("parts_order?order=" + this.cart.getId());
						// parts paid but there is a problem in creating part!
						// save the error and proceed normally but fix at the backend
					}
				}
			} else if (status.equals("failed")) {
				monitorBean.addToActivity("failed credit card q.app.qetaa.payment");
				failed = true;
				Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
				Helper.redirect("parts_order?order=" + this.cart.getId());
				// do not proceed.
			}

		} catch (Exception ex) {

		}
	}

	private double getCalculatedDiscountPercentage() {
		if (this.getCart().getPromoCodeObject() != null) {
			if (this.getCart().getPromoCodeObject().isDiscountPromo()) {
				return this.getCart().getPromoCodeObject().getDiscountPercentage();
			}
		}
		return 0;
	}

	public void processPaymentResponse2() {
		try {
			monitorBean.addToActivity("received response from credit card q.app.qetaa.payment");
			String paymentId = Helper.getParam("id");// some string
			String status = Helper.getParam("status");// paid , failed
			// String messageString = Helper.getParam("message");// Succeeded%21,
			// 3-D+secure+transaction+attempt+failed
			String typeString = Helper.getParam("type");// C , S (creditCard or sadad).
			if (status.equals("paid")) {
				monitorBean.addToActivity("successful q.app.qetaa.payment at credit card, q.app.qetaa.payment id: " + paymentId);
				failed = false;
				if (typeString.equals("C")) {
					//
					PartsPayment payment = new PartsPayment(creditResponse, cart, paymentId);
					Response r = reqs.postSecuredRequest(AppConstants.POST_PARTS_PAYMENT_FINALIZE, payment);
					if (r.getStatus() == 200) {
						// get q.app.qetaa.payment id;
						Long pId = r.readEntity(Long.class);
						// create part order
						preparePartsOrder2(pId);
						Response r2 = reqs.postSecuredRequest(AppConstants.CUSTOMER_CREATE_PARTS_ORDER, this.partOrder);
						if (r2.getStatus() == 201) {
							this.cart.setStatus('P');
							System.out.println("paid and successfully processed " + localPayment);
							step = 3;
						} else {
							this.cart.setStatus('P');
							step = 3;
							System.out.println("paid but failed to process " + localPayment);
							// parts paid but there is a problem in creating part!
							// save the error and proceed normally but fix at the backend
						}
					} else {
						step = 3;
						// log its important!! because q.app.qetaa.payment was made but was not processed!!!
						System.out.println("paid but failed to process " + localPayment);
						this.cart.setStatus('P');
						// parts paid but there is a problem in saving the q.app.qetaa.payment
						// save the error and proceed normally but fix at the backend
					}
				}
			} else if (status.equals("failed")) {
				monitorBean.addToActivity("failed credit card q.app.qetaa.payment");
				failed = true;
				Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
				// do not proceed.
			}

		} catch (Exception ex) {
			monitorBean.addToActivity("failed credit card q.app.qetaa.payment");
			failed = true;
			Helper.addErrorMessage("لم تتم عملية الدفع بنجاح, الرجاء المحاولة مرة اخرى");
			// redirect to partsOrder and show an error
		}
	}

	public void updateDelivery() {
		if (this.paymentMethod == 'D') {
			this.additionalDelivery = 30;
		} else {
			this.additionalDelivery = 0;
		}
	}

	public int[] getExpiryDates() {
		int[] years = new int[10];
		int year = Calendar.getInstance().get(Calendar.YEAR);
		for (int i = 0; i < years.length; i++) {
			years[i] = year;
			year++;
		}
		return years;
	}

	public int[] getExpiryMonths() {
		int[] months = new int[12];
		for (int i = 0; i < months.length; i++) {
			months[i] = i + 1;
		}
		return months;
	}

	public void previous() {
		if (step == 2) {
			step = 1;
		}
	}

	public boolean isCardPayment() {
		switch (this.paymentMethod) {
		case 'V':
		case 'M':
		case 'A':
			return true;
		default:
			return false;
		}
	}

	public String regexCardNumber(char method) {
		switch (this.paymentMethod) {
		case 'V':
			return "^4[0-9]{12}(?:[0-9]{3})?$";
		case 'M':
			return "^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$";
		case 'A':
			return "^3[47][0-9]{13}$";
		default:
			return "";
		}
	}

	public Integer getDeliveryCharges() {
		return this.cart.getDeliveryFees() + this.additionalDelivery;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public List<City> getCities() {
		return cities;
	}

	public void setCities(List<City> cities) {
		this.cities = cities;
	}

	public City getSelectedCity() {
		return selectedCity;
	}

	public void setSelectedCity(City selectedCity) {
		this.selectedCity = selectedCity;
	}

	public int getSelectedCityId() {
		return selectedCityId;
	}

	public void setSelectedCityId(int selectedCityId) {
		this.selectedCityId = selectedCityId;
	}

	public char getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(char paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public CustomerAddress getAddress() {
		return address;
	}

	public void setAddress(CustomerAddress address) {
		this.address = address;
	}

	public PartsOrder getPartOrder() {
		return partOrder;
	}

	public void setPartOrder(PartsOrder partOrder) {
		this.partOrder = partOrder;
	}

	public CreditCard getVisa() {
		return visa;
	}

	public void setVisa(CreditCard visa) {
		this.visa = visa;
	}

	public CreditCard getMasterCard() {
		return masterCard;
	}

	public void setMasterCard(CreditCard masterCard) {
		this.masterCard = masterCard;
	}

	public boolean isFailed() {
		return failed;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}

	public boolean isHavePromo() {
		return havePromo;
	}

	public void setHavePromo(boolean havePromo) {
		this.havePromo = havePromo;
	}

	public boolean isPromVerified() {
		return promVerified;
	}

	public void setPromVerified(boolean promVerified) {
		this.promVerified = promVerified;
	}

	public String getPromCodeString() {
		return promCodeString;
	}

	public void setPromCodeString(String promCodeString) {
		this.promCodeString = promCodeString;
	}
*/
}
