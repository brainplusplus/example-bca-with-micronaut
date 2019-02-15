package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Singleton;
import java.math.BigDecimal;

@Singleton
public class Account {
    @JsonProperty("AccountNumber")
    private String no;

    @JsonProperty("Currency")
    private String currencyCode;

    @JsonProperty("Balance")
    private BigDecimal balance;

    @JsonProperty("AvailableBalance")
    private BigDecimal availableBalance;

    @JsonProperty("FloatAmount")
    private BigDecimal floatAmount;

    @JsonProperty("HoldAmount")
    private BigDecimal holdAmount;

    @JsonProperty("Plafon")
    private BigDecimal plafond;

    public String getNo() {
        return no;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public BigDecimal getAvailableBalance() {
        return availableBalance;
    }

    public BigDecimal getFloatAmount() {
        return floatAmount;
    }

    public BigDecimal getHoldAmount() {
        return holdAmount;
    }

    public BigDecimal getPlafond() {
        return plafond;
    }

    @Override
    public String toString() {
        return "Account{" +
                "no='" + no + '\'' +
                ", currencyCode='" + currencyCode + '\'' +
                ", balance=" + balance +
                ", availableBalance=" + availableBalance +
                ", floatAmount=" + floatAmount +
                ", holdAmount=" + holdAmount +
                ", plafond=" + plafond +
                '}';
    }
}
