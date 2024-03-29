package bank.transaction.service.service;

import bank.transaction.service.domain.AccountStatement;
import bank.transaction.service.domain.AccountStatementDetail;
import io.micronaut.spring.tx.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashMap;

public class AccountStatementService {
    @PersistenceContext
    private EntityManager entityManager;
    private static final Logger LOG = LoggerFactory.getLogger(AccountStatementService.class);

    public AccountStatementService(EntityManager entityManager){
        this.entityManager = entityManager;
    }

    /**
     * @param accountStatement a list of account statement that receive from api.klikbca.com
     * @return accountStatement a list of account statement where already save
     * */
    @Transactional
    public AccountStatement saveConditional(@NotNull AccountStatement accountStatement){
        HashMap map = CheckIfCurrentDateAlreadyExist(accountStatement.getStartDate(),accountStatement.getEndDate());
        LOG.info("\n\n\n Account Statement -> {}",accountStatement);
        LOG.info("\n\n\n MAP -> {}",map.get(map.get("notExist")));
        if((Boolean) map.get("notExist") == true){
            LOG.info("\n\n\n\n Not exist = true");
            entityManager.persist(accountStatement);
            for (AccountStatementDetail detail: accountStatement.getAccountStatementDetailList() ) {
                detail.setAccountStatement(accountStatement);
                entityManager.persist(detail);
            }
            return accountStatement;
        }
        else{

            LOG.info("\n\n\n\n Not exist = false");
            AccountStatement accountStatementInstance = (AccountStatement) map.get("result");

            for (AccountStatementDetail detail: accountStatement.getAccountStatementDetailList() ) {
                Query query = entityManager.createQuery("SELECT a FROM AccountStatementDetail as a WHERE a.accountStatement = :accountStatement AND a.transactionType = :transactionType AND a.transactionDate = :transactionDate AND a.branchCode = :branchCode AND a.amount = :transactionAmount AND a.name = :transactionName AND a.remark = :trailer")
                        .setParameter("accountStatement",accountStatementInstance)
                        .setParameter("transactionType",detail.getTransactionType())
                        .setParameter("transactionDate",detail.getTransactionDate())
                        .setParameter("branchCode",detail.getBranchCode())
                        .setParameter("transactionAmount",detail.getAmount())
                        .setParameter("transactionName",detail.getName())
                        .setParameter("trailer",detail.getRemark());
                if(query.getResultList().isEmpty()){
                    detail.setAccountStatement(accountStatementInstance);
                    entityManager.persist(detail);
                }
                else{
                    LOG.info("\n\n\nThis Account Statement Detail already exist in DB ::: {}");
                }

            }

            return accountStatement;
        }
    }

//    public AccountStatement saveConditional(@NotNull Date startDate, @NotNull Date endDate, @NotNull String currency, @NotNull BigDecimal startBalance){
//        boolean notExist = CheckIfCurrentDateAlreadyExist(startDate,endDate);
//        if(notExist){
//                AccountStatement accountStatement = CreateAccountStatement(startDate,endDate,currency,startBalance);
////                DataTransaction dataTransaction = CreateDataTransaction.
//            return accountStatement;
//        }
//        else{
//            return null;
//        }
//    }

    /**
     * @params startDate a date which giving start date of account statement
     * @params endDate a date which giving end date of account statement
     * @return HashMap with key as `notExist` as Boolean and `result` as a list of Account Statement
     * */
    @Transactional
    public HashMap CheckIfCurrentDateAlreadyExist(@NotNull Date startDate, @NotNull Date endDate){
        HashMap map = new HashMap();
        if(startDate.compareTo(endDate) == 0){
            Query query = entityManager.createQuery("SELECT a FROM AccountStatement as a WHERE a.startDate = :startDate").setParameter("startDate",startDate);
            if(query.getResultList().isEmpty()){
                map.put("notExist",true);
                map.put("result",null);
                return map;
            }
            else{
                LOG.info("\n\n=========== IN CheckIfCurrentDateAlreadyExist => {}",query.getResultList().get(0));
                map.put("notExist",false);
                map.put("result", query.getResultList().get(0));
                return map;
            }
        }
        else{
            map.put("notExist",false);
            map.put("result",null);
            return map;
        }
    }

//    @Transactional
//    public AccountStatement CreateAccountStatement(@NotNull Date startDate, @NotNull Date endDate, @NotNull String currency, @NotNull BigDecimal startBalance){
//        AccountStatement accountStatement = new AccountStatement(startDate,endDate,currency,startBalance );
//        entityManager.persist(accountStatement);
//        List<AccountStatementDetail> accountStatementDetail = CreateAccountStatementDetail(accountStatement);
//        accountStatement.setAccountStatementDetailList(accountStatementDetail);
//        entityManager.persist(accountStatementDetail);
//        return accountStatement;
//    }

//    @Transactional
//    public boolean CheckIfDetailAlreadyExist(@NotNull AccountStatement accountStatement){
//        boolean result = false;
//        for (AccountStatementDetail detail: accountStatement.getAccountStatementDetailList() ) {
//            Query query = entityManager.createQuery("SELECT a FROM AccountStatementDetail as a WHERE a.accountStatement = :accountStatement AND a.transactionType = :transactionType AND a.transactionDate = :transactionDate AND a.branchCode = :branchCode AND a.transactionAmount = :transactionAmount")
//                    .setParameter("accountStatement",accountStatement)
//                    .setParameter("transactionType",detail.getTransactionType())
//                    .setParameter("transactionDate",detail.getTransactionDate())
//                    .setParameter("branchCode",detail.getBranchCode())
//                    .setParameter("transactionAmount",detail.getAmount());
//            if(query.getResultList().isEmpty()){
//                result =  true; //if havenot insert
//            }
//            else{
//                LOG.info("\n\nHasil SELECT a FROM AccountStatementDetail => {}",query.getResultList().get(0));
//                result =  false; //if already inserted
//            }
//        }
//        return result;
//    }

//    @Transactional
//    public List<AccountStatementDetail> CreateAccountStatementDetail(@NotNull AccountStatement accountStatement){
//        List<AccountStatementDetail> list = new ArrayList<>();
//        LOG.info("\n\na-------------- accountStatement => {}",accountStatement.getAccountStatementDetailList());
//        for (AccountStatementDetail detail: accountStatement.getAccountStatementDetailList() ) {
//            Query query = entityManager.createQuery("SELECT a FROM AccountStatementDetail as a WHERE a.accountStatement = :accountStatement AND a.transactionType = :transactionType AND a.transactionDate = :transactionDate AND a.branchCode = :branchCode AND a.transactionAmount = :transactionAmount")
//                    .setParameter("accountStatement",accountStatement)
//                    .setParameter("transactionType",detail.getTransactionType())
//                    .setParameter("transactionDate",detail.getTransactionDate())
//                    .setParameter("branchCode",detail.getBranchCode())
//                    .setParameter("transactionAmount",detail.getAmount());
//            if(query.getResultList().isEmpty()){
//                AccountStatementDetail accountStatementDetail = detail;
//                entityManager.persist(accountStatementDetail);
//                list.add(accountStatementDetail);
//            }
//            else{
//                LOG.info("\n\nHasil SELECT a FROM AccountStatementDetail => {}",query.getResultList().get(0));
//            }
//        }
//        return list;
//    }
}
