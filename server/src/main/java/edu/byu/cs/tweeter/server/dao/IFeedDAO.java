package edu.byu.cs.tweeter.server.dao;

import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.PostStatusRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;
import edu.byu.cs.tweeter.model.net.response.PostStatusResponse;

public interface IFeedDAO {
    GetFeedResponse getFeed(GetFeedRequest request);
    PostStatusResponse postFeed(PostStatusRequest request);


}
