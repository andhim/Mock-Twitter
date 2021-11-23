package edu.byu.cs.tweeter.server.util;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Utils {

    public static String hashPassword(String passwordToHash) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(passwordToHash.getBytes());
            byte[] bytes = md.digest();
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "FAILED TO HASH";
    }

    public static long stringToEpoch(String datetime) {
        long epoch = 0;
        try {
            SimpleDateFormat df = new SimpleDateFormat("MMM dd yyyy hh:mm:ss aa");
            Date date = df.parse(datetime);
            epoch = date.getTime();
        } catch(Exception e) {
            throw new RuntimeException("Failed to convert datetime into epoch.");
        }
        return epoch;
    }

    /**
     *  lastItem not took into account
     *
     * @param partitionKey
     * @param partitionValue
     * @param limit
     * @return
     */
    public static QuerySpec getBasicSpec(String partitionKey, String partitionValue, Integer limit) {
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#p", partitionKey);

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":partition", partitionValue);

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("#p = :partition")
                .withScanIndexForward(false)
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        if (limit != null) {
            spec = spec.withMaxResultSize(limit);
        }

        return spec;
    }

    public static boolean checkHasMore(ItemCollection<QueryOutcome> items) {
        Map<String, AttributeValue> lastItem = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
        return lastItem != null;
    }

}
