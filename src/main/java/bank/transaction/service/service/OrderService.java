/**
 * Copyright (c) 2019. PT. Distributor Indonesia Unggul. All rights reserverd.
 *
 * This source code is an unpublished work and the use of  a copyright  notice
 * does not imply otherwise. This source  code  contains  confidential,  trade
 * secret material of PT. Distributor Indonesia Unggul.
 * Any attempt or participation in deciphering, decoding, reverse  engineering
 * or in any way altering the source code is strictly  prohibited, unless  the
 * prior  written consent of Distributor Indonesia Unggul. is obtained.
 *
 * Unless  required  by  applicable  law  or  agreed  to  in writing, software
 * distributed under the License is distributed on an "AS IS"  BASIS,  WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or  implied.  See  the
 * License for the specific  language  governing  permissions  and limitations
 * under the License.
 *
 * Author : Bobby
 */

package bank.transaction.service.service;

import bank.transaction.service.domain.Email;
import bank.transaction.service.domain.EmailOrderDetail;
import bank.transaction.service.domain.EmailPayment;
import bank.transaction.service.expedition.Order;
import bank.transaction.service.expedition.OrderDetail;
import bank.transaction.service.repository.OrderServiceRepository;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.IntStream;

@Singleton
public class OrderService implements OrderServiceRepository {
    @Inject
    @Named("tokdis")
    DataSource dataSource; // "warehouse" will be injected

    @Inject
    @Named("maintokdis")
    DataSource dataSourceTokdisdev;

    private static final Logger LOG = LoggerFactory.getLogger(OrderService.class);
    public OrderService(){

    }

    /**
     * Step #1
     * @param amount from account statement detail.amount
     * */
    @Override
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
            preparedStatement = con.prepareStatement("select * from order_summaries where total_amount = ? AND is_paid = 0 AND is_cancelled = 0 AND payment_expired_at >= NOW() AND payment_status in (0,3)");
            preparedStatement.setInt(1,amountValue);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Order order = new Order();
                updateToTokdis(resultSet.getInt("id"));
                updateOrderSuppliers(resultSet.getInt("id"));

