package q.app.qetaa.beans;

import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.Bundler;
import q.app.qetaa.helpers.Helper;
import q.app.qetaa.model.quotation.PublicQuotation;
import q.app.qetaa.reqs.Requester;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.Response;
import java.io.Serializable;

@Named
@ViewScoped
public class AddToCartBean implements Serializable {

    private PublicQuotation quotation;

    @Inject
    private Requester reqs;

    @Inject
    private ActivityMonitorBean monitorBean;

    @Inject
    private CartBean cartBean;


    @PostConstruct
    private void init(){
        try {
            String s = Helper.getParam("quotation");
            initQuotation(Long.parseLong(s));
        }
        catch (Exception ex){
            Helper.redirect("quotations");
        }
    }

    private void initQuotation(long quotationId){
        Response r = reqs.getSecuredRequest(AppConstants.getQuotation(quotationId));
        if(r.getStatus() == 200){
            quotation = r.readEntity(PublicQuotation.class);
            quotation.initNewQuantities();
        }
    }

    public void checkout() {
        monitorBean.addToActivity("clicked checkout in add-to-cart page : " + quotation.getId());
        if (quotation.getTotalProducts() > 0) {
            monitorBean.addToActivity("moved to payment page");
            for (var pi : quotation.getQuotationItems()) {
                monitorBean.addToActivity("selected item for purchase: " + pi.getId() + ", quantity: " + pi.getQuantity());
            }
            cartBean.init();
            cartBean.addQuotation(quotation);
            cartBean.setStage(2);
            Helper.redirect("select-address");
        } else {
            monitorBean.addToActivity("clicked next, but no parts selected for purchase");
            Helper.addErrorMessage(Bundler.getValue("partsNotSelected"));
        }
    }


    public PublicQuotation getQuotation() {
        return quotation;
    }

    public void setQuotation(PublicQuotation quotation) {
        this.quotation = quotation;
    }
}
