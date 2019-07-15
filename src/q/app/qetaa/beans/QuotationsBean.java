package q.app.qetaa.beans;

import q.app.qetaa.helpers.AppConstants;
import q.app.qetaa.helpers.Helper;
import q.app.qetaa.model.quotation.PublicQuotation;
import q.app.qetaa.reqs.Requester;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@ViewScoped
public class QuotationsBean implements Serializable {

    //private static final String WHATSAPP_PHONE_NUNBER = "966550955954";
//    private static final String WHATSAPP_PHONE_NUNBER = "966508448856";
    private static final String WHATSAPP_PHONE_NUNBER = "966547074452";
    private static final long serialVersionUID = 1L;
    private PublicQuotation selectedQuotation;
    private List<PublicQuotation> closedQuotations;
    private List<PublicQuotation> pendingQuotaitons;
    private List<PublicQuotation> completedQuotations;

    @Inject
    private Requester reqs;

    @Inject
    private LoginBean loginBean;

    @Inject
    private ActivityMonitorBean monitorBean;

    @PostConstruct
    public void init() {
        this.selectedQuotation = new PublicQuotation();
        initQuotations();

    }

    public void closeQuotation(PublicQuotation quotation) {
        monitorBean.addToActivity("Customer closed quotation no. " + quotation.getId());
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("quotationId", quotation.getId());
        Response r = reqs.putSecuredRequest(AppConstants.PUT_CLOSE_QUOTATION, map);
        Helper.addInfoMessage(""+r.getStatus());
        if(r.getStatus() == 201){
            Helper.redirect("quotations");
        }
    }

    private void initQuotations() {
        pendingQuotaitons = new ArrayList<>();
        completedQuotations = new ArrayList<>();
        closedQuotations = new ArrayList<>();
        Response r = reqs.getSecuredRequest(AppConstants.getPendingQuotations(loginBean.getLoginObject().getCustomer().getId()));
        if (r.getStatus() == 200) {
            this.pendingQuotaitons = r.readEntity(new GenericType<List<PublicQuotation>>() {});
        }
        Response r2 = reqs.getSecuredRequest(AppConstants.getClosedQuotations(loginBean.getLoginObject().getCustomer().getId()));
        if (r2.getStatus() == 200) {
            this.closedQuotations = r2.readEntity(new GenericType<List<PublicQuotation>>() {});
        }
        Response r3 = reqs.getSecuredRequest(AppConstants.getCompletedQuotations(loginBean.getLoginObject().getCustomer().getId()));
        if (r3.getStatus() == 200) {
            this.completedQuotations = r3.readEntity(new GenericType<List<PublicQuotation>>() {});
        }

    }




    public String getWhatsappLink(PublicQuotation quotation) {
        try {
            String q= URLEncoder.encode("استفسار بخصوص الطلب رقم " + quotation.getId(), StandardCharsets.UTF_8);
            return "https://wa.me/"+WHATSAPP_PHONE_NUNBER+"/?text=" + q;
        }catch(Exception ex) {
            return "https://wa.me/"+WHATSAPP_PHONE_NUNBER+"/";
        }
    }


    public PublicQuotation getSelectedQuotation() {
        return selectedQuotation;
    }

    public void setSelectedQuotation(PublicQuotation selectedQuotation) {
        this.selectedQuotation = selectedQuotation;
    }

    public List<PublicQuotation> getClosedQuotations() {
        return closedQuotations;
    }

    public void setClosedQuotations(List<PublicQuotation> closedQuotations) {
        this.closedQuotations = closedQuotations;
    }

    public List<PublicQuotation> getPendingQuotaitons() {
        return pendingQuotaitons;
    }

    public void setPendingQuotaitons(List<PublicQuotation> pendingQuotaitons) {
        this.pendingQuotaitons = pendingQuotaitons;
    }

    public List<PublicQuotation> getCompletedQuotations() {
        return completedQuotations;
    }

    public void setCompletedQuotations(List<PublicQuotation> completedQuotations) {
        this.completedQuotations = completedQuotations;
    }
}
