package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorWrapper {
    @JsonProperty("ErrorCode")
    private String errorCode;

    @JsonProperty("ErrorMessage")
    private Messages messages;

    public String getErrorCode() {
        return errorCode;
    }

    public Messages getMessages() {
        return messages;
    }

    @Override
    public String toString() {

        return "ErrorWrapper{" +
                "errorCode='" + errorCode + '\'' +
                ", messages=" + messages +
                '}';
    }
}
