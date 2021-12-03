package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.BatchWriteItemOutcome;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.TableWriteItems;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.dynamodbv2.model.WriteRequest;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;
import edu.byu.cs.tweeter.server.util.Utils;

public class FeedDAO implements IFeedDAO {

    private static final String PARTITION_KEY = "alias";
    private static final String SORT_KEY = "timestamp";

    private static final String TABLE_NAME = "Feed";

    private Table table;
    private DynamoDB dynamoDB;

    public FeedDAO(Table feedTable, DynamoDB dynamoDB) {
        this.table = feedTable;
        this.dynamoDB = dynamoDB;
    }

    @Override
    public GetFeedResponse getFeed(GetFeedRequest request) {
        ItemCollection<QueryOutcome> items = null;
        List<Status> statuses = null;
        try {
            QuerySpec spec = Utils.getBasicSpec(PARTITION_KEY, request.getUserAlias(), request.getLimit());

            if (request.getLastStatus() != null) {
                long timestamp = Utils.stringToEpoch(request.getLastStatus().getDatetime());
                PrimaryKey lastItemKey = new PrimaryKey(PARTITION_KEY, request.getUserAlias(), SORT_KEY, timestamp);
                spec.withExclusiveStartKey(lastItemKey);
            }

            items = table.query(spec);
            Iterator<Item> iterator = items.iterator();

            Item item = null;
            statuses = new ArrayList<>();
            while (iterator.hasNext()) {
                item = iterator.next();
                User user = new User(item.getString("statusUserFirstName"), item.getString("statusUserLastName"), item.getString("statusUserAlias"), item.getString("statusUserImageUrl"));
                Status status = new Status(item.getString("post"), user, item.getString("datetime"), item.getList("urls"), item.getList("mentions"));
                statuses.add(status);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get feeds");
        }

        return new GetFeedResponse(Utils.checkHasMore(items), statuses);
    }

    @Override
    public PostStatusResponse postFeed(List<String> followerAliases, Status status) {
        User statusUser = status.getUser();
        String datetime = status.getDatetime();
        long timestamp = Utils.stringToEpoch(datetime);

        try {
            TableWriteItems items = new TableWriteItems(TABLE_NAME);
            for (String alias : followerAliases) {
                Item item = new Item()
                        .withPrimaryKey("alias", alias)
                        .withLong("timestamp", timestamp)
                        .withString("statusUserAlias", statusUser.getAlias())
                        .withString("statusUserFirstName", statusUser.getFirstName())
                        .withString("statusUserLastName", statusUser.getLastName())
                        .withString("statusUserImageUrl", statusUser.getImageUrl())
                        .withString("post", status.getPost())
                        .withString("datetime", status.getDatetime())
                        .withList("urls", status.getUrls())
                        .withList("mentions", status.getMentions());

                items.addItemToPut(item);

                if (items.getItemsToPut() != null && items.getItemsToPut().size() == 25) {
                    loopBatchWrite(items);
                    items = new TableWriteItems(TABLE_NAME);
                }
            }

            // Write any leftover items
            if (items.getItemsToPut() != null && items.getItemsToPut().size() > 0) {
                loopBatchWrite(items);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to post a feed");
        }
        return new PostStatusResponse();
    }

    private void loopBatchWrite(TableWriteItems items) {
        BatchWriteItemOutcome outcome = dynamoDB.batchWriteItem(items);
        System.out.println("Wrote feed batch");

        // Check the outcome for items that didn't make it onto the table
        // If any were not added to the table, try again to write the batch
        while (outcome.getUnprocessedItems().size() > 0) {
            Map<String, List<WriteRequest>> unprocessedItems = outcome.getUnprocessedItems();
            outcome = dynamoDB.batchWriteItemUnprocessed(unprocessedItems);
            System.out.println("Wrote more Feeds");
        }
    }

}
