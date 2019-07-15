package q.app.qetaa.model.quotation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import q.app.qetaa.helpers.Bundler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CardHolder implements Serializable {

    private Integer ccMonth;
    private Integer ccYear;
    private String ccName;
    private String ccNumber;
    private String ccCvc;


    @JsonIgnore
    public int[] getExpiryDates() {
        int[] years = new int[10];
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 0; i < years.length; i++) {
            years[i] = year;
            year++;
        }
        return years;
    }

    @JsonIgnore
    public int[] getExpiryMonths() {
        int[] months = new int[12];
        for (int i = 0; i < months.length; i++) {
            months[i] = i + 1;
        }
        return months;
    }

    @JsonIgnore
    public List<String> verifyPayment(char paymentMethod){
        List<String> errors = new ArrayList<>();
        if (paymentMethod == 'V' || paymentMethod == 'M') {
            ccNumber = ccNumber.trim();
            Pattern pattern = Pattern.compile(regexCardNumber());
            Matcher matcher = pattern.matcher(ccNumber);
            // check card number
            if (!matcher.matches()) {
                errors.add("*" + Bundler.getValue("wrongCartNumber"));
            }
            // check card name
            ccName = ccName.trim();
            if (ccName.length() == 0 || !ccName.contains(" ")) {
                errors.add("*" + Bundler.getValue("wrongName"));
            }
            // check cvc

            ccCvc = ccCvc.trim();
            Pattern pattern2 = Pattern.compile(regexCVV());
            Matcher matcher2 = pattern2.matcher(ccCvc);
            // check card number
            if (!matcher2.matches()) {
                errors.add("*" + Bundler.getValue("wrongCVC"));
            }

            // check card expiry date
            Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            int currentMonth = c.get(Calendar.MONTH) + 1;

            if (ccYear < currentYear || (ccYear == currentYear && ccMonth <= currentMonth)) {
                errors.add("*" + Bundler.getValue("cardExpired"));
            }
        }
        if (paymentMethod == 'N') {
            errors.add("*" + Bundler.getValue("selectPayment"));
        }
        return errors;
    }



    private String regexCardNumber() {
        return "^(?:(?<visa>4[0-9]{12}(?:[0-9]{3})?)|(?<mastercard>5[1-5][0-9]{14}))$";
    }

    private String regexCVV() {
        return "^[0-9]{3}$";
    }

    public Integer getCcMonth() {
        return ccMonth;
    }

    public void setCcMonth(Integer ccMonth) {
        this.ccMonth = ccMonth;
    }

    public Integer getCcYear() {
        return ccYear;
    }

    public void setCcYear(Integer ccYear) {
        this.ccYear = ccYear;
    }

    public String getCcName() {
        return ccName;
    }

    public void setCcName(String ccName) {
        this.ccName = ccName;
    }

    public String getCcNumber() {
        return ccNumber;
    }

    public void setCcNumber(String ccNumber) {
        this.ccNumber = ccNumber;
    }

    public String getCcCvc() {
        return ccCvc;
    }

    public void setCcCvc(String ccCvc) {
        this.ccCvc = ccCvc;
    }
}
