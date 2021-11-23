package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.ItemCollection;
import com.amazonaws.services.dynamodbv2.document.PrimaryKey;
import com.amazonaws.services.dynamodbv2.document.QueryOutcome;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

    private Table table;

    public FeedDAO(Table feedTable) {
        this.table = feedTable;
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
    public PostStatusResponse postFeed(List<User> followers, PostStatusRequest request) {
        Status status = request.getNewStatus();
        User statusUser = status.getUser();
        String datetime = status.getDatetime();
        long timestamp = Utils.stringToEpoch(datetime);

        try {
            for (User follower : followers) {
                Item item = new Item()
                        .withPrimaryKey("alias", follower.getAlias())
                        .withLong("timestamp", timestamp)
                        .withString("statusUserAlias", statusUser.getAlias())
                        .withString("statusUserFirstName", statusUser.getFirstName())
                        .withString("statusUserLastName", statusUser.getLastName())
                        .withString("statusUserImageUrl", statusUser.getImageUrl())
                        .withString("post", status.getPost())
                        .withString("datetime", status.getDatetime())
                        .withList("urls", status.getUrls())
                        .withList("mentions", status.getMentions());

                table.putItem(item);
            }
        } catch (Exception e) {
            throw new RuntimeException("Fail to post a feed");
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
