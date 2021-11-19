package edu.byu.cs.tweeter.server.dao.dynamoDB;

import com.amazonaws.services.dynamodbv2.document.Table;

import java.util.List;

import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;
import edu.byu.cs.tweeter.server.dao.IFeedDAO;
import edu.byu.cs.tweeter.server.util.FakeData;
import edu.byu.cs.tweeter.server.util.Pair;

public class FeedDAO implements IFeedDAO {

    private Table table;

    public FeedDAO(Table feedTable) {
        this.table = feedTable;
    }

    @Override
    public GetFeedResponse getFeed(GetFeedRequest request) {
        assert request.getLimit() > 0;
        assert request.getUserAlias() != null;
        Pair<List<Status>, Boolean> feed = getDummyStatuses(request.getLastStatus(), request.getLimit());

        return new GetFeedResponse(feed.getSecond(), feed.getFirst());
    }

    @Override
    public PostStatusResponse postFeed(PostStatusRequest request) {
        return new PostStatusResponse();
    }

    Pair<List<Status>, Boolean> getDummyStatuses(Status lastItem, int limit) {
        return getFakeData().getPageOfStatus(lastItem, limit);
    }

    FakeData getFakeData() {
        return new FakeData();
    }

}
