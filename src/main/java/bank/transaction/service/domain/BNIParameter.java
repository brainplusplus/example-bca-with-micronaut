package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class BNIParameter {
    @JsonProperty("responseCode")
    private String responseCode;

    @JsonProperty("responseCode")
    private String responseMessage;

    @JsonProperty("responseCode")
    private Date responseTimestamp;

    @JsonProperty("responseCode")
    private String customerName;

    @JsonProperty("responseCode")
    private String accountCurrency;

    @JsonProperty("responseCode")
    private String accountBalance;

    private BNIResponseStatement bniResponseStatement;

    public void setResponseCode(String responseCode) { this.responseCode = responseCode; }
    public String getResponseCode() { return responseCode; }

    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    public String getResponseMessage() { return responseMessage; }

    public void setResponseTimestamp(Date responseTimestamp) { this.responseTimestamp = responseTimestamp; }
    public Date getResponseTimestamp() { return responseTimestamp; }

    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerName() { return customerName; }

    public void setAccountCurrency(String accountCurrency) { this.accountCurrency = accountCurrency; }
    public String getAccountCurrency() { return accountCurrency; }

    public void setAccountBalance(String accountBalance) { this.accountBalance = accountBalance; }
    public String getAccountBalance() { return accountBalance; }

    public void setBniResponseStatement(BNIResponseStatement bniResponseStatement) { this.bniResponseStatement = bniResponseStatement; }
    public BNIResponseStatement getBniResponseStatement() { return bniResponseStatement; }

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
