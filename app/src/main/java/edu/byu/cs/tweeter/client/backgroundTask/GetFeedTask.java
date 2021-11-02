package edu.byu.cs.tweeter.client.backgroundTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.util.Pair;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.GetFeedRequest;
import edu.byu.cs.tweeter.model.net.request.GetFollowersRequest;
import edu.byu.cs.tweeter.model.net.response.GetFeedResponse;


/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedTask<Status> {
    private static final String LOG_TAG = "GetFeedTask";
    static final String URL_PATH = "/getfeed";

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected boolean runTask() throws IOException, TweeterRemoteException {
        GetFeedRequest request = new GetFeedRequest(authToken, targetUser.getAlias(), limit, lastItem);
        GetFeedResponse response = Cache.getInstance().getServerFacade().getFeed(request, URL_PATH);
        boolean success = response.isSuccess();
        if (success) {
            this.items = response.getFeed();
            this.hasMorePages = response.getHasMorePages();
            for (Status s : items) {
                BackgroundTaskUtils.loadImage(s.getUser());
            }
        }
        return success;
    }
}
