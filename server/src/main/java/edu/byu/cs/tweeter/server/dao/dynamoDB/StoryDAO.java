package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Attribute;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.PutItemOutcome;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.QueryRequest;
import com.amazonaws.services.dynamodbv2.model.QueryResult;

import org.w3c.dom.Attr;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.lambda.HandlerConfig;
import edu.byu.cs.tweeter.server.util.DateUtil;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

public class StoryDAO implements IStoryDAO {

    private static final String ALIAS = "alias";
    private static final String TIMESTAMP = "timestamp";
    private static final String TABLE_NAME = "Story";

    private Table table;

    public StoryDAO(Table storyTable) {
        this.table = storyTable;
    }

    @Override
    public GetStoryResponse getStory(GetStoryRequest request) {
        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#p", ALIAS);

        HashMap<String, Object> valueMap = new HashMap<>();
        valueMap.put(":alias", request.getUserAlias());

        QuerySpec spec = new QuerySpec()
                .withKeyConditionExpression("#p = :alias")
                .withScanIndexForward(false)
                .withNameMap(nameMap)
                .withValueMap(valueMap)
                .withMaxResultSize(request.getLimit());

        if (request.getLastStatus() != null) {
            long timestamp = DateUtil.stringToEpoch(request.getLastStatus().getDatetime());
            PrimaryKey lastItemKey = new PrimaryKey(ALIAS, request.getUserAlias(), TIMESTAMP, timestamp);
            spec.withExclusiveStartKey(lastItemKey);
        }

        ItemCollection<QueryOutcome> items = table.query(spec);
        Iterator<Item> iterator = items.iterator();

        Item item = null;
        List<Status> statuses = new ArrayList<>();
        while (iterator.hasNext()) {
            item = iterator.next();
            User user = new User(item.getString("firstName"), item.getString("lastName"), item.getString("alias"), item.getString("imageURL"));
            Status status = new Status(item.getString("post"), user, item.getString("datetime"), item.getList("urls"), item.getList("mentions"));
            statuses.add(status);
        }

        boolean hasMore = false;
        Map<String, AttributeValue> lastItem = items.getLastLowLevelResult().getQueryResult().getLastEvaluatedKey();
        if (lastItem != null) {
            hasMore = true;
        }

        return new GetStoryResponse(hasMore, statuses);
    }

    @Override
    public PostStatusResponse postStory(PostStatusRequest request) {
        Status status = request.getNewStatus();
        User user = status.getUser();

        String alias = user.getAlias();
        String firstName = user.getFirstName();
        String lastName = user.getLastName();
        String imageURL = user.getImageUrl();
        String post = status.getPost();
        String datetime = status.getDatetime();
        long timestamp = DateUtil.stringToEpoch(datetime);;
        List<String> urls = status.getUrls();
        List<String> mentions = status.getMentions();

        try {
            Item item = new Item()
                    .withPrimaryKey("alias", alias)
                    .withLong("timestamp", timestamp)
                    .withString("firstName", firstName)
                    .withString("lastName", lastName)
                    .withString("imageURL", imageURL)
                    .withString("datetime", datetime)
                    .withString("post", post)
                    .withList("urls", urls)
                    .withList("mentions", mentions);

            table.putItem(item);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return new PostStatusResponse();
    }


    Pair<List<Status>, Boolean> getDummyStatuses(Status lastItem, int limit) {
        return getFakeData().getPageOfStatus(lastItem, limit);
    }

    FakeData getFakeData() {
        return new FakeData();
    }
}
