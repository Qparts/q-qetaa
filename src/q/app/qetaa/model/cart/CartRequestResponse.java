package q.app.qetaa.model.cart;

public class CartRequestResponse {
    private long cartId;
    private long quotaitonId;
    private String transactionUrl;

    public long getQuotaitonId() {
        return quotaitonId;
    }

    public void setQuotaitonId(long quotaitonId) {
        this.quotaitonId = quotaitonId;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    public String getTransactionUrl() {
        return transactionUrl;
    }

    public void setTransactionUrl(String transactionUrl) {
        this.transactionUrl = transactionUrl;
    }
}
