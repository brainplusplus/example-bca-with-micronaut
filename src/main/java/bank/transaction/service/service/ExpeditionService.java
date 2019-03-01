package bank.transaction.service.service;

import bank.transaction.service.expedition.Expedition;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micronaut.http.MediaType;
import io.micronaut.spring.tx.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Singleton
public class ExpeditionService {
    @Inject
    @Named("tokdis")
    DataSource dataSource; // "warehouse" will be injected
    private static final Logger LOG = LoggerFactory.getLogger(AccountStatementService.class);
    private final String HOST_NAME= "http://13.250.223.74:3002";
    private final String PATH_TRACK= "/api/v1/tracking/";
    protected Expedition expedition;

    public ExpeditionService(Expedition expedition){this.expedition = expedition; }

    @Transactional
    public void CheckTracking() throws Exception {
        List<HashMap<String,String>> hashMapList =  getListOfAwbNumber();
        String url = "";
        for (HashMap<String,String> list: hashMapList) {
            String kurir = list.get("kurir");

            url = HOST_NAME+PATH_TRACK+list.get("kurir")+"/"+list.get("awbNumber");
            LOG.info("\n\n\nURL = {}",url);

            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            connection.setRequestMethod("GET");

            //add request header
            connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);

            int responseCode = connection.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            //print result
            expedition = new ObjectMapper().readValue(response.toString(), Expedition.class);

            /*
             * DO
             * delivery_status = "DELIVERED"
             * order_status = "WAITING CONFIRMATION APALAH"
             * comfirmed_expired_at = delivered_at + 48 hours
             * is_delivered = 1
             * delivered_at = new date
             * */
            if(expedition.getDelivered()){
                try{
                    updateOrderSupplierIfTrackedNumber(list.get("awbNumber"), list.get("kurir"));//
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }



//            LOG.info("\n\n\nPRINT RESULT expedition = {}", expedition);
//            LOG.info("\n\n\nPRINT RESULT expedition detail= {}", expedition.getExpeditionDetailList());
        }
    }

    public List<HashMap<String,String>> getListOfAwbNumber(){
        List<HashMap<String,String>> listawbnumber = new ArrayList<>();
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("SELECT a.shipping_data->'$.name' as kurir, a.awb_number FROM order_suppliers a "+
                    "JOIN order_summaries b on (a.summaries_id = b.id) WHERE b.payment_status = 1 AND b.is_paid = 1 AND a.supplier_feedback_at is not null "+
                    "AND a.is_delivered = 0 AND a.order_status in (4,5);");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                HashMap<String, String> map = new HashMap<>();
                String str = resultSet.getString("kurir").replace("\"", "").toLowerCase();
                if(str.equals("jnt"))str="jet";
                map.put("kurir",str);
                map.put("awbNumber",resultSet.getString("awb_number"));
                listawbnumber.add(map);
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

        return listawbnumber;
    }

    /*
     * DO
     * delivery_status = "DELIVERED"
     * order_status = "WAITING CONFIRMATION APALAH"
     * confirmed_expired_at = delivered_at + 48 hours
     * is_delivered = 1
     * delivered_at = new date
     * */
    public void updateOrderSupplierIfTrackedNumber(String awbnumber, String kurir){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_suppliers set " +
                    "delivery_status = ? , " +
                    "order_status = ? , " +
                    "confirmed_expired_at = DATE_ADD(delivered_at, INTERVAL 2 DAY) , " +
                    "is_delivered = ? , " +
                    "delivered_at = ? " +
                    "where awb_number = ? AND shipping_data->'$.name' = ? ");
            preparedStatement.setInt(1,1);
            preparedStatement.setInt(2,5);//waiting confirmation
            preparedStatement.setInt(3, 1);
            preparedStatement.setTimestamp(4, new java.sql.Timestamp(new Date().getTime()));
            preparedStatement.setString(5, awbnumber); // id awb_numer;
            preparedStatement.setString(6, kurir.toUpperCase()); // id awb_numer;
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
