package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class AccountStatement {
    @JsonProperty("Currency")
    private String currencyCode;

    @JsonProperty("StartBalance")
    private BigDecimal startBalance;

    @JsonProperty("Data")
    private List<AccountStatementDetail> accountStatementDetailList;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getStartBalance() {
        return startBalance;
    }

    public List<AccountStatementDetail> getAccountStatementDetailList() {
        return accountStatementDetailList;
    }

    @Override
    public String toString() {
        return "AccountStatement{" +
                "currencyCode='" + currencyCode + '\'' +
                ", startBalance=" + startBalance +
                ", accountStatementDetailList=" + accountStatementDetailList +
                '}';
    }
}
