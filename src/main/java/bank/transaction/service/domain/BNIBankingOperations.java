package bank.transaction.service.domain;

import javax.inject.Singleton;
import java.util.Date;

@Singleton
public interface BNIBankingOperations {
    AccountStatement getStatement(String corporateId, String accountNo, Date startDate, Date endDate);
}
