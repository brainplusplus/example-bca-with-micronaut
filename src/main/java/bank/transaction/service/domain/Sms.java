package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.inject.Singleton;
import java.util.List;

@Singleton
public class Sms {

    @JsonProperty("code")
    private String code;

    @JsonProperty("code_message")
    private String codeMessage;

    @JsonProperty("code_type")
    private String codeType;

    @JsonProperty("data")
    private List<SmsData> smsDataList;

    public void setCode(String code) { this.code = code; }
    public String getCode() { return code; }

    public void setCodeMessage(String codeMessage) { this.codeMessage = codeMessage; }
    public String getCodeMessage() { return codeMessage; }

    public void setCodeType(String codeType) { this.codeType = codeType; }
    public String getCodeType() { return codeType; }

    public void setSmsDataList(List<SmsData> smsDataList) { this.smsDataList = smsDataList; }
    public List<SmsData> getSmsDataList() { return smsDataList; }

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
