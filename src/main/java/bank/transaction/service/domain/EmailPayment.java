package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EmailPayment {
    @JsonProperty("method")
    private String method;

    @JsonProperty("vendor")
    private String vendor;

    @JsonProperty("vendor_icon")
    private String vendorIcon;

    @JsonProperty("account_name")
    private String accountName;

    @JsonProperty("account_number")
    private String accountNo;

    @JsonProperty("vendor_branch")
    private String vendorBranch;

    public void setMethod(String method) { this.method = method; }
    public String getMethod() { return method; }

    public void setVendor(String vendor) { this.vendor = vendor; }
    public String getVendor() { return vendor; }

    public void setVendorIcon(String vendorIcon) { this.vendorIcon = vendorIcon; }
    public String getVendorIcon() { return vendorIcon; }

    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getAccountName() { return accountName; }

    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getAccountNo() { return accountNo; }

    public void setVendorBranch(String vendorBranch) { this.vendorBranch = vendorBranch; }
    public String getVendorBranch() { return vendorBranch; }

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
