package com.group4.cursus.service;

import com.group4.cursus.config.ZaloPayConfig;
import com.group4.cursus.utils.HMACUtil;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ZaloPayService {


    /**
     * Create a payment order with ZaloPay.
     *
     * @param appuser The user making the payment.
     * @param amount The amount to be paid.
     * @param order_id The order ID.
     * @return A map containing the ZaloPay response including return message, order URL, return code, transaction token, and app transaction ID.
     * @throws Exception If an error occurs during the payment creation process.
     */
    public Map<String, Object> createPayment(String appuser, Long amount, Long order_id) throws Exception {
        Map<String, Object> zalopay_Params = new HashMap<>();
        zalopay_Params.put("appid", ZaloPayConfig.APP_ID);
        zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + order_id);  // Sử dụng order_id từ cơ sở dữ liệu
        zalopay_Params.put("apptime", System.currentTimeMillis());
        zalopay_Params.put("appuser", appuser);
        zalopay_Params.put("amount", amount);
        zalopay_Params.put("description", "Thanh toan don hang #" + order_id);
        zalopay_Params.put("bankcode", "");
        String item = "[{\"itemid\":\"knb\",\"itemname\":\"kim nguyen bao\",\"itemprice\":198400,\"itemquantity\":1}]";
        zalopay_Params.put("item", item);

        Map<String, String> embeddata = new HashMap<>();
        embeddata.put("merchantinfo", "eshop123");
        embeddata.put("promotioninfo", "");
        embeddata.put("redirecturl", ZaloPayConfig.REDIRECT_URL);

        Map<String, String> columninfo = new HashMap<>();
        columninfo.put("store_name", "E-Shop");
        embeddata.put("columninfo", new JSONObject(columninfo).toString());
        zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

        String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
                + zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
                + zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
                + zalopay_Params.get("item");
        zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, ZaloPayConfig.KEY1, data));

        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(ZaloPayConfig.CREATE_ORDER_URL);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }
        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<>();
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("orderurl", result.get("orderurl"));
        kq.put("returncode", result.get("returncode"));
        kq.put("zptranstoken", result.get("zptranstoken"));
        kq.put("apptransid", zalopay_Params.get("apptransid")); // thêm thông tin apptransid
        return kq;
    }


    /**
     * Get the current time string in the specified format.
     *
     * @param format The date format.
     * @return The current time string in the specified format.
     */
    private static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }
}
