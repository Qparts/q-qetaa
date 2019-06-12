package q.app.qetaa.helpers;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;

public class Bundler {
	
	public static String getValue(String key){
		FacesContext context= FacesContext.getCurrentInstance();
		ResourceBundle bundle = context.getApplication().getResourceBundle(context, "lex");
		String result = null;
		try{
			result = bundle.getString(key);
		}catch(MissingResourceException e){
			result = "???" + key + "??? not found";
		}
		return result;
	}

}
