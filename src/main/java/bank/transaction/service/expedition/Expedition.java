package bank.transaction.service.expedition;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class Expedition {
    public Expedition(){ }

    public Expedition(String code, String codeMessage, String codeType, List<ExpeditionDetail> expeditionDetailList){
        this.code = code;
        this.codeMessage = codeMessage;
        this.codeType = codeType;
        this.expeditionDetailList = expeditionDetailList;
    }

    @JsonProperty("code")
    private String code;

    @JsonProperty("code_message")
    private String codeMessage;

    @JsonProperty("code_type")
    private String codeType;

    @JsonProperty("is_delivered")
    private boolean isDelivered;

    @JsonProperty("data")
    private List<ExpeditionDetail> expeditionDetailList;

    public void setCode(String code) { this.code = code; }
    public String getCode() { return code; }

    public void setCodeMessage(String codeMessage) { this.codeMessage = codeMessage; }
    public String getCodeMessage() { return codeMessage; }

    public void setCodeType(String codeType) { this.codeType = codeType; }
    public String getCodeType() { return codeType; }

    public void setDelivered(boolean delivered) { isDelivered = delivered; }
    public boolean getDelivered(){return isDelivered; }

    public void setExpeditionDetailList(List<ExpeditionDetail> expeditionDetailList) { this.expeditionDetailList = expeditionDetailList; }
    public List<ExpeditionDetail> getExpeditionDetailList() { return expeditionDetailList; }

    @Override
    public String toString() {
        return "Expedition{" +
                "code='" + code + '\'' +
                ", codeMessage='" + codeMessage + '\'' +
                ", codeType='" + codeType +'\'' +
                ", isDelivered=" + isDelivered +
                ", data='" + expeditionDetailList + '\'' +
                '}';
    }
}
