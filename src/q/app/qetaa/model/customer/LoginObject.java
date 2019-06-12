package q.app.qetaa.model.customer;

import java.io.Serializable;

public class LoginObject implements Serializable {

    private PublicCustomer customer;
    private String token;
    private long tokenExpire;
    private Character cartId;



    public PublicCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(PublicCustomer customer) {
        this.customer = customer;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getTokenExpire() {
        return tokenExpire;
    }

    public void setTokenExpire(long tokenExpire) {
        this.tokenExpire = tokenExpire;
    }

    public Character getCartId() {
        return cartId;
    }

    public void setCartId(Character cartId) {
        this.cartId = cartId;
    }
}