                HashMap<String,List> result = getOrderProductDetail(resultSet.getInt("id"));
                List<OrderDetail> orderDetails =  result.get("orderDetailsList");
                order =  getInformationToEmail(resultSet.getInt("reseller_code"),order);
                order.setOrderNumber(resultSet.getString("order_number"));
                order.setTotalPayment(resultSet.getInt("total_amount"));
                order.setTotalPrice(resultSet.getInt("subtotal"));
                order.setShippingAddress(getAddress(resultSet.getInt("id")));
//                order.setEmailTo("bobby@tokodistributor.com");
                order.setPpn(0);
                order.setOrderDetailList(orderDetails);
                LOG.info("\n\n\norderDetails ---------------------------------- \n {}",orderDetails);
                SendMailNotification(
                        order,"confirm"
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
            preparedStatement.setInt(1,1);
            preparedStatement.setInt(2,0); //0 => system;
            preparedStatement.setTimestamp(3, new java.sql.Timestamp(new Date().getTime()));
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, id);
            preparedStatement.executeUpdate();
            LOG.info("\n\n\n\nCurrentDAte = {}",new java.sql.Timestamp(new Date().getTime()));
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
            preparedStatement = con.prepareStatement("update order_suppliers set order_status = ? where summaries_id = ?");
            preparedStatement.setInt(1,1);//waiting confirm <DO NOT FORGET TO CHANGE THIS>
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
    @Override
    public void autoUpdatePaymentStatusIfExpired(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select * from order_summaries where payment_status in(0,2) AND payment_expired_at <= ? AND is_paid = 0 AND is_cancelled = 0"); //expired_payment-date -> payment_expired_at
            preparedStatement.setTimestamp(1, new java.sql.Timestamp(new Date().getTime()));
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                updateOrderSummaries(resultSet.getInt("id"));//id order supplier
                updateOrderStatusAtOrderSupplier(resultSet.getInt("id"));
                HashMap<String,List> result = getOrderProductDetail(resultSet.getInt("id"));

                List<HashMap<String,Integer>> itemQtyList = result.get("itemQtyList");
                String qlstringCase = "case ";
                String qlstringWhere = " where id in (";

                int lastloop = itemQtyList.size()-1;
                int index = 0 ;
                for (HashMap<String,Integer> map: itemQtyList) {
                    qlstringCase = qlstringCase +
                            "when id = "+map.get("id_item")+" then (quantity +"+map.get("qty")+") ";
                    if(lastloop == index){
                        qlstringWhere = qlstringWhere+map.get("id_item");
                    }
                    else{
                        qlstringWhere = qlstringWhere+map.get("id_item")+",";
                    }

                    index++;
                }
                qlstringCase = qlstringCase+"end";
                qlstringWhere = qlstringWhere+")";

                UpdateItemQuantity(qlstringCase,qlstringWhere);


                Order order = new Order();
//                HashMap<String,List> result = getOrderProductDetail(resultSet.getInt("id"));
                List<OrderDetail> orderDetails =  result.get("orderDetailsList");
                order =  getInformationToEmail(resultSet.getInt("reseller_code"),order);
                order.setOrderNumber(resultSet.getString("order_number"));
                order.setTotalPayment(resultSet.getInt("total_amount"));
                order.setTotalPrice(resultSet.getInt("subtotal"));
                order.setShippingAddress(getAddress(resultSet.getInt("id")));
//                order.setEmailTo("bobby@tokodistributor.com");
                order.setPpn(0);
                order.setOrderDetailList(orderDetails);
                LOG.info("\n\n\norderDetails ---------------------------------- \n {}",orderDetails);
//                SendMailNotification(
//                        order,"failed"
//                );
//                updateOrderSummariesIfCancelled(resultSet.getInt("id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
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
     * payment status => 2
     * is_cancelled = 1
     * */
    public void updateOrderSummaries(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_summaries set payment_status = ? , " +
                     "is_cancelled = ? where id = ?");
            preparedStatement.setInt(1,2);
            preparedStatement.setInt(2,1);
            preparedStatement.setInt(3, id);
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
     * update if ordrder expired
     * */
    public void updateOrderStatusAtOrderSupplier(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_suppliers set order_status = 2  " +
                    " where summaries_id = ?");
            preparedStatement.setInt(1,id);
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
     * TODO if payment is success, update supplier_feedback_expired_at and order_status
     * @params id -> summaries_id
     * */
    public void updateOrderSuppliers(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_suppliers set supplier_feedback_expired_at = DATE_ADD(SYSDATE(), INTERVAL 2 DAY)  ," +
                    "supplier_feedback_expired_time = UNIX_TIMESTAMP(DATE_ADD(SYSDATE(), INTERVAL 2 DAY)) ," +
                    "order_status = ?    where summaries_id = ?");
            preparedStatement.setInt(1,1);
            preparedStatement.setInt(2, id);
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
            preparedStatement = con.prepareStatement("update order_suppliers set order_status = ? where summaries_id = ?");
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

    public void SendMailNotification(Order order, String status) throws Exception {
        String URL_TOKDIS ="http://13.250.223.74:3001/api/v1/email/transaction/"+status;
        URL obj = new URL(URL_TOKDIS);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", MediaType.APPLICATION_JSON);

//        String urlParameters = "order_number="+order.getOrderNumber()+"&email_to="+order.getEmailTo()+"&customer_name="+order.getCustomerName()+"&shipping_address="+order.getShippingAddress()+"&phone_number="+order.getPhoneNo()+"&total_price="+order.getTotalPrice()+"&total_payment="+order.getTotalPayment();
//        LOG.info("\n\n\nORDER  -> {} ",urlParameters);
        // Send post request
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(order.toString());
        wr.flush();
        wr.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        LOG.info("\n\n\n Response => {}",response);
        in.close();
    }

    public Order getInformationToEmail(int id,Order order){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        HashMap map = new HashMap();


        try {
            con = dataSourceTokdisdev.getConnection();
            preparedStatement = con.prepareStatement("select * from reseller where id = ? ");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                order.setEmailTo(resultSet.getString("email_address"));
                order.setCustomerName(resultSet.getString("fullname"));
                order.setPhoneNo(resultSet.getString("handphone"));

                map.put("emailTo",resultSet.getString("email_address"));
                map.put("customerName",resultSet.getString("fullname"));
//                map.put("shippingAddress",resultSet.getString("fullname")); //order supplier->receiver
                map.put("phoneNo",resultSet.getString("handphone"));
//                map.put("totalPrice",resultSet.getString("handphone")); //order summaries ?
//                map.put("totalPayment",resultSet.getString("handphone")); //order summaries ?
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
        return order;
    }


    public String getAddress(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        String alamat = "";
        HashMap map = new HashMap();

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select receiver->'$.address' as alamat from order_suppliers where summaries_id = ? LIMIT 1 ");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                alamat = resultSet.getString("alamat");
                LOG.info("\n\n\nALAMAT == {}", alamat);
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
        return alamat;
    }

    public  HashMap<String,List> getOrderProductDetail(int id){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<OrderDetail> tampung = new ArrayList<>();
        List<HashMap<String,Integer>> itemAndQty = new ArrayList<>();
        HashMap<String,List> result = new HashMap<>();

        LOG.info("\n\n\n ID =========================================================");
        LOG.info("\n\n\n ID = {}",id);
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select a.product_data, a.product_variation->'$[*].id' as id_item, a.product_variation->'$[*].qty' as qty from order_products a join order_suppliers b on a.order_supplier_id = b.id join order_summaries c on b.summaries_id = c.id where c.id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            int index = 0;
            while(resultSet.next()){
                String jsonString = resultSet.getString("product_data");
                String getItemList = resultSet.getString("id_item");
                String getQtyList = resultSet.getString("qty");
                LOG.info("\n\n\nGET ITEM LIST INDEX[{}] => {}",index,getItemList);
                LOG.info("\nGET QTY LIST INDEX[{}] => {}",index,getQtyList);
                LOG.info("\n-------------------------------------------------------");
                getItemList = getItemList.replace("[","").replace("]","").replace(" ","");
                getQtyList = getQtyList.replace("[","").replace("]","").replace(" ","");
                List<String> idItemList = new ArrayList<>(Arrays.asList(getItemList.split(",")));
                List<String> qtyItemList = new ArrayList<>(Arrays.asList(getQtyList.split(",")));

                IntStream.range(0, idItemList.size())
                        .forEach(idx ->
                                {
                                    HashMap<String,Integer> map = new HashMap();
                                    map.put("id_item",Integer.parseInt(idItemList.get(idx)));
                                    map.put("qty",Integer.parseInt(qtyItemList.get(idx)));
                                    itemAndQty.add(map);
                                }
                        )
                ;
                LOG.info("\n\n\n itemAndQty == {}",itemAndQty);

//                for(int a = 0; a < idItemList.size(); a++){
//                }
//                LOG.info("\n\n\nItem List -> {} ",idItemList);
//                LOG.info("\n\n\nQty List -> {} ",qtyItemList);

                ObjectMapper mapper = new ObjectMapper();
                OrderDetail orderDetail = mapper.readValue(jsonString, OrderDetail.class);
                tampung.add(orderDetail);

            }
            result.put("orderDetailsList",tampung);
            result.put("itemQtyList",itemAndQty);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        return result;
    }

    @Override
    @Transactional
    public void updateOrderStatusToDone(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_suppliers set order_status = ? , confirmed_at = NOW() where confirmed_expired_at <= NOW() AND confirmed_at is null AND order_status = 5 ");
            preparedStatement.setInt(1,6);//Order status -> DONE
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

    @Transactional
    public void updateOrderStatusRejected(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        UpdateQuantityOfItemWhenRejectOrder();
        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_suppliers set is_rejected = 1 , order_status = 2, supplier_feedback_at = CURDATE(), is_rejected_by_system = 1, rejected_at = NOW(), reason_rejected = 'SYSTEM_REJECTED_REASON_1' where supplier_feedback_expired_at <= CURDATE() AND supplier_feedback_at is null AND order_status = 1 ");
//            preparedStatement.setInt(1,6);//Order status -> DONE
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

    public List<HashMap<String,Integer>> getAllItemWhereWantToUpdateIt(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<HashMap<String,Integer>> itemAndQty = new ArrayList<>();

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select b.product_variation->'$[*].id' as id_item, b.product_variation->'$[*].qty' as qty from order_suppliers a join order_products b on b.order_supplier_id = a.id where a.supplier_feedback_expired_at < CURDATE() AND a.supplier_feedback_at is null AND a.order_status = 1");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                HashMap<String,Integer> map = new HashMap();
                map.put("id_item",resultSet.getInt("id_item"));
                map.put("qty",resultSet.getInt("qty"));
                LOG.info("\n\nID ITEM --> {}",resultSet.getInt("id_item"));;
                LOG.info("\n\nqty qty --> {}",resultSet.getInt("qty"));
                itemAndQty.add(map);
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

        return itemAndQty;
    }

    public void UpdateQuantityOfItemWhenRejectOrder(){
        List<HashMap<String,Integer>> itemQtyList = getAllItemWhereWantToUpdateIt(); //METHOD

        String qlstringCase = "case ";
        String qlstringWhere = " where id in (";

        if(!itemQtyList.isEmpty()){
            int lastloop = itemQtyList.size()-1;
            int index = 0 ;
            for (HashMap<String,Integer> map: itemQtyList) {
                qlstringCase = qlstringCase +
                        "when id = "+map.get("id_item")+" then (quantity -"+map.get("qty")+") ";
                if(lastloop == index){
                    qlstringWhere = qlstringWhere+map.get("id_item");
                }
                else{
                    qlstringWhere = qlstringWhere+map.get("id_item")+",";
                }

                index++;
            }
            qlstringCase = qlstringCase+"end";
            qlstringWhere = qlstringWhere+")";
            UpdateItemQuantity(qlstringCase,qlstringWhere);
        }
    }

    @Override
    @Transactional
    public void UpdateIsRejectedIfSupplierNotSentTheOrder(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("update order_suppliers set is_shipped = 1, order_status = 2, is_rejected_by_system = 1, is_rejected = 1 ,rejected_at = NOW(), reason_rejected = 'SYSTEM_REJECTED_REASON_2' where awb_expired_at <= CURDATE() AND supplier_feedback_at  is not null ");
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

    public void UpdateOrderAllCompleted(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            for (Integer index : CheckOrderSummariesGetAllCompletedIsNull()) {
                con = dataSource.getConnection();
                preparedStatement = con.prepareStatement("select * from pdate order_suppliers set confirmed_at = CURDATE() , order_status = 6 where confirmed_expired_at < CURDATE() AND confirmed_at is null ");
                preparedStatement.executeUpdate();
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

    public List<Integer> CheckOrderSummariesGetAllCompletedIsNull(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<Integer> listOrderSummaries = new ArrayList<>();

        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select * from order_summaries where completed_at is null ");
//            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                listOrderSummaries.add(resultSet.getInt("id"));
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
        return listOrderSummaries;
    }

    public void UpdateItemQuantity(String qlStringCase, String qlStringWhere){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;

        try {
            LOG.info("QUERY \n\n\n update product_variation_details SET quantity = "+ qlStringCase + qlStringWhere);
            con = dataSourceTokdisdev.getConnection();
            preparedStatement = con.prepareStatement("update product_variation_details SET quantity = "+ qlStringCase + qlStringWhere);
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

    @Override
    @Transactional
    public void checkForReminder(){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        Email email = new Email();
        EmailOrderDetail emailOrderDetail = new EmailOrderDetail();
        EmailPayment emailPayment = new EmailPayment();
        List<EmailPayment> emailPaymentList = new ArrayList<>();

        try {

            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select id, payment_expired_at, order_number, total_amount, subtotal, transfer_unique_number, payment_data->'$.method' as method, payment_data->'$.via.BANK[0].code' as vendor, payment_data->'$.via.BANK[0].icon' as vendor_icon, payment_data->'$.via.BANK[0].name' as account_name, payment_data->'$.via.BANK[0].number' as account_number from order_summaries where payment_status = 0 AND DATE_SUB(payment_expired_at, interval 1 hour) between  NOW() AND DATE_ADD(NOW(), interval 5 minute) AND is_paid = 0 AND is_cancelled = 0");
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Timestamp timestamp = resultSet.getTimestamp("payment_expired_at");
                Date paymentExpiredAt = new Date(timestamp.getTime());
                String tanggal = new SimpleDateFormat("EEEE, dd MMMM yyyy").format(paymentExpiredAt);
                String jam = new SimpleDateFormat("hh.mm").format(paymentExpiredAt);
                //TODO set values to EmailPayment
                emailPayment.setMethod(resultSet.getString("method"));
                emailPayment.setVendor(resultSet.getString("vendor"));
                emailPayment.setVendorIcon(resultSet.getString("vendor_icon "));
                emailPayment.setAccountName(resultSet.getString("account_name"));
                emailPayment.setAccountNo(resultSet.getString("aacount_number"));
                emailPayment.setVendorBranch("Jelambar");
                emailPaymentList.add(emailPayment);

                //TODO set values to Object
                email.setEmailPaymentList(emailPaymentList);
                email.setLastPaymentDate(tanggal);
                email.setLastPaymentHour(jam);
                email.setOrderNumber(resultSet.getString("order_number"));
                email.setTotalPayment(resultSet.getInt("total_amount"));
                email.setSubtotal(resultSet.getInt("subtotal"));
                email.setUniqueCode(resultSet.getInt("transfer_unique_number"));

                Email updateValues = GetEmailOrderDetailForReminder(resultSet.getInt("id"), email);

                updateValues = sendEmailReminder(resultSet.getInt("id"), updateValues);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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

    public Email GetEmailOrderDetailForReminder(int id,Email email){
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        List<EmailOrderDetail> emailOrderDetailList = new ArrayList<>();


        try {
            con = dataSource.getConnection();
            preparedStatement = con.prepareStatement("select a.product_variation->'$[*].name' as name, a.product_data->'$.price' as price, a.product_variation->'$[*].qty' as qty from order_products a join order_suppliers b on a.order_supplier_id = b.id join order_summaries c on b.summaries_id = ?");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
               String getListName = resultSet.getString("name");
               String getListQty = resultSet.getString("qty");
               int price = resultSet.getInt("price");

               getListName = getListName.replace("[","").replace("]","").replace(" ","");
               getListQty = getListQty.replace("[","").replace("]","").replace(" ","");
               List<String> itemNameList = new ArrayList<>(Arrays.asList(getListName.split(",")));
               List<String> qtyItemList = new ArrayList<>(Arrays.asList(getListQty.split(",")));


                IntStream.range(0, itemNameList.size())
                        .forEach(idx ->
                                {
                                    EmailOrderDetail emailOrderDetail = new EmailOrderDetail();
                                    emailOrderDetail.setName(itemNameList.get(idx));
                                    emailOrderDetail.setPrice(price);
                                    emailOrderDetail.setQty(Integer.parseInt(qtyItemList.get(idx)));
                                    emailOrderDetail.setTotal(price * Integer.parseInt(qtyItemList.get(idx)));
                                    emailOrderDetail.setEmail(email);
                                    emailOrderDetailList.add(emailOrderDetail);
                                }
                        );
                email.setEmailOrderDetailList(emailOrderDetailList);


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

        return email;
    }


    public Email sendEmailReminder(int id, Email email) throws IOException {
        Connection con = null;
        Statement statement = null;
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        HashMap map = new HashMap();


        try {
            con = dataSourceTokdisdev.getConnection();
            preparedStatement = con.prepareStatement("select * from reseller where id = ? ");
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                email.setEmailTo(resultSet.getString("email_address"));
                email.setCustomerName(resultSet.getString("fullname"));
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

        return email;
    }

//    public void medium(){
//        List<String> namaMahasisawa = new ArrayList<>();
//        namaMahasisawa.add("Akyong");
//        namaMahasisawa.add("Bobby");
//        namaMahasisawa.add("Jeni");
//        namaMahasisawa.add("Johan");
//
//        for (int a = 0; a< namaMahasisawa.size(); a++) {
//            System.out.println("Nama Mahasiswa -> "+ namaMahasisawa.get(a));
//        }
//    }

}
