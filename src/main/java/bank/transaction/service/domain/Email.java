package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class Email {
    @JsonProperty("last_payment_date")
    private String lastPaymentDate;

    @JsonProperty("last_payment_hour")
    private String lastPaymentHour;

    @JsonProperty("email_to")
    private String emailTo;

    @JsonProperty("customer_name")
    private String customerName;

    @JsonProperty("order_number")
    private String orderNumber;

    @JsonProperty("total_payment")
    private int totalPayment;

    @JsonProperty("subtotal")
    private int subtotal;

    @JsonProperty("shipping_fee")
    private int shippingFee;

    @JsonProperty("unique_code")
    private int uniqueCode;

    @JsonProperty("order_detail")
    private List<EmailOrderDetail> emailOrderDetailList;

    @JsonProperty("payment")
    private List<EmailPayment> emailPaymentList;

    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public String getOrderNumber() { return orderNumber; }

    public void setLastPaymentDate(String lastPaymentDate) { this.lastPaymentDate = lastPaymentDate; }
    public String getLastPaymentDate() { return lastPaymentDate; }

    public void setLastPaymentHour(String lastPaymentHour) { this.lastPaymentHour = lastPaymentHour; }
    public String getLastPaymentHour() { return lastPaymentHour; }

    public void setEmailTo(String emailTo) { this.emailTo = emailTo; }
    public String getEmailTo() { return emailTo; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerName() { return customerName; }

    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }
    public int getSubtotal() { return subtotal; }

    public void setShippingFee(int shippingFee) { this.shippingFee = shippingFee; }
    public int getShippingFee() { return shippingFee; }

    public void setTotalPayment(int totalPayment) { this.totalPayment = totalPayment; }
    public int getTotalPayment() { return totalPayment; }

    public void setUniqueCode(int uniqueCode) { this.uniqueCode = uniqueCode; }
    public int getUniqueCode() { return uniqueCode; }

    public void setEmailOrderDetailList(List<EmailOrderDetail> emailOrderDetailList) { this.emailOrderDetailList = emailOrderDetailList; }
    public List<EmailOrderDetail> getEmailOrderDetailList() { return emailOrderDetailList; }

    public void setEmailPaymentList(List<EmailPayment> emailPaymentList) { this.emailPaymentList = emailPaymentList; }
    public List<EmailPayment> getEmailPaymentList() { return emailPaymentList; }

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
