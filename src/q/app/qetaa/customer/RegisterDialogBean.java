package q.app.qetaa.customer;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import q.app.qetaa.beans.LoginBean;

@Named(value = "registerDialogBean")
@RequestScoped
public class RegisterDialogBean {

	@Inject
	private LoginBean loginBean;

	@PostConstruct
	public void init() {
		try {
			if (null != loginBean && loginBean.getLoginStatus() == 'R')
				if (!FacesContext.getCurrentInstance().isPostback()) {
					PrimeFaces.current().executeScript("document.getElementById('myBtn2').click()");
				}
		} catch (Exception ex) {
			System.out.println("handled");
		}
	}

	public void test() {

	}

}
