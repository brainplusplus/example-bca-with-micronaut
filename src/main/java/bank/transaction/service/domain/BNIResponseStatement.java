package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class BNIResponseStatement {

    @JsonProperty("cliendId")
    private String cliendId;

    @JsonProperty("parameters")
    private List<BNIParameter> bniParameterList;

    private BNIStatement bniStatement;

    public void setCliendId(String cliendId) { this.cliendId = cliendId; }
    public String getCliendId() { return cliendId; }

    public void setBniParameterList(List<BNIParameter> bniParameterList) { this.bniParameterList = bniParameterList; }
    public List<BNIParameter> getBniParameterList() { return bniParameterList; }

    public void setBniStatement(BNIStatement bniStatement) { this.bniStatement = bniStatement; }
    public BNIStatement getBniStatement() { return bniStatement; }

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
