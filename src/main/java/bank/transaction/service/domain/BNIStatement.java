package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class BNIStatement {
    @JsonProperty("getBalanceResponse")
    private List<BNIResponseStatement> bniResponseStatementList;

    public void setBniResponseStatementList(List<BNIResponseStatement> bniResponseStatementList) {
        this.bniResponseStatementList = bniResponseStatementList;
    }

    public List<BNIResponseStatement> getBniResponseStatementList() {
        return bniResponseStatementList;
    }

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