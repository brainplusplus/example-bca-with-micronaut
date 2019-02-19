package bank.transaction.service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;

@Singleton
public class OrderService {
    @Inject
    @Named("tokdis")
    DataSource dataSource; // "warehouse" will be injected

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    public OrderService(){

    }

    /**
     * Step #1
     * @param amount from account statement detail.amount
     * */
    public void CheckToTokdis(BigDecimal amount){
        int amountValue = amount.intValue();
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        /*
         * TODO select order_summaries
         * parameter total_amount -> amount
         * parameter is_paid -> 0 yang belum bayar
         * */
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select * from order_summaries where total_amount = ? AND is_paid = 0");
            preparedStatement.setInt(1,amountValue);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                updateToTokdis(resultSet.getInt("id"));
                LOG.info("order product = {}, id= {} ",resultSet.getString("order_number"), resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null) resultSet.close();
                if(statement != null) statement.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * TODO update order_summaries and order_supplier
     * Step #2
     * @param id is field id from table order_summaries
     *
     * */

    /*
     * TODO update order_summaries
     * parameter payment_status -> PAID
     * parameter payment_verified_by -> system
     * parameter payment_verified_at ->current date time
     * is_paid -> 1 -> true/paid
     * */
    public void updateToTokdis(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_summaries set payment_status = ? , " +
                    "payment_verified_by = ? , payment_verified_at = ? , is_paid = ? where id = ?");
            preparedStatement.setString(1,"PAID");
            preparedStatement.setInt(2,0); //0 => system;
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();
            LOG.info("\n\n\n\n currentDAte = {}",new java.sql.Timestamp(new Date().getTime()));

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null) resultSet.close();
                if(statement != null) statement.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_supplier set order_status = ? where summaries_id = ?");
            preparedStatement.setString(1,"Waiting Confirmation");//waiting confirm <DO NOT FORGET TO CHANGE THIS>
            preparedStatement.setInt(2, id); // id summaries_id;
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null) resultSet.close();
                if(statement != null) statement.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * auto check expired time every 10s
     * */
    public void autoUpdatePaymentStatusIfExpired(){

        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select * from order_summaries where payment_expired_at < ?"); //expired_payment-date -> payment_expired_at
            preparedStatement.setTimestamp(1 ,new java.sql.Timestamp(new Date().getTime()));
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                updateOrderSummaries(resultSet.getInt("id"));//id order supplier
                updateOrderSupplier(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @params id
     * update column
     * payment status => NOT PAID,
     * payment verified by => 0 (System),
     * payment verified at => new Date() <p>Date Time<p/>
     * from table order_summaries base on id
     * */
    public void updateOrderSummaries(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_summaries set payment_status = ? , " +
                    "payment_verified_by = ? , payment_verified_at = ? where id = ?");
            preparedStatement.setString(1,"NOT PAID");
            preparedStatement.setInt(2,0); //0 => system;
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            preparedStatement.setInt(4, id);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null) resultSet.close();
                if(statement != null) statement.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * update column order_status => Canceled
     * base on summeries_id frpm table order_supplier
     * */
    public void updateOrderSupplier(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_supplier set order_status = ? where summaries_id = ?");
            preparedStatement.setString(1,"Canceled");//waiting confirm <DO NOT FORGET TO CHANGE THIS>
            preparedStatement.setInt(2, id); // id summaries_id;
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally{
            try {
                if(resultSet != null) resultSet.close();
                if(statement != null) statement.close();
                if(con != null) con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
