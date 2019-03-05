package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import javax.inject.Singleton;

@Singleton
public class EmailOrderDetail {
    @JsonProperty("name")
    private String name;

    @JsonProperty("code")
    private String code;

    @JsonProperty("price")
    private int price;

    @JsonProperty("quantity")
    private int qty;

    @JsonProperty("total")
    private int total;

    private Email email;

    public void setName(String name) { this.name = name; }
    public String getName() { return name; }

    public void setCode(String code) { this.code = code; }
    public String getCode() { return code; }

    public void setPrice(int price) { this.price = price; }
    public int getPrice() { return price; }

    public void setQty(int qty) { this.qty = qty; }
    public int getQty() { return qty; }

    public void setTotal(int total) { this.total = total; }
    public int getTotal() { return total; }

    public void setEmail(Email email) { this.email = email; }
    public Email getEmail() { return email; }

    @Override
    public String toString() {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
