package edu.byu.cs.tweeter.server.util;

import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.google.gson.Gson;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;


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

    public static String serialize(Object requestInfo) {
        return (new Gson()).toJson(requestInfo);
    }

    public static <T> T deserialize(String value, Class<T> returnType) {
        return (new Gson()).fromJson(value, returnType);
    }

//    public static Pair<PostStatusRequest, String[]> handleSQSEvent (SQSEvent event) {
//        List<String> followerAliases = new ArrayList<>();
//
//        //TODO: Do we ever get more than one record?
//        for (SQSEvent.SQSMessage msg : event.getRecords()) {
//            String[] requestAndAliases = msg.getBody().split("\\?");
//            String requestString = requestAndAliases[0];
//            String[] aliases = requestAndAliases[1].split(",");
//        }
//        SQSEvent.SQSMessage msg = event.getRecords().get(0);
//        String jsonString = msg.getBody();
//        return Utils.deserialize(jsonString, PostStatusRequest.class);
//    }

    public static SendMessageResult sendToSQS(String messageBody, String url) {
        SendMessageRequest request = new SendMessageRequest()
                .withQueueUrl(url)
                .withMessageBody(messageBody);
        AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
        SendMessageResult result  = sqs.sendMessage(request);

        return result;
    }

    public static final String POST_STATUS_QUEUE_URL = "https://sqs.us-west-2.amazonaws.com/754020789969/cs340PostStatusQueue";
    public static final String UPDATE_FEED_QUEUE_URL = "https://sqs.us-west-2.amazonaws.com/754020789969/cs340UpdateFeedQueue";
    public static final int BATCH_NUM = 100;

}
