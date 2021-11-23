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
import edu.byu.cs.tweeter.model.net.request.GetStoryRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetStoryResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IStoryDAO;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;
import edu.byu.cs.tweeter.server.util.Utils;

public class StoryDAO implements IStoryDAO {

    private static final String PARTITION_KEY = "alias";
    private static final String SORT_KEY = "timestamp";

    private Table table;

    public StoryDAO(Table storyTable) {
        this.table = storyTable;
    }

    @Override
    public GetStoryResponse getStory(GetStoryRequest request) {
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
                User user = new User(item.getString("firstName"), item.getString("lastName"), item.getString("alias"), item.getString("imageURL"));
                Status status = new Status(item.getString("post"), user, item.getString("datetime"), item.getList("urls"), item.getList("mentions"));
                statuses.add(status);
            }
        } catch (Exception  e) {
            throw new RuntimeException("Failed to get stories");
        }
        return new GetStoryResponse(Utils.checkHasMore(items), statuses);
    }

    @Override
    public PostStatusResponse postStory(PostStatusRequest request) {
        try {
            Status status = request.getNewStatus();
            User user = status.getUser();
            String datetime = status.getDatetime();
            long timestamp = Utils.stringToEpoch(datetime);


            Item item = new Item()
                    .withPrimaryKey("alias", user.getAlias())
                    .withLong("timestamp", timestamp)
                    .withString("firstName", user.getFirstName())
                    .withString("lastName", user.getLastName())
                    .withString("imageURL", user.getImageUrl())
                    .withString("datetime", datetime)
                    .withString("post", status.getPost())
                    .withList("urls", status.getUrls())
                    .withList("mentions", status.getMentions());

            table.putItem(item);
        } catch (Exception e) {
            throw new RuntimeException("Failed to post a story");
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
