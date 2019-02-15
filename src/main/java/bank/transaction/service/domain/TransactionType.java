package bank.transaction.service.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

import javax.inject.Singleton;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Singleton
public class TransactionType {
    /**
     * Debit Type
     */
    private final String DEBIT = "D";

    /**
     * Credit Type
     */
    private final String CREDIT = "C";

    private final String code;

    TransactionType(String code) {
        this.code = code;
    }

//    private static Map<String, TransactionType> transactionTypeMap = Stream.of(TransactionType.values()).collect(Collectors.toMap(
//            s -> s.code, Function.identity())
//    );

//    @JsonCreator
    public String fromString(String value) {
        return value;
    }

    @Override
    public String toString() {
        return code;
    }
}
