package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="account_statement")
public class AccountStatement {

    public AccountStatement(){ }

    public AccountStatement(@NotNull Date startDate, @NotNull Date endDate, @NotNull String currencyCode, @NotNull BigDecimal startBalance, List<AccountStatementDetail> accountStatementDetailList){
        this.startDate = startDate;
        this.endDate = endDate;
        this.currencyCode = currencyCode;
        this.startBalance = startBalance;
        this.accountStatementDetailList = accountStatementDetailList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Long version;

    @Column(name = "start_date")
    @JsonProperty("StartDate")
    @NotNull
    private Date startDate;

    @Column(name = "end_date")
    @JsonProperty("EndDate")
    @NotNull
    private Date endDate;

    @NotNull
    @Column(name = "currency")
    @JsonProperty("Currency")
    private String currencyCode;

    @NotNull
    @Column(name = "start_balance")
    @JsonProperty("StartBalance")
    private BigDecimal startBalance;

    @Nullable
    @OneToMany(mappedBy = "accountStatement")
    @JsonProperty("Data")
    private List<AccountStatementDetail> accountStatementDetailList;


    public void setId(Long id) { this.id = id; }

    public Long getId() { return id; }

    public void setVersion(Long version) { this.version = version; }

    public Long getVersion() { return version; }

    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public Date getStartDate() { return startDate; }

    public void setEndDate(Date endDate) { this.endDate = endDate; }

    public Date getEndDate() { return endDate;}

    public void setCurrencyCode(String currencyCode) { this.currencyCode = currencyCode; }

    public String getCurrencyCode() { return currencyCode; }

    public void setStartBalance(BigDecimal startBalance) { this.startBalance = startBalance; }

    public BigDecimal getStartBalance() { return startBalance; }

    public List<AccountStatementDetail> getAccountStatementDetailList() {
        return accountStatementDetailList;
    }

    public void setAccountStatementDetailList(@Nullable List<AccountStatementDetail> accountStatementDetailList) {
        this.accountStatementDetailList = accountStatementDetailList;
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
