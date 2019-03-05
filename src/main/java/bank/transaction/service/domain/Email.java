package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class Email {
    @JsonProperty("payment_code")
    private String paymentCode;

    @JsonProperty("last_payment_date")
    private String lastPaymentDate;

    @JsonProperty("last_payment_hout")
    private String lastPaymentHour;

    @JsonProperty("email_to")
    private String emailTo;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("shipping_address")
    private String shippingAddress;

    @JsonProperty("phone_number")
    private String phoneNumber;

    @JsonProperty("total_price")
    private int totalPrice;

    @JsonProperty("total_payment")
    private int totalPayment;

    @JsonProperty("ppn")
    private int ppn;

    @JsonProperty("order_detail")
    private List<EmailOrderDetail> emailOrderDetailList;

    public void setPaymentCode(String paymentCode) { this.paymentCode = paymentCode; }
    public String getPaymentCode() { return paymentCode; }

    public void setLastPaymentDate(String lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    public String getLastPaymentDate() { return lastPaymentDate; }

    public void setLastPaymentHour(String lastPaymentHour) { this.lastPaymentHour = lastPaymentHour; }
    public String getLastPaymentHour() { return lastPaymentHour; }

    public void setEmailTo(String emailTo) { this.emailTo = emailTo; }
    public String getEmailTo() { return emailTo; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerName() { return customerName; }

    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getShippingAddress() { return shippingAddress; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getPhoneNumber() { return phoneNumber; }

    public void setTotalPrice(int totalPrice) { this.totalPrice = totalPrice; }
    public int getTotalPrice() { return totalPrice; }

    public void setTotalPayment(int totalPayment) { this.totalPayment = totalPayment; }
    public int getTotalPayment() { return totalPayment; }

    public void setPpn(int ppn) { this.ppn = ppn; }
    public int getPpn() { return ppn; }

    public void setEmailOrderDetailList(List<EmailOrderDetail> emailOrderDetailList) { this.emailOrderDetailList = emailOrderDetailList; }

    public List<EmailOrderDetail> getEmailOrderDetailList() { return emailOrderDetailList; }

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
