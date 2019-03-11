package bank.transaction.service.impl;

import bank.transaction.service.domain.AccountStatement;
import bank.transaction.service.domain.BNIBankingOperations;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

public class BNIBankingTemplate extends AbstractBNIOperations implements BNIBankingOperations {
    public BNIBankingTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public AccountStatement getStatement(String corporateId, String accountNo, Date startDate, Date endDate) {

        List<String> pathVariables = new ArrayList<>();
        pathVariables.add(corporateId);
        pathVariables.add(accountNo);

        Map<String, Object> requestParam = new HashMap<>();
        requestParam.put("StartDate", formatDate(startDate));
        requestParam.put("EndDate", formatDate(endDate));

        return restTemplate.getForObject(buildUrl("/banking/v3/corporates/{}/accounts/{}/statements", pathVariables, requestParam), AccountStatement.class);
    }

    private String formatDate(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(date);
    }
}
