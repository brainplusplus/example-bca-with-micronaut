package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Singleton;
import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Singleton
public class AccountStatement {
    @JsonProperty("StartDate")
    private Date startDate;

    @JsonProperty("EndDate")
    @NotNull
    private Date endDate;

    @JsonProperty("Currency")
    private String currencyCode;

    @JsonProperty("StartBalance")
    private BigDecimal startBalance;

    @JsonProperty("Data")
    private List<AccountStatementDetail> accountStatementDetailList;


    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

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
